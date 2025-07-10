import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.navigation.safe.args)
}

val apiProfile = rootProject.file("api.properties")
val apiProperties = Properties().apply {
    load(FileInputStream(apiProfile))
}

android {
    namespace = "com.arduia.expense"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.arduia.expense"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
    }

    bundle {
        language {
            enableSplit = false
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            buildConfigField("String", "BASE_URL", apiProperties["main_url"] as String)
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("String", "BASE_URL", apiProperties["main_url"] as String)
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    kapt {
        correctErrorTypes = false
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    
    // Android Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    
    // Compose BOM
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Core Compose libraries
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.compose.animation)
    
    // Activity and Navigation
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    
    // Hilt integration
    implementation(libs.hilt.navigation.compose)
    
    // Tooling (debug only)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.test.runner)
    testImplementation(libs.androidx.arch.core.testing)
    
    // Fragment Testing
    debugImplementation(libs.fragment.testing)
    testImplementation(libs.fragment.testing)
    testImplementation(libs.navigation.testing)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.android)
    
    // Hilt Testing
    testImplementation(libs.hilt.android.testing)
    kaptTest(libs.hilt.compiler)
    
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.livedata.core.ktx)
    implementation(libs.lifecycle.reactivestreams.ktx)

    // Navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // Epoxy
    implementation(libs.epoxy)
    kapt(libs.epoxy.processor)

    // Timber
    implementation(libs.timber)

    // Project modules
    implementation(project(":expense-backup"))
    implementation(project(":currency-store"))
    implementation(project(":backup"))
    implementation(project(":shared"))
    implementation(project(":week-expense-graph"))

    // Fragment
    implementation(libs.fragment.ktx)

    // RecyclerView
    implementation(libs.recyclerview)
    implementation(libs.recyclerview.selection)

    // Preference
    implementation(libs.preference.ktx)

    // Paging
    implementation(libs.paging.runtime.ktx)

    // Work
    implementation(libs.work.runtime)
    implementation(libs.work.runtime.ktx)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Other
    implementation(libs.mvvm.core)
    implementation(libs.flow.preferences)
    debugImplementation(libs.leakcanary.android)
    implementation(libs.progressview)

    // Hilt WorkManager
    implementation(libs.hilt.work)
    kapt(libs.hilt.work.compiler)

    configurations.all {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-parcelize-runtime")
    }
} 