import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
}

val apiProfile = rootProject.file("api.properties")
val apiProperties = Properties().apply {
    load(FileInputStream(apiProfile))
}

android {
    compileSdk = rootProject.extra["compileSdk"] as Int
    buildToolsVersion = rootProject.extra["buildToolVersion"] as String

    defaultConfig {
        applicationId = "com.arduia.expense"
        minSdk = rootProject.extra["minSdk"] as Int
        targetSdk = rootProject.extra["targetSdk"] as Int
        versionCode = 12
        versionName = "1.0.0-beta06"

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
    implementation(project(":backup"))
    implementation(project(":currency-store"))
    implementation(project(":expense-backup"))

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
    val roomVersion = "2.5.0"
    val pagingVersion = "2.1.2"
    val recyclerVersion = "1.1.0"
    val recyclerSelectionView = "1.1.0-rc03"
    val timberVersion = "4.7.1"
    val coroutinesVersion = "1.3.7"
    val workVersion = "2.8.0"
    val epoxy = "4.3.1"
    val gsonVersion = "2.8.6"
    val retrofitVersion = "2.9.0"

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlin_version"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    implementation(project(":week-expense-graph"))
    implementation(project(":shared"))
    implementation("androidx.core:core-ktx:$androidxVersion")
    implementation("androidx.appcompat:appcompat:$appCompatVersion")
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
    implementation("com.google.android.material:material:$materialVersion")
    implementation("com.mikhaellopez:circularimageview:4.3.1")
    implementation("com.google.dagger:hilt-android:$diggerHiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$diggerHiltVersion")
    implementation("androidx.hilt:hilt-common:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.preference:preference-ktx:$preferenceVersion")
    implementation("androidx.paging:paging-runtime-ktx:$pagingVersion")
    implementation("com.jakewharton.timber:timber:$timberVersion")
    implementation("com.github.arduia:mvvm-core:0.0.3")
    implementation("androidx.work:work-runtime:$workVersion")
    implementation("androidx.work:work-runtime-ktx:$workVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("androidx.hilt:hilt-work:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.github.tfcporciuncula.flow-preferences:flow-preferences:1.3.3")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.9.1")
    implementation("com.github.skydoves:progressview:1.1.0")
    implementation("com.airbnb.android:epoxy:$epoxy")
    kapt("com.airbnb.android:epoxy-processor:$epoxy")

    configurations.all {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-parcelize-runtime")
    }
} 