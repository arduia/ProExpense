package com.arduia.expense.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.Test

/**
 * Encodes the module dependency rules from AGENTS.md's "Dependency Rules" table as executable
 * checks, so a violation fails the build instead of relying on a human noticing it in review.
 */
class ArchitectureTest {
    private val featurePackagePrefix = "com.arduia.expense.feature."

    private val featureNames =
        listOf(
            "logging",
            "currency",
            "history",
            "sharedcost",
            "auth",
            "importexport",
            "sync",
            "debt",
            "eventbudget",
            "reports",
            "categories",
            "onboarding",
        )

    @Test
    fun `feature modules do not depend on other feature modules`() {
        Konsist
            .scopeFromProject()
            .files
            .filter { file -> file.packagee?.name?.startsWith(featurePackagePrefix) == true }
            .assertFalse { file ->
                val ownFeature =
                    file.packagee!!
                        .name
                        .removePrefix(featurePackagePrefix)
                        .substringBefore('.')
                file.hasImport { import ->
                    featureNames.any { other ->
                        other != ownFeature && import.name.startsWith("$featurePackagePrefix$other")
                    }
                }
            }
    }

    @Test
    fun `core and shared modules do not depend on feature modules`() {
        val corePackagePrefixes =
            listOf(
                "com.arduia.expense.domain",
                "com.arduia.expense.data",
                "com.arduia.expense.storage",
                "com.arduia.expense.shared",
            )

        Konsist
            .scopeFromProject()
            .files
            .filter { file -> corePackagePrefixes.any { file.packagee?.name?.startsWith(it) == true } }
            .assertFalse { file -> file.hasImport { import -> import.name.startsWith(featurePackagePrefix) } }
    }

    @Test
    fun `commonMain source does not import java or android platform packages`() {
        Konsist
            .scopeFromProject()
            .files
            .filter { file -> file.resideInSourceSet("commonMain") }
            .assertFalse { file ->
                file.hasImport { import ->
                    import.name.startsWith("java.") || import.name.startsWith("android.")
                }
            }
    }
}
