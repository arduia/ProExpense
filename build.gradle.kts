plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.roborazzi) apply false
}

tasks.register<Exec>("verifyDesignAlignment") {
    group = "verification"
    description = "Check Roborazzi baselines against design_handoff_v2 reference images (Step 5.5)."
    commandLine("bash", "scripts/verify-design-alignment.sh")
}

tasks.register("verifyAll") {
    group = "verification"
    description = "Build devDebug, run all unit tests, and verify Roborazzi screenshots."
    dependsOn(
        ":app:assembleDevDebug",
        ":app:verifyRoborazziDevDebug",
        "verifyDesignAlignment",
    )
}

subprojects {
    tasks.matching { it.name == "test" }.configureEach {
        rootProject.tasks.named("verifyAll").configure {
            dependsOn(this@configureEach)
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}
