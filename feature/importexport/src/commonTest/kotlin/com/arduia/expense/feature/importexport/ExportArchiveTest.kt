package com.arduia.expense.feature.importexport

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExportArchiveTest {
    @Test
    fun buildAndVerifyArchive_roundTripsSignature() {
        val payload = ExportArchive.buildArchive(
            password = "export-secret",
            records = emptyList(),
            events = emptyList(),
            debts = emptyList(),
            sharedCosts = emptyList(),
        )
        assertEquals(ExportVerificationResult.Valid, ExportArchive.verifyArchive("export-secret", payload))
    }

    @Test
    fun verifyArchive_rejectsWrongPassword() {
        val payload = ExportArchive.buildArchive(
            password = "correct",
            records = emptyList(),
            events = emptyList(),
            debts = emptyList(),
            sharedCosts = emptyList(),
        )
        assertEquals(
            ExportVerificationResult.InvalidPasswordOrCorrupt,
            ExportArchive.verifyArchive("wrong", payload),
        )
    }

    @Test
    fun parseManifestJson_readsSchemaVersion() {
        val payload = ExportArchive.buildArchive(
            password = "pw",
            records = emptyList(),
            events = emptyList(),
            debts = emptyList(),
            sharedCosts = emptyList(),
        )
        val json = ExportArchive.manifestToJson(payload.manifest)
        val parsed = ExportArchive.parseManifestJson(json)
        assertEquals(ExportArchive.SCHEMA_VERSION, parsed?.schemaVersion)
        assertTrue(parsed?.signature?.isNotBlank() == true)
    }
}
