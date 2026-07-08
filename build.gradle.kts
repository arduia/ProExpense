plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.roborazzi) apply false
}

tasks.register("verifyAll") {
    group = "verification"
    description = "Build devDebug APK and verify design system screenshots."
    dependsOn(
        "checkIosTargets",
        ":app:assembleDevDebug",
        ":app:testDevDebugUnitTest",
        ":shared:testDebugUnitTest",
        ":core:domain:testDebugUnitTest",
        ":core:storage:testDebugUnitTest",
        ":feature:logging:testDebugUnitTest",
        ":feature:currency:testDebugUnitTest",
        ":feature:history:testDebugUnitTest",
        ":feature:auth:testDebugUnitTest",
        ":feature:sharedcost:testDebugUnitTest",
    )
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
