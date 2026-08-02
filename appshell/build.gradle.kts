import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // Umbrella framework for the SwiftUI shell. Only the link* tasks need macOS; configuring the
    // binary here is inert on Linux, so verifyIosCompat still covers this module's klibs.
    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ProExpenseKit"
            isStatic = true
            export(project(":core:domain"))
            export(project(":core:data"))
            export(project(":shared"))
            export(project(":feature:logging"))
            export(project(":feature:history"))
            export(project(":feature:currency"))
            export(project(":feature:onboarding"))
            export(project(":feature:auth"))
        }
    }

    sourceSets {
        commonMain.dependencies {
            // `api` (not `implementation`) is required for the framework `export`s above to resolve.
            api(project(":core:domain"))
            api(project(":core:data"))
            api(project(":shared"))
            api(project(":feature:logging"))
            api(project(":feature:history"))
            api(project(":feature:currency"))
            api(project(":feature:onboarding"))
            api(project(":feature:auth"))
            // Not exported to Swift (the slice's SwiftUI screens don't name their types yet), but
            // needed so the iOS Koin graph resolves the same bindings the Android shell does.
            implementation(project(":feature:categories"))
            implementation(project(":feature:eventbudget"))
            implementation(project(":feature:reports"))
            implementation(project(":feature:debt"))
            implementation(project(":feature:sharedcost"))
            implementation(project(":feature:importexport"))
            implementation(libs.coroutines.core)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.test)
        }
        iosMain.dependencies {
            implementation(project(":core:storage"))
        }
    }
}

android {
    namespace = "com.arduia.expense.shell"
    compileSdk =
        libs.versions.compileSdk
            .get()
            .toInt()

    defaultConfig {
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
