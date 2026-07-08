import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.roborazzi)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    iosArm64()
    iosSimulatorArm64()

    // This module's Compose UI lives entirely in androidMain (Jetpack Compose, not Compose
    // Multiplatform) — the Compose Compiler plugin still registers itself for every compilation
    // in the project, including iOS ones with no Composable code, where it fails hard because no
    // Compose runtime is on that classpath. Drop the plugin from the iOS compiler-plugin
    // classpaths so those compilations skip Compose IR generation entirely.
    project.afterEvaluate {
        configurations.matching { it.name.startsWith("kotlinCompilerPluginClasspathIos") }
            .configureEach {
                exclude(group = "org.jetbrains.kotlin", module = "kotlin-compose-compiler-plugin-embeddable")
            }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.test)
        }
        androidMain.dependencies {
            api(project.dependencies.platform(libs.compose.bom))
            api(libs.compose.ui)
            api(libs.compose.foundation)
            api(libs.compose.material3)
            api(libs.compose.ui.tooling.preview)
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(libs.robolectric)
                implementation(libs.androidx.test.core)
                implementation(libs.androidx.test.ext.junit)
                implementation(libs.compose.ui.test.junit4)
                implementation(libs.compose.ui.test.manifest)
                implementation(libs.roborazzi)
                implementation(libs.roborazzi.compose)
                implementation(libs.roborazzi.junit.rule)
            }
        }
    }
}

android {
    namespace = "com.arduia.expense.shared"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.systemProperties["robolectric.pixelCopyRenderMode"] = "hardware"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

roborazzi {
    outputDir.set(file("src/test/screenshots"))
}
