import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.navigation.safe.args)
    alias(libs.plugins.google.service.plugin)
    alias(libs.plugins.firebase.analytics)
    alias(libs.plugins.roborazzi)
}

val apiProfile = rootProject.file("api.properties")
val apiProperties = Properties().apply {
    load(FileInputStream(apiProfile))
}

android {
    namespace = "com.arduia.expense"
    compileSdk = libs.versions.compileSdk.get().toInt()

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }

    defaultConfig {
        applicationId = "com.arduia.expense"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 14
        versionName = "1.0.0-beta08"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("String", "BASE_URL", apiProperties["main_url"] as String)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationId = "com.arduia.expense.dev"
        }
        create("production") {
            dimension = "environment"
            applicationId = "com.arduia.expense"
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Core modules (data + UI + domain)
    implementation(project(":core-model"))
    implementation(project(":core-domain"))
    implementation(project(":core-data"))
    implementation(project(":core-ui"))

    // Feature modules
    implementation(project(":feature-splash"))
    implementation(project(":feature-onboarding"))
    implementation(project(":feature-home"))
    implementation(project(":feature-entry"))
    implementation(project(":feature-expenselogs"))
    implementation(project(":feature-statistics"))
    implementation(project(":feature-backup"))
    implementation(project(":feature-settings"))
    implementation(project(":feature-feedback"))
    implementation(project(":feature-about"))
    implementation(project(":feature-web"))

    // App-level only: Hilt entry point (@HiltAndroidApp)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Firebase (app-level; google-services plugin must be in :app)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.remote.config)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.firestore)

    // LeakCanary (debug only, app-level)
    debugImplementation(libs.leakcanary.android)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Unit tests
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.test.runner)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.androidx.test.ext)
    testImplementation(libs.espresso.core)
    testImplementation(libs.fragment.testing)
    testImplementation(libs.navigation.testing)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.android)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compiler)

    // Instrumented tests
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.navigation.testing)
    debugImplementation(libs.fragment.testing)
    androidTestImplementation(libs.fragment.testing)
    androidTestImplementation(libs.mockk)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.espresso.intents)
    androidTestImplementation(libs.espresso.contrib)
    androidTestImplementation(libs.uiautomator)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    configurations.all {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-parcelize-runtime")
    }
}
