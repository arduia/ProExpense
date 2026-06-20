package com.arduia.expense.feature.importexport

import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.AesKeyStrength
import net.lingala.zip4j.model.enums.CompressionMethod
import net.lingala.zip4j.model.enums.EncryptionMethod
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object EncryptedZipArchive {
    fun pack(password: String, files: Map<String, String>): ByteArray {
        val output = ByteArrayOutputStream()
        val tempFile = java.io.File.createTempFile("pro_export", ".zip")
        try {
            val zipFile = ZipFile(tempFile, password.toCharArray())
            val parameters = ZipParameters().apply {
                compressionMethod = CompressionMethod.DEFLATE
                isEncryptFiles = true
                encryptionMethod = EncryptionMethod.AES
                aesKeyStrength = AesKeyStrength.KEY_STRENGTH_256
            }
            files.forEach { (name, content) ->
                zipFile.addStream(
                    ByteArrayInputStream(content.encodeToByteArray()),
                    parameters.apply { fileNameInZip = name },
                )
            }
            zipFile.close()
            return tempFile.readBytes()
        } finally {
            tempFile.delete()
        }
    }

    fun unpack(password: String, archiveBytes: ByteArray): Map<String, String> {
        val tempFile = java.io.File.createTempFile("pro_import", ".zip")
        try {
            tempFile.writeBytes(archiveBytes)
            val zipFile = ZipFile(tempFile, password.toCharArray())
            if (!zipFile.isEncrypted) {
                throw IllegalArgumentException("Archive is not encrypted")
            }
            val entries = zipFile.fileHeaders.map { it.fileName }
            return entries.associateWith { name ->
                val output = ByteArrayOutputStream()
                zipFile.getInputStream(zipFile.getFileHeader(name)).use { input ->
                    input.copyTo(output)
                }
                output.toString(Charsets.UTF_8.name())
            }
        } finally {
            tempFile.delete()
        }
    }
}
