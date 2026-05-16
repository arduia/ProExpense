pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "ProExpense"
include(":app")
include(":expense-backup")
include(":currency-store")
include(":backup")
include(":shared")
include(":week-expense-graph")

// Core modules
include(":core-model")
include(":core-domain")
include(":core-data")
include(":core-ui")

// Feature modules
include(":feature-splash")
include(":feature-onboarding")
include(":feature-home")
include(":feature-entry")
include(":feature-expenselogs")
include(":feature-statistics")
include(":feature-backup")
include(":feature-settings")
include(":feature-feedback")
include(":feature-about")
include(":feature-web")