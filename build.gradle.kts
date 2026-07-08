plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.roborazzi) apply false
}

val kmpModulesWithIosTargets = listOf(
    ":shared",
    ":core:domain",
    ":core:data",
    ":core:storage",
    ":feature:auth",
    ":feature:categories",
    ":feature:currency",
    ":feature:debt",
    ":feature:eventbudget",
    ":feature:history",
    ":feature:importexport",
    ":feature:logging",
    ":feature:onboarding",
    ":feature:reports",
    ":feature:sharedcost",
)

tasks.register("verifyIos") {
    group = "verification"
    description = "Compile every KMP module's commonMain for iosSimulatorArm64 — proves the data " +
        "layer (and everything built on it) stays portable to iOS without running Xcode."
    dependsOn(kmpModulesWithIosTargets.map { "$it:compileKotlinIosSimulatorArm64" })
}

tasks.register("verifyAll") {
    group = "verification"
    description = "Build devDebug APK, verify design system screenshots, and compile for iOS."
    dependsOn(
        ":app:assembleDevDebug",
        ":app:testDevDebugUnitTest",
        ":shared:testDebugUnitTest",
        ":core:domain:testDebugUnitTest",
        ":core:storage:testDebugUnitTest",
        ":feature:auth:testDebugUnitTest",
        ":feature:logging:testDebugUnitTest",
        ":feature:currency:testDebugUnitTest",
        ":feature:history:testDebugUnitTest",
        "verifyIos",
    )
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}
