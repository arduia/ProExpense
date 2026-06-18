plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.roborazzi) apply false
}

tasks.register("verifyAll") {
    group = "verification"
    description = "Build devDebug, run all unit tests, and verify Roborazzi screenshots."
    dependsOn(
        ":app:assembleDevDebug",
        ":app:verifyRoborazziDevDebug",
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
