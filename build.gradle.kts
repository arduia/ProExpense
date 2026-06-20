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
    description = "Build devDebug APK for the scaffold shell."
    dependsOn(":app:assembleDevDebug")
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}
