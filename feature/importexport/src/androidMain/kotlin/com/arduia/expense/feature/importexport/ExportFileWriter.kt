package com.arduia.expense.feature.importexport

import android.content.Context
import java.io.File
import net.lingala.zip4j.ZipFile

/**
 * Writes a grouped export (filename -> CSV content) to the cache dir and zips it into a single
 * file for sharing (US-IE-1). Cache dir is intentional — these are transient share artifacts, not
 * user-owned long-term storage.
 */
object ExportFileWriter {
    private const val EXPORT_DIR_NAME = "exports"

    fun writeZip(context: Context, files: Map<String, String>, zipFileName: String): File {
        val exportDir = File(context.cacheDir, EXPORT_DIR_NAME).apply { mkdirs() }
        exportDir.listFiles()?.forEach { it.delete() }

        val csvFiles = files.map { (name, content) ->
            File(exportDir, name).apply { writeText(content) }
        }

        val zipFile = File(exportDir, zipFileName)
        if (zipFile.exists()) zipFile.delete()
        ZipFile(zipFile).addFiles(csvFiles)
        return zipFile
    }
}
