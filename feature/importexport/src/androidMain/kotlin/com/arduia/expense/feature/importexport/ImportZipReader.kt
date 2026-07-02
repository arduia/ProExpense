package com.arduia.expense.feature.importexport

import java.io.InputStream
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.io.inputstream.ZipInputStream

/**
 * Reads the expenses CSV out of an exported zip so the exported file round-trips through import
 * without manual extraction (US-IE-2). Only `expenses.csv` is imported — the other per-type CSVs
 * are reference exports without an import path.
 */
object ImportZipReader {

    sealed interface ZipRead {
        data class Success(val csvContent: String) : ZipRead
        object NeedsPassword : ZipRead
        object NoExpensesCsv : ZipRead
        object Unreadable : ZipRead
    }

    private const val EXPENSES_ENTRY = "expenses.csv"

    fun readExpensesCsv(inputStream: InputStream, password: String? = null): ZipRead = try {
        ZipInputStream(inputStream, password?.takeIf { it.isNotBlank() }?.toCharArray()).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                if (entry.fileName.substringAfterLast('/') == EXPENSES_ENTRY) {
                    return@use ZipRead.Success(zip.readBytes().toString(Charsets.UTF_8))
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
