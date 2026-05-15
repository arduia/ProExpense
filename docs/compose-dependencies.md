# Compose Migration — Dependency Analysis

This document audits every current dependency against what the Jetpack Compose migration requires. It covers what is already ready, what must be added, what has bugs in the current setup, what must be version-bumped, and what can be removed after migration is complete.

---

## 1. Current State — Compose Dependencies

These are already declared in `app/build.gradle.kts` and are sufficient as-is.

| Dependency | Version (via BOM) | Purpose | Status |
|---|---|---|---|
| Compose BOM | `2025.08.00` | Manages all `androidx.compose` versions | ✅ Ready |
| `compose-ui` | BOM-managed | Core UI runtime | ✅ Ready |
| `compose-material3` | BOM-managed | Material Design 3 components | ✅ Ready |
| `compose-foundation` | BOM-managed | Layout, gestures, drawing primitives | ✅ Ready |
| `compose-animation` | BOM-managed | `AnimatedVisibility`, `animateContentSize`, transitions | ✅ Ready |
| `compose-ui-tooling-preview` | BOM-managed | `@Preview` annotations | ✅ Ready |
| `compose-ui-tooling` *(debug)* | BOM-managed | Layout inspector, slow composition detection | ✅ Ready |
| `compose-ui-test-manifest` *(debug)* | BOM-managed | Required for Compose UI tests | ✅ Ready |
| `compose-ui-test-junit4` *(androidTest)* | BOM-managed | Compose UI test rules | ✅ Ready |
| `activity-compose` | `1.10.1` | `ComponentActivity.setContent {}`, `rememberLauncherForActivityResult` | ✅ Ready |
| `navigation-compose` | `2.9.3` | `NavHost`, `composable {}` destinations, `rememberNavController` | ✅ Ready |
| `lifecycle-viewmodel-compose` | `2.9.2` | `viewModel()`, `hiltViewModel()` in composables | ✅ Ready |
| `hilt-navigation-compose` | `1.2.0` | `hiltViewModel()` with scoped nav back stack entries | ✅ Ready |

---

## 2. Build Configuration Bug — Must Fix Before Migration

### Problem: `composeOptions` block is redundant and version-conflicting

The `app/build.gradle.kts` contains:

```kotlin
composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get() // "1.5.15"
}
```

`1.5.15` is the old standalone Compose compiler extension version that matched **Kotlin 1.9.x**. The project now uses **Kotlin 2.2.0** with the `org.jetbrains.kotlin.plugin.compose` plugin:

```kotlin
alias(libs.plugins.compose.compiler) // org.jetbrains.kotlin.plugin.compose @ 2.2.0
```

When the `org.jetbrains.kotlin.plugin.compose` plugin is applied, it automatically selects the correct bundled Compose compiler for the active Kotlin version. The `composeOptions.kotlinCompilerExtensionVersion` override is not only unnecessary — it pins an incompatible version and will cause build failures or subtle runtime bugs.

**Fix**: Remove the `composeOptions` block entirely and remove the `compose-compiler = "1.5.15"` version entry from `libs.versions.toml`.

```kotlin
// DELETE this from app/build.gradle.kts:
composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
}
```

```toml
# DELETE this from libs.versions.toml:
compose-compiler = "1.5.15"
```

---

## 3. Missing Dependencies — Must Add for Migration

### 3.1 `lifecycle-runtime-compose` — **Critical**

**Why needed**: Provides `collectAsStateWithLifecycle()`, the lifecycle-aware StateFlow collector for Compose. Without it, every `StateFlow` in Compose must use `collectAsState()`, which does not respect the Activity/Fragment lifecycle and will collect state in the background needlessly.

All ViewModel `StateFlow` / `Flow` observations in migrated screens must use this.

**Catalog entry to add** (`libs.versions.toml`):
```toml
[libraries]
lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycle-viewmodel-compose" }
```

> Uses the same version ref as `lifecycle-viewmodel-compose` (`2.9.2`). Alternatively it is BOM-managed if a lifecycle BOM is added, but pinning to the same version is simpler here.

**`app/build.gradle.kts` addition**:
```kotlin
implementation(libs.lifecycle.runtime.compose)
```

---

### 3.2 `compose-runtime-livedata` — **Required During Incremental Migration**

**Why needed**: Several ViewModels still use `LiveData` and `EventLiveData` (from `mvvm-core`). During the incremental migration, Compose screens that consume these ViewModels need `observeAsState()` to convert `LiveData` to Compose `State`. Without it, migrated screens cannot observe any existing ViewModel.

This dependency can be removed once all ViewModels are converted to expose `StateFlow` instead of `LiveData`.

**Catalog entry to add**:
```toml
[libraries]
compose-runtime-livedata = { group = "androidx.compose.runtime", name = "runtime-livedata" }
```

> Version is BOM-managed — no version ref needed.

**`app/build.gradle.kts` addition**:
```kotlin
implementation(libs.compose.runtime.livedata)
```

---

### 3.3 `paging-compose` + Paging 3 upgrade — **Required for Expense Logs Screen**

**Why needed**: The Expense Logs screen uses `PagedList` and `LivePagedListBuilder` from **Paging 2** (`paging = "2.1.2"`). Paging 2 has no Compose integration. Migrating the Expense Logs screen requires upgrading to **Paging 3**, which provides `collectAsLazyPagingItems()` and `LazyColumn`/`LazyPagingItems` support via `paging-compose`.

**Version to add** (`libs.versions.toml`):
```toml
[versions]
paging3 = "3.3.6"

[libraries]
paging-runtime3 = { group = "androidx.paging", name = "paging-runtime-ktx", version.ref = "paging3" }
paging-compose = { group = "androidx.paging", name = "paging-compose", version.ref = "paging3" }
```

**`app/build.gradle.kts` change**:
```kotlin
// Replace:
implementation(libs.paging.runtime.ktx)          // Paging 2
// With:
implementation(libs.paging.runtime3)             // Paging 3
implementation(libs.paging.compose)              // Compose integration
```

> **Migration note**: The `ExpenseLogAdapter` and `ExpenseLogViewModel` currently use `PagedList` / `DataSource.Factory` APIs. These must be replaced with `PagingData<T>` / `Pager` / `PagingSource` when migrating the Expense Logs screen. This is the most complex data-layer change in the entire migration.

---

### 3.4 `compose-material3-window-size` — **Needed for Responsive Layouts**

Already declared in `libs.versions.toml` but **not added** to `app/build.gradle.kts`.

The existing layouts use `?dimen/width_layout_min` (450dp) constraints for tablet support. In Compose this is handled by `WindowSizeClass`.

**`app/build.gradle.kts` addition**:
```kotlin
implementation(libs.compose.material3.window.size)
```

---

### 3.5 `compose-ui-test-junit4` in unit tests — **Needed for Robolectric Compose Tests**

Currently `compose-ui-test-junit4` is only declared under `androidTestImplementation`. The project uses Robolectric for unit tests (`isIncludeAndroidResources = true`). Compose composables can be tested under Robolectric using `createComposeRule()`, which requires `compose-ui-test-junit4` under `testImplementation` as well.

**`app/build.gradle.kts` addition**:
```kotlin
testImplementation(libs.compose.ui.test.junit4)
```

---

## 4. Outdated Dependencies — Recommended Version Bumps

These are not Compose-specific blockers but should be updated to avoid compatibility issues during migration.

| Dependency | Current | Recommended | Reason |
|---|---|---|---|
| `lifecycle` | `2.5.1` | `2.9.2` (align with `lifecycle-viewmodel-compose`) | `2.5.x` is incompatible with `lifecycle-runtime-compose` 2.9.x; mismatched versions cause runtime crashes |
| `coroutines` | `1.6.4` | `1.9.0` | `collectAsStateWithLifecycle()` internally uses coroutines; newer version has performance fixes relevant to Compose |
| `fragment` | `1.2.5` | `1.8.6` | Used during interop phase (`ComposeView` inside Fragments); `1.2.5` predates several stability fixes |
| `navigation` (Fragment) | `2.5.3` | `2.9.3` (align with `navigation-compose`) | Mismatched Navigation versions during interop cause `NavController` type conflicts |

### Lifecycle version alignment — most critical

The `lifecycle-runtime-compose` at version `2.9.2` depends on `lifecycle-runtime` at the same version. Currently `lifecycle-runtime-ktx` is pinned at `2.5.1` via the `lifecycle` version ref. Mixing `2.5.1` runtime with `2.9.2` compose extensions causes a `java.lang.NoSuchMethodError` at runtime.

**`libs.versions.toml` fix**:
```toml
[versions]
# Change:
lifecycle = "2.5.1"
# To:
lifecycle = "2.9.2"
```

---

## 5. Catalog Entries — Complete Diff

### Add to `[versions]`
```toml
paging3 = "3.3.6"
```

### Add to `[libraries]`
```toml
lifecycle-runtime-compose       = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose",  version.ref = "lifecycle-viewmodel-compose" }
compose-runtime-livedata        = { group = "androidx.compose.runtime", name = "runtime-livedata" }
paging-runtime3                 = { group = "androidx.paging", name = "paging-runtime-ktx",            version.ref = "paging3" }
paging-compose                  = { group = "androidx.paging", name = "paging-compose",                version.ref = "paging3" }
```

### Remove from `[versions]`
```toml
compose-compiler = "1.5.15"   # Redundant with org.jetbrains.kotlin.plugin.compose
```

### Version bumps
```toml
lifecycle = "2.9.2"           # was 2.5.1
coroutines = "1.9.0"          # was 1.6.4
fragment = "1.8.6"            # was 1.2.5
navigation = "2.9.3"          # was 2.5.3 (align with navigation-compose)
```

---

## 6. `app/build.gradle.kts` — Complete Diff

### Remove
```kotlin
// Stale override — compiler version is now managed by org.jetbrains.kotlin.plugin.compose
composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
}

// Paging 2 — replace with Paging 3
implementation(libs.paging.runtime.ktx)
```

### Add
```kotlin
// lifecycle-aware state collection in Compose
implementation(libs.lifecycle.runtime.compose)

// LiveData → State bridge for incremental migration
implementation(libs.compose.runtime.livedata)

// Paging 3 runtime + Compose integration
implementation(libs.paging.runtime3)
implementation(libs.paging.compose)

// Responsive layout window size class
implementation(libs.compose.material3.window.size)

// Compose UI tests under Robolectric
testImplementation(libs.compose.ui.test.junit4)
```

---

## 7. Dependencies to Remove After Full Migration

These are View-system dependencies that become unused once all screens are migrated to Compose. Remove them screen-by-screen as migration completes — do not remove upfront.

| Dependency | Replacement in Compose | Remove when |
|---|---|---|
| `constraintlayout` | `ConstraintLayout {}` composable (or `Box`/`Column` restructuring) | All screens migrated |
| `recyclerview` | `LazyColumn` / `LazyRow` | All lists migrated |
| `recyclerview-selection` | Compose selection via `toggleable` modifier and state | Expense Logs migrated |
| `fragment-ktx` | No Fragment subclasses needed | All screens migrated |
| `navigation-fragment-ktx` | `navigation-compose` already present | All screens migrated |
| `navigation-ui-ktx` | `NavController` in Compose handles drawer/back | All screens migrated |
| `plugins.navigation.safe.args` | Compose Navigation uses typed routes | All screens migrated |
| `progressview` (skydoves) | `LinearProgressIndicator` + `animateFloatAsState` | Statistics screen migrated |
| `mvvm-core` (`EventLiveData`) | `Channel` + `receiveAsFlow()`, or `SharedFlow` | All ViewModels converted to StateFlow |
| `paging-runtime-ktx` (Paging 2) | `paging-runtime3` already added | Expense Logs migrated |
| `fragment-testing` | `createComposeRule()` | All screens migrated |
| `navigation-testing` | Compose Navigation testing utilities | All screens migrated |
| `espresso-*` | `ComposeTestRule` + `onNodeWithText()` etc. | All screens migrated |
| `uiautomator` | `ComposeTestRule` for black-box tests | After full migration |

---

## 8. Dependencies to Keep Permanently

These are unaffected by the UI migration and remain unchanged.

| Category | Dependencies |
|---|---|
| **DI** | `hilt-android`, `hilt-compiler`, `hilt-work`, `hilt-work-compiler`, `hilt-navigation-compose` |
| **Database** | `room-runtime`, `room-ktx`, `room-compiler` |
| **Network** | `retrofit`, `retrofit-converter-gson`, `gson` |
| **Background work** | `work-runtime`, `work-runtime-ktx` |
| **Firebase** | `firebase-bom`, `firebase-analytics`, `firebase-remote-config`, `firebase-crashlytics`, `firebase-firestore` |
| **Preferences** | `flow-preferences` |
| **Logging** | `timber` |
| **Desugaring** | `desugar-jdk-libs` |
| **Coroutines** | `coroutines-core`, `coroutines-android` |
| **Testing** | `junit`, `mockk`, `mockk-android`, `mockito-*`, `robolectric`, `coroutines-test`, `hilt-android-testing`, `androidx-arch-core-testing` |

---

## 9. Migration Dependency Timeline

| Phase | Screen | Action |
|---|---|---|
| **Pre-migration** | All | Fix `composeOptions` bug; bump `lifecycle` to `2.9.2`; add `lifecycle-runtime-compose`, `compose-runtime-livedata` |
| **Phase 1** | Splash, Home | No new dependencies needed beyond pre-migration additions |
| **Phase 2** | Expense Entry | No new dependencies |
| **Phase 3** | Expense Logs | Add Paging 3 (`paging-runtime3`, `paging-compose`); migrate `ExpenseLogViewModel` from `PagedList` to `PagingData` |
| **Phase 4** | Statistics, Settings, Backup, Onboarding | No new dependencies |
| **Post-migration** | All | Remove View-system dependencies listed in §7; remove `compose-runtime-livedata` once all ViewModels use `StateFlow` |
