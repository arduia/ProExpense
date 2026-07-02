package com.arduia.expense.feature.importexport

import android.content.Context
import java.io.File
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.EncryptionMethod

/**
 * Writes a grouped export (filename -> CSV content) to the cache dir and zips it into a single
 * file for sharing (US-IE-1). Cache dir is intentional — these are transient share artifacts, not
 * user-owned long-term storage. A non-blank password produces an AES-encrypted zip (PRD's Secure
 * Import & Export).
 */
object ExportFileWriter {
    private const val EXPORT_DIR_NAME = "exports"

    fun writeZip(
        context: Context,
        files: Map<String, String>,
        zipFileName: String,
        password: String? = null,
    ): File = writeZipTo(File(context.cacheDir, EXPORT_DIR_NAME), files, zipFileName, password)

    internal fun writeZipTo(
        exportDir: File,
        files: Map<String, String>,
        zipFileName: String,
        password: String? = null,
    ): File {
        exportDir.mkdirs()
        exportDir.listFiles()?.forEach { it.delete() }

        val csvFiles = files.map { (name, content) ->
            File(exportDir, name).apply { writeText(content) }
        }

        val zipFile = File(exportDir, zipFileName)
        if (zipFile.exists()) zipFile.delete()
        if (password.isNullOrBlank()) {
            ZipFile(zipFile).addFiles(csvFiles)
        } else {
            val parameters = ZipParameters().apply {
                isEncryptFiles = true
                encryptionMethod = EncryptionMethod.AES
            }
            ZipFile(zipFile, password.toCharArray()).addFiles(csvFiles, parameters)
        }
        return zipFile
    }
}
