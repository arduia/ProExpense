/**
 * Domain rules (design plan B4):
 * - Password-protected AES ZIP round-trip.
 * - Unencrypted archives are rejected on unpack.
 */
package com.arduia.expense.feature.importexport

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class EncryptedZipArchiveTest {
    @Test
    fun packAndUnpack_roundTripsFiles() {
        val files = mapOf(
            ExportArchive.MANIFEST_FILE to """{"schemaVersion":1}""",
            ExportArchive.RECORDS_FILE to "id,amount\nr1,100",
        )
        val packed = EncryptedZipArchive.pack("export-password", files)
        val unpacked = EncryptedZipArchive.unpack("export-password", packed)

        assertEquals(files[ExportArchive.MANIFEST_FILE], unpacked[ExportArchive.MANIFEST_FILE])
        assertEquals(files[ExportArchive.RECORDS_FILE], unpacked[ExportArchive.RECORDS_FILE])
    }

    @Test
    fun unpack_wrongPassword_fails() {
        val packed = EncryptedZipArchive.pack("correct", mapOf("test.txt" to "data"))
        assertFailsWith<Exception> {
            EncryptedZipArchive.unpack("wrong", packed)
        }
    }
}
