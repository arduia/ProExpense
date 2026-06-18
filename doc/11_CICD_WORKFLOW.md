# CI/CD & Development Workflow - ProExpense Best Practices

## Overview

ProExpense uses **CircleCI** for automated builds, testing, and deployment. Every commit goes through a validation pipeline before deployment.

---

## CircleCI Pipeline

**File:** `/.circleci/config.yml`

```yaml
version: 2.1

orbs:
  android: circleci/android@0.2.1

workflows:
  build_and_test:
    jobs:
      - build_and_test

jobs:
  build_and_test:
    executor: android/android
    steps:
      - checkout
      - android/accept-licenses
      - run:
          name: Build Debug APK
          command: ./gradlew assembleDebug
      - run:
          name: Run Unit Tests
          command: ./gradlew testDebugUnitTest
      - run:
          name: Run Instrumented Tests
          command: ./gradlew connectedAndroidTest
      - store_test_results:
          path: ~/project/app/build/test-results
      - store_artifacts:
          path: ~/project/app/build/outputs/apk
```

### Pipeline Stages

1. **Checkout**: Get latest code
2. **Build**: Compile APK/AAB
3. **Unit Tests**: Fast tests with mocks
4. **Instrumented Tests**: Real device/emulator tests
5. **Store Results**: Save artifacts for review

---

## Version Management

### Semantic Versioning

ProExpense follows **MAJOR.MINOR.PATCH-QUALIFIER**:

```gradle
// In build.gradle.kts
android {
    defaultConfig {
        versionCode = 14                    // Build number (incremented)
        versionName = "1.0.0-beta08"        // User-facing version
    }
}
```

**Pattern:**
- `1.0.0-beta01`: Beta release
- `1.0.0-rc01`: Release candidate
- `1.0.0`: Final release
- `1.0.1`: Bug fix release
- `1.1.0`: Feature release
- `2.0.0`: Major release

### Incrementing Versions

```kotlin
// In CI/CD, increment automatically
val currentVersion = readVersionName()  // "1.0.0-beta08"
val nextVersion = incrementPatch(currentVersion)  // "1.0.0-beta09"

// Update build.gradle.kts
updateVersionName(nextVersion)
updateVersionCode(currentVersionCode + 1)

// Commit with tag
git tag -a v1.0.0-beta09 -m "Release 1.0.0-beta09"
git push origin v1.0.0-beta09
```

---

## Build Variants

### Debug vs Release

**In build.gradle.kts:**
```kotlin
android {
    buildTypes {
        debug {
            debuggable = true
            minifyEnabled = false  // No obfuscation in debug
            signingConfig = signingConfigs.debug
            buildConfigField("String", "API_BASE_URL", "\"https://dev.api.example.com\"")
        }
        
        release {
            debuggable = false
            minifyEnabled = true  // Obfuscate in release
            shrinkResources = true  // Remove unused resources
            signingConfig = signingConfigs.release
            buildConfigField("String", "API_BASE_URL", "\"https://api.example.com\"")
            
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

### Dev Flavor (for Testing)

```kotlin
android {
    flavorDimensions.add("environment")
    
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "API_BASE_URL", "\"https://dev.api.example.com\"")
        }
        
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "API_BASE_URL", "\"https://api.example.com\"")
        }
    }
}
```

**Build variants:**
- `devDebug` - Dev API, debug build
- `devRelease` - Dev API, release build
- `prodDebug` - Production API, debug build
- `prodRelease` - Production API, release build

---

## Dependency Management

### Gradle Version Catalog

**File:** `gradle/libs.versions.toml`

```toml
[versions]
compileSdk = "34"
minSdk = "21"
targetSdk = "34"
android_gradle_plugin = "8.0.2"
kotlin = "1.8.10"
hilt = "2.57"
room = "2.7.2"
retrofit = "2.9.0"
compose = "2025.08.00"

[libraries]
android_gradle_plugin = { module = "com.android.tools.build:gradle", version.ref = "android_gradle_plugin" }
kotlin_gradle_plugin = { module = "org.jetbrains.kotlin:kotlin-gradle-plugin", version.ref = "kotlin" }

androidx_appcompat = "androidx.appcompat:appcompat:1.6.1"
androidx_core_ktx = "androidx.core:core-ktx:1.10.1"
androidx_lifecycle_viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1"

hilt_android = { module = "com.google.dagger:hilt-android", version.ref = "hilt" }
hilt_compiler = { module = "com.google.dagger:hilt-compiler", version.ref = "hilt" }

room_runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
room_compiler = { module = "androidx.room:room-compiler", version.ref = "room" }

retrofit = { module = "com.squareup.retrofit2:retrofit", version.ref = "retrofit" }
retrofit_gson = { module = "com.squareup.retrofit2:converter-gson", version.ref = "retrofit" }

compose_bom = "androidx.compose:compose-bom:2025.08.00"
compose_ui = { module = "androidx.compose.ui:ui" }
compose_material3 = { module = "androidx.compose.material3:material3" }

timber = "com.jakewharton.timber:timber:4.7.1"

[bundles]
androidx = [
    "androidx_appcompat",
    "androidx_core_ktx",
    "androidx_lifecycle_viewmodel"
]

room = [
    "room_runtime"
]

hilt = [
    "hilt_android"
]

retrofit = [
    "retrofit",
    "retrofit_gson"
]

compose = [
    "compose_ui",
    "compose_material3"
]
```

**Usage in build.gradle.kts:**
```kotlin
dependencies {
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.room)
    implementation(libs.bundles.hilt)
    implementation(libs.bundles.retrofit)
    
    kapt(libs.hilt.compiler)
    kapt(libs.room.compiler)
}
```

**Benefits:**
- Single source of truth for versions
- Easy to update all dependencies
- Transitive dependency management

---

## KSP Migration (from KAPT)

Modern Kotlin uses **KSP** (Kotlin Symbol Processing) instead of KAPT for faster builds:

**Before (KAPT):**
```kotlin
plugins {
    kotlin("kapt")
}

dependencies {
    kapt("com.google.dagger:hilt-compiler:2.57")
}
```

**After (KSP):**
```kotlin
plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    ksp("com.google.dagger:hilt-compiler:2.57")
}
```

**KSP is ~2x faster** than KAPT:
- KAPT: ~30 seconds
- KSP: ~15 seconds

---

## Testing in CI/CD

### Unit Tests

```bash
./gradlew testDebugUnitTest
```

Runs all unit tests with fast test dispatcher (Robolectric).

### Instrumented Tests

```bash
./gradlew connectedAndroidTest
```

Requires Android emulator or physical device.

### Code Coverage

```bash
./gradlew jacocoTestReport
```

Generates code coverage report:
```bash
./gradlew jacocoTestReport
open app/build/reports/jacoco/jacocoTestReport/html/index.html
```

### Lint Analysis

```bash
./gradlew lint
```

Checks for:
- Security issues
- Performance problems
- API compatibility
- Code style violations

---

## Deployment Strategy

### Google Play Release

**Manual Steps:**
1. Update version in `build.gradle.kts`
2. Build release APK: `./gradlew assembleRelease`
3. Sign APK with release key
4. Upload to Google Play Console

**Automated via Fastlane (Optional):**
```bash
# Install fastlane
sudo gem install fastlane

# Initialize
fastlane init

# Upload to play store
fastlane android deploy
```

### Versioning on Release

```kotlin
// Before release
versionName = "1.0.0-beta08"
versionCode = 14

// On release
versionName = "1.0.0"
versionCode = 15

// Post-release (start next development)
versionName = "1.0.1-dev"
versionCode = 16
```

---

## Git Workflow

### Branch Naming

```
feature/add-expense-export     - New feature
bugfix/expense-deletion-crash  - Bug fix
refactor/di-setup              - Refactoring
docs/architecture-guide        - Documentation
```

### Commit Message Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `refactor`: Code restructuring
- `docs`: Documentation
- `test`: Test additions/updates
- `ci`: CI/CD changes
- `chore`: Dependency updates

**Examples:**
```
feat(expense): add recurring expense support

Allow users to mark expenses as recurring with frequency options.

Implements #125
```

```
fix(ui): prevent duplicate expense entries on network retry

Add deduplication logic to expense repository.

Fixes #456
```

### Pull Request Workflow

1. **Create branch** from `main`
2. **Make commits** with descriptive messages
3. **Push to remote**
4. **Create PR** with title and description
5. **CI runs tests** automatically
6. **Code review** by team
7. **Merge** when approved and CI passes

---

## Performance Optimization

### Build Performance

**Measure build time:**
```bash
./gradlew build --profile

# View report
open build/reports/profile/profile-[timestamp].html
```

**Optimize:**
- Parallel builds: `org.gradle.parallel=true`
- Build cache: `org.gradle.caching=true`
- KSP over KAPT (2x faster)
- Avoid buildConfigField where possible

### App Size Optimization

**Enable ProGuard + resource shrinking:**
```kotlin
release {
    minifyEnabled = true
    shrinkResources = true
    proguardFiles(...)
}
```

**Check APK size:**
```bash
./gradlew bundleRelease

# Analyze in Android Studio
Analyze → Analyze APK → app/build/outputs/bundle/release/app-release.aab
```

---

## Best Practices

### ✅ DO:

1. **Run tests locally** before pushing
   ```bash
   ./gradlew test
   ```

2. **Use version catalog** for dependencies
   ```toml
   # Single source of truth
   ```

3. **Increment versions** semantically
   ```
   1.0.0-beta08 → 1.0.0
   ```

4. **Obfuscate release builds**
   ```kotlin
   minifyEnabled = true  // in release
   ```

5. **Use KSP** over KAPT
   ```kotlin
   ksp()  // 2x faster
   ```

### ❌ DON'T:

1. **Push without testing**
   ```bash
   # Bad - don't do this
   git push
   
   # Good
   ./gradlew test && git push
   ```

2. **Hardcode versions**
   ```gradle
   // Bad
   implementation "com.google.dagger:hilt-android:2.57"
   
   // Good
   implementation libs.hilt.android
   ```

3. **Disable ProGuard in release**
   ```kotlin
   // Bad - leaves code readable
   minifyEnabled = false
   
   // Good
   minifyEnabled = true
   ```

4. **Skip instrumented tests**
   ```bash
   # Bad
   ./gradlew build -x connectedAndroidTest
   
   # Good
   ./gradlew build  # Runs all tests
   ```

---

## Reuse in New Architecture

✅ **CI/CD pipeline** remains unchanged with new design  
✅ **Versioning strategy** applies regardless of architecture  
✅ **Build optimization** techniques are framework-agnostic  
✅ **Dependency management** with version catalog carries forward  
✅ **Testing in CI** workflow remains valid for refactored code

**Key: Automated testing catches regressions early**
