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
        }
        androidUnitTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.test)
            // In-memory JDBC driver runs the real generated schema on the JVM, so repository
            // tests exercise actual SQL + mappers without an emulator or SQLCipher.
            implementation(libs.sqldelight.sqlite.driver)
        }
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
