plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
}

val verifyAll = tasks.register("verifyAll") {
    group = "verification"
    description = "Build devDebug APK and verify design system screenshots."
    dependsOn(
        // checkIosTargets isn't listed here directly — verifyIosCompat already depends on it.
        "verifyIosCompat",
        ":app:assembleDevDebug",
        ":app:testDevDebugUnitTest",
        ":app:testDevReleaseUnitTest",
        ":shared:testDebugUnitTest",
        ":core:domain:testDebugUnitTest",
        ":core:storage:testDebugUnitTest",
        ":feature:logging:testDebugUnitTest",
        ":feature:currency:testDebugUnitTest",
        ":feature:history:testDebugUnitTest",
        ":feature:auth:testDebugUnitTest",
        ":feature:sharedcost:testDebugUnitTest",
        ":konsist-test:test",
    )
}

// Static analysis — ktlint (formatting) + detekt (code smells), applied uniformly to every
// subproject. Existing findings are captured in committed baselines (detekt-baseline.xml / lint
// baselines) so verifyAll only fails on new violations, not a repo-wide cleanup; ktlint issues are
// auto-fixed via `ktlintFormat` instead since the formatter itself resolves them safely.
val generateDetektBaselines = tasks.register("generateDetektBaselines") {
    group = "verification"
    description = "Regenerate every module's detekt baseline (main-variant only — see subprojects block)."
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    extensions.configure(org.jlleitschuh.gradle.ktlint.KtlintExtension::class.java) {
        android.set(true)
    }

    extensions.configure(io.gitlab.arturbosch.detekt.extensions.DetektExtension::class.java) {
        buildUponDefaultConfig = true
        config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    }

    // The extension's single `baseline` path is only auto-suffixed per Android build-type/flavor
    // (detekt-baseline-debug.xml, ...) — the KMP "metadata" source-set tasks (commonMain, iosMain,
    // appleMain, nativeMain, main) all share that same unsuffixed path and silently overwrite each
    // other's baseline. Give every variant its own file instead, keyed by a name derived identically
    // from both the check task ("detektAndroidDebug") and its baseline task
    // ("detektBaselineAndroidDebug") so the two agree on the same path.
    fun baselineFileFor(variant: String) =
        file(if (variant.isEmpty()) "$projectDir/detekt-baseline.xml" else "$projectDir/detekt-baseline-$variant.xml")

    // Scope detekt to production/main source only — every *Test-suffixed variant task
    // (detektAndroidDebugUnitTest, detektAndroidDebugAndroidTest, detektIosArm64Test,
    // detektMetadataCommonTest, ...) resolves against source that the generated detekt.yml already
    // excludes from nearly every rule (see the `excludes: ['**/test/**', ...]` entries), and their
    // DetektCreateBaselineTask counterparts silently no-op without ever writing a baseline file
    // (confirmed empirically — the task reports success but produces no output). Test code quality
    // is covered by AGENTS.md's TDD/backbone rules, not detekt smells.
    fun isMainVariant(taskName: String) = !taskName.endsWith("Test")

    // Only wire a baseline into the check task when one was actually generated — detekt's baseline
    // writer skips creating a file at all when a variant has zero findings to whitelist (confirmed
    // empirically: several clean main-variant modules never got a baseline file even after a forced
    // --rerun-tasks regeneration). Declaring a `baseline` path that doesn't exist fails Gradle's own
    // input-file validation, so only set it when the file is present; a variant with no baseline just
    // runs unsuppressed, which is correct when there's nothing to suppress.
    tasks.withType(io.gitlab.arturbosch.detekt.Detekt::class.java).configureEach {
        val baselineFile = baselineFileFor(name.removePrefix("detekt"))
        if (baselineFile.exists()) baseline.set(baselineFile)
    }
    tasks.withType(io.gitlab.arturbosch.detekt.DetektCreateBaselineTask::class.java).configureEach {
        baseline.set(baselineFileFor(name.removePrefix("detektBaseline")))
    }

    // The plain "detekt" umbrella task is a no-op (NO-SOURCE) for KMP modules — depend on every
    // actual main-variant Detekt task instance instead (detektMetadataCommonMain, detektAndroidDebug,
    // detektIosArm64Main, ...) so nothing silently goes unanalyzed.
    val modulePath = path
    val detektTasks = tasks.withType(io.gitlab.arturbosch.detekt.Detekt::class.java).matching { isMainVariant(it.name) }
    verifyAll.configure {
        dependsOn("$modulePath:ktlintCheck")
        dependsOn(detektTasks)
    }
    generateDetektBaselines.configure {
        dependsOn(tasks.withType(io.gitlab.arturbosch.detekt.DetektCreateBaselineTask::class.java).matching { isMainVariant(it.name) })
    }

    // Android Lint — same baseline-onboarding approach as detekt. AGP exposes one variant-agnostic
    // `lint` / `updateLintBaseline` task pair per module regardless of flavors, so no per-variant
    // task-name juggling is needed here (unlike detekt).
    fun configureLint(lint: com.android.build.api.dsl.Lint) {
        lint.baseline = file("$projectDir/lint-baseline.xml")
        lint.abortOnError = true
    }
    pluginManager.withPlugin("com.android.library") {
        extensions.configure(com.android.build.api.dsl.LibraryExtension::class.java) { configureLint(lint) }
        verifyAll.configure { dependsOn("$modulePath:lint") }
    }
    pluginManager.withPlugin("com.android.application") {
        extensions.configure(com.android.build.api.dsl.ApplicationExtension::class.java) { configureLint(lint) }
        verifyAll.configure { dependsOn("$modulePath:lint") }
    }
}

// iOS compatibility gates — see AGENTS.md "iOS Compatibility" and docs/ios_compatibility_plan.md.
val requiredIosTargets = setOf("iosArm64", "iosSimulatorArm64")

tasks.register("checkIosTargets") {
    group = "verification"
    description = "Fail if any KMP module does not declare the required iOS targets ($requiredIosTargets)."
    val offenders = provider {
        subprojects
            .filter { it.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") }
            .mapNotNull { project ->
                val kmp = project.extensions
                    .findByType(org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension::class.java)
                val declared = kmp?.targets?.names ?: emptySet<String>()
                val missing = requiredIosTargets - declared
                if (missing.isEmpty()) null else "${project.path} (missing: ${missing.joinToString()})"
            }
    }
    doLast {
        val bad = offenders.get()
        if (bad.isNotEmpty()) {
            throw GradleException(
                "Every KMP module must declare iOS targets — add iosArm64() and iosSimulatorArm64():\n" +
                    bad.joinToString("\n") { "  $it" }
            )
        }
    }
}

val verifyIosCompat = tasks.register("verifyIosCompat") {
    group = "verification"
    description = "Cross-compile iOS klibs for every KMP module (compile-time iOS compatibility check; no macOS needed)."
    dependsOn("checkIosTargets")
}

subprojects {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        val modulePath = path
        verifyIosCompat.configure {
            dependsOn("$modulePath:compileKotlinIosArm64", "$modulePath:compileKotlinIosSimulatorArm64")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}
