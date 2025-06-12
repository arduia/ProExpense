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
    buildToolsVersion = libs.versions.buildToolVersion.get()

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
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.6"
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.espresso.core)

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

    val fragmentVersion = "1.2.5"
    val androidxVersion = "1.3.0"
    val preferenceVersion = "1.1.1"
    val navigationVersion = "2.3.2"
    val appCompatVersion = "1.1.0"
    val constraintVersion = "2.0.0-beta7"
    val lifecycleVersion = "2.5.0"
    val junitVersion = "4.13.1"
    val androidxTestingVersion = "1.1.1"
    val espressoVersion = "3.2.0"
    val materialVersion = "1.2.0-beta01"
    val diggerHiltVersion = "2.42"
    val pagingVersion = "2.1.2"
    val recyclerVersion = "1.1.0"
    val recyclerSelectionView = "1.1.0-rc03"
    val timberVersion = "4.7.1"
    val coroutinesVersion = "1.3.7"
    val workVersion = "2.8.0"
    val epoxy = "4.3.1"
    val gsonVersion = "2.8.6"
    val retrofitVersion = "2.9.0"

    implementation("androidx.core:core-ktx:$androidxVersion")
    implementation("androidx.appcompat:appcompat:$appCompatVersion")
    implementation("com.google.android.material:material:$materialVersion")
    implementation("androidx.constraintlayout:constraintlayout:$constraintVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-core-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")
    implementation("androidx.recyclerview:recyclerview:$recyclerVersion")
    implementation("androidx.recyclerview:recyclerview-selection:$recyclerSelectionView")
    testImplementation("junit:junit:$junitVersion")
    androidTestImplementation("androidx.test.ext:junit:$androidxTestingVersion")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
    implementation("androidx.preference:preference-ktx:$preferenceVersion")
    implementation("androidx.paging:paging-runtime-ktx:$pagingVersion")
    implementation("com.jakewharton.timber:timber:$timberVersion")
    implementation("com.github.arduia:mvvm-core:0.0.3")
    implementation("androidx.work:work-runtime:$workVersion")
    implementation("androidx.work:work-runtime-ktx:$workVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.github.tfcporciuncula.flow-preferences:flow-preferences:1.3.3")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.9.1")
    implementation("com.github.skydoves:progressview:1.1.0")
    implementation("com.airbnb.android:epoxy:$epoxy")
    kapt("com.airbnb.android:epoxy-processor:$epoxy")

    // Hilt WorkManager
    implementation(libs.hilt.work)
    kapt(libs.hilt.work.compiler)

    configurations.all {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-parcelize-runtime")
    }
} 