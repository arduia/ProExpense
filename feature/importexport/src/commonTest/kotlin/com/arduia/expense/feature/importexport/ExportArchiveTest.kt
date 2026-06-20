/**
 * Domain rules (design plan B4/B5):
 * - Export manifest includes schema version and HMAC signature over counts + CSV payloads.
 * - Wrong password or tampered payload → InvalidPasswordOrCorrupt.
 * - Schema newer than app → SchemaTooNew (distinct from corrupt/password errors).
 */
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
    fun verifyArchive_rejectsTamperedCsv() {
        val payload = ExportArchive.buildArchive(
            password = "secret",
            records = emptyList(),
            events = emptyList(),
            debts = emptyList(),
            sharedCosts = emptyList(),
        )
        val tampered = payload.copy(recordsCsv = "tampered")
        assertEquals(
            ExportVerificationResult.InvalidPasswordOrCorrupt,
            ExportArchive.verifyArchive("secret", tampered),
        )
    }

    @Test
    fun verifyArchive_rejectsSchemaTooNew() {
        val payload = ExportArchive.buildArchive(
            password = "pw",
            records = emptyList(),
            events = emptyList(),
            debts = emptyList(),
            sharedCosts = emptyList(),
        )
        val future = payload.copy(
            manifest = payload.manifest.copy(schemaVersion = ExportArchive.SCHEMA_VERSION + 1),
        )
        assertEquals(ExportVerificationResult.SchemaTooNew, ExportArchive.verifyArchive("pw", future))
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
