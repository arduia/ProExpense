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
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project(":core:data"))
            implementation(project(":shared"))
            implementation(libs.coroutines.core)
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
            implementation(libs.sqldelight.coroutines)
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
        // NOTE: an `iosMain` stub (DatabaseDriverFactory.ios.kt) is checked in for iOS readiness
        // but no iOS target is configured yet, so it is intentionally not compiled. Add the iOS
        // targets + native SQLDelight driver here when the iosApp phase starts.
    }
}

android {
    namespace = "com.arduia.expense.storage"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
