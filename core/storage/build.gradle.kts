import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.sqldelight)
}

sqldelight {
    databases {
        create("ProExpenseDatabase") {
            packageName.set("com.arduia.expense.storage.db")
        }
        // Standalone schema for the monthly Drive sync file format (US-SYNC-3) — independent of
        // ProExpenseDatabase; this is purely the on-the-wire file layout, not a second copy of
        // local storage. Lives in its own source directory so SQLDelight doesn't merge the two
        // schemas' tables.
        create("SyncPayloadDatabase") {
            packageName.set("com.arduia.expense.storage.syncpayload.db")
            srcDirs.setFrom("src/commonMain/sqldelight-syncpayload")
        }
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project(":core:data"))
            implementation(project(":shared"))
            implementation(libs.coroutines.core)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.kotlinx.datetime)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.test)
        }
        androidMain.dependencies {
            implementation(libs.coroutines.core)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.androidx.sqlite)
            implementation(libs.androidx.sqlite.framework)
            implementation(libs.sqlcipher.android)
            implementation(libs.androidx.security.crypto)
            implementation(libs.koin.android)
        }
        androidUnitTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.test)
            // Pure-JVM driver: exercises real SQL + mappers without SQLCipher/Android.
            implementation(libs.sqldelight.sqlite.driver)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
    }
}

android {
    namespace = "com.arduia.expense.storage"
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
