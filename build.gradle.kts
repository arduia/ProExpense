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

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}
