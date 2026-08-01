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

    // The iOS framework Swift links against. Linking itself needs macOS/Xcode (Phase 3) — on Linux
    // this only registers the link tasks; `verifyIosCompat` still proves the klibs compile.
    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "ProExpenseKit"
            isStatic = true
            // Only `api` dependencies can be exported — Swift needs the domain models, the Result
            // wrapper, the shared formatters and the logging screen state to be visible by name.
            export(project(":core:domain"))
            export(project(":core:data"))
            export(project(":shared"))
            export(project(":feature:logging"))
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core:domain"))
            api(project(":core:data"))
            api(project(":shared"))
            api(project(":feature:logging"))
            implementation(project(":core:storage"))
            implementation(project(":feature:categories"))
            implementation(project(":feature:currency"))
            implementation(project(":feature:debt"))
            implementation(project(":feature:eventbudget"))
            implementation(project(":feature:history"))
            implementation(project(":feature:importexport"))
            implementation(project(":feature:onboarding"))
            implementation(project(":feature:reports"))
            implementation(project(":feature:sharedcost"))
            implementation(libs.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.test)
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
