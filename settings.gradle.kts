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
    }
}

rootProject.name = "ProExpense"

// Android shell
include(":app")

// KMP platform utilities
include(":shared")

// Core layers
include(":core:domain")
include(":core:data")
include(":core:storage")

// MVP feature modules (PRD Phase 1)
include(":feature:logging")
include(":feature:currency")
include(":feature:history")
include(":feature:sharedcost")
include(":feature:auth")
include(":feature:importexport")
include(":feature:debt")
include(":feature:eventbudget")
include(":feature:reports")
include(":feature:categories")
include(":feature:onboarding")
