package com.arduia.expense.feature.importexport

import com.arduia.expense.data.ExportFormat
import java.io.InputStream
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.io.inputstream.ZipInputStream

/**
 * Reads the expenses file out of an exported zip so the exported file round-trips through import
 * without manual extraction (US-IE-2). Both `expenses.csv` (the grouped-CSV export) and
 * `expenses.json` (the JSON export option) are recognized — the other per-type CSVs are reference
 * exports without an import path.
 */
object ImportZipReader {

    sealed interface ZipRead {
        data class Success(val content: String, val format: ExportFormat) : ZipRead
        object NeedsPassword : ZipRead
        object NoExpensesCsv : ZipRead
        object Unreadable : ZipRead
    }

    private const val EXPENSES_CSV_ENTRY = "expenses.csv"
    private const val EXPENSES_JSON_ENTRY = "expenses.json"

    fun readExpensesCsv(inputStream: InputStream, password: String? = null): ZipRead = try {
        ZipInputStream(inputStream, password?.takeIf { it.isNotBlank() }?.toCharArray()).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                val fileName = entry.fileName.substringAfterLast('/')
                val format = when (fileName) {
                    EXPENSES_CSV_ENTRY -> ExportFormat.CSV
                    EXPENSES_JSON_ENTRY -> ExportFormat.JSON
                    else -> null
                }
                if (format != null) {
                    return@use ZipRead.Success(zip.readBytes().toString(Charsets.UTF_8), format)
                }
                entry = zip.nextEntry
            }
            ZipRead.NoExpensesCsv
        }
    } catch (e: ZipException) {
        // Missing password surfaces as a generic ZipException, not Type.WRONG_PASSWORD
        val passwordProblem = e.type == ZipException.Type.WRONG_PASSWORD ||
            e.message?.contains("password", ignoreCase = true) == true
        if (passwordProblem) ZipRead.NeedsPassword else ZipRead.Unreadable
    } catch (e: Exception) {
        ZipRead.Unreadable
    }
}
