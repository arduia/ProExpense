package com.arduia.backup.generator

import com.arduia.backup.FileNameGenerator
import java.text.SimpleDateFormat
import java.util.*

class BackupNameGenerator:
    FileNameGenerator {

    companion object{
        private const val PREFIX = "Backup"
        private const val EXTENSION = ".xls"
        private const val DATE_PATTERN = "_ddMyyyyHms"
    }

    override fun generate(): String {
        val date = Date().time

        val sampleDateFormatter = SimpleDateFormat( DATE_PATTERN, Locale.getDefault())

        val dateSuffix = sampleDateFormatter.format(date)

        return "$PREFIX$dateSuffix$EXTENSION"
    }
}
