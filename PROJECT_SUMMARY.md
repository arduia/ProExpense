# Pro Expense - Project Summary

## 📝 Project Overview
Pro Expense is a free and open-source Android finance application designed to safely and simply record daily expenses. It prioritizes privacy, security, and performance with a modern UI/UX.

## 🛠 Tech Stack
- **Language**: Kotlin
- **UI Framework**: Mixed (Migrating from traditional XML Views/Fragments to **Jetpack Compose**)
- **Architecture**: MVVM (Model-View-ViewModel) with Clean Architecture principles
- **Dependency Injection**: Dagger Hilt
- **Database**: Room
- **Networking**: Retrofit + Gson
- **Async Execution**: Coroutines, Flow
- **Navigation**: Jetpack Navigation Component (Fragment-based hosting Compose)
- **Design System**: Custom Material 3 components in `:design-system` module

## 📦 Module Structure
- `:app`: Main application module containing UI, Fragments, and ViewModels.
- `:design-system`: Reusable Jetpack Compose components and theme tokens.
- `:shared`: Shared logic and common utilities.
- `:backup` / `:expense-backup`: Logic for data persistence and export/import.
- `:currency-store`: Module for handling currency and exchange rates.
- `:week-expense-graph`: Specific UI module for historical data visualization.

## 🏛 Architecture Patterns
- **Dagger Hilt**: Used for dependency injection across all modules.
- **Navigation Component**: Defines the app flow. New screens (like Login) are added as Compose-based Fragments in the navigation graph.
- **ViewBinding**: Used in older XML-based layouts.
- **Compose**: Primary tool for new UI development.

## 🚀 Current Progress
- [x] Initial Project Setup
- [x] Design System Implementation (Primary/Secondary buttons, TextFields)
- [x] Core Navigation (Splash -> Home)
- [x] **Auth Module (In Progress)**:
    - `LoginScreen.kt`: Compose UI for login.
    - `AuthViewModel.kt`: Authentication logic and state management.
    - `LoginFragment.kt`: Navigation host for the Auth module.

## 📂 Key Directories
- `app/src/main/java/com/arduia/expense/ui/`: UI logic organized by feature.
- `app/src/main/res/navigation/main_nav.xml`: App navigation graph.
- `design-system/src/main/java/com/arduia/design/`: Custom design tokens and components.
- `app/src/main/java/com/arduia/expense/data/`: Data sources and repositories.

## 🎯 Next Steps
1. Refactor existing XML screens (Home, Statistics) to Jetpack Compose.
2. Enhance backup/restore functionality with Cloud options.
