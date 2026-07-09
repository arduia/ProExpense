package com.arduia.expense.feature.importexport

import com.arduia.expense.data.ExportFormat
import com.arduia.expense.feature.importexport.ImportZipReader.ZipRead
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExportImportZipRoundTripTest {
    private val tempDir = File(System.getProperty("java.io.tmpdir"), "export-test-${System.nanoTime()}")

    private val files =
        mapOf(
            "expenses.csv" to "id,money_cents\n\"r1\",\"5000\"",
            "events.csv" to "id,name\n\"e1\",\"Trip\"",
        )

    @AfterTest
    fun cleanup() {
        tempDir.deleteRecursively()
    }

    @Test
    fun plainZip_roundTripsExpensesCsv() {
        val zip = ExportFileWriter.writeZipTo(tempDir, files, "export.zip")

        val read = zip.inputStream().use { ImportZipReader.readExpensesCsv(it) }

        assertEquals(ZipRead.Success(files.getValue("expenses.csv"), ExportFormat.CSV), read)
    }

    @Test
    fun plainZip_roundTripsExpensesJson() {
        val jsonFiles = mapOf("expenses.json" to """[{"id":"r1"}]""")
        val zip = ExportFileWriter.writeZipTo(tempDir, jsonFiles, "export.zip")

        val read = zip.inputStream().use { ImportZipReader.readExpensesCsv(it) }

        assertEquals(ZipRead.Success(jsonFiles.getValue("expenses.json"), ExportFormat.JSON), read)
    }

    @Test
    fun encryptedZip_roundTripsWithCorrectPassword() {
        val zip = ExportFileWriter.writeZipTo(tempDir, files, "export.zip", password = "s3cret")

        val read = zip.inputStream().use { ImportZipReader.readExpensesCsv(it, password = "s3cret") }

        assertEquals(ZipRead.Success(files.getValue("expenses.csv"), ExportFormat.CSV), read)
    }

    @Test
    fun encryptedZip_missingOrWrongPassword_reportsNeedsPassword() {
        val zip = ExportFileWriter.writeZipTo(tempDir, files, "export.zip", password = "s3cret")

        val withoutPassword = zip.inputStream().use { ImportZipReader.readExpensesCsv(it) }
        val wrongPassword = zip.inputStream().use { ImportZipReader.readExpensesCsv(it, password = "nope") }

        assertEquals(ZipRead.NeedsPassword, withoutPassword)
        assertEquals(ZipRead.NeedsPassword, wrongPassword)
    }

    @Test
    fun zipWithoutExpensesCsv_reportsNoExpensesCsv() {
        val zip = ExportFileWriter.writeZipTo(tempDir, mapOf("events.csv" to "id\n\"e1\""), "export.zip")

        val read = zip.inputStream().use { ImportZipReader.readExpensesCsv(it) }

        assertEquals(ZipRead.NoExpensesCsv, read)
    }

    @Test
    fun nonZipContent_reportsUnreadable() {
        val read = "just,a,csv".byteInputStream().use { ImportZipReader.readExpensesCsv(it) }

        assertTrue(read == ZipRead.Unreadable || read == ZipRead.NoExpensesCsv)
    }
}
