package com.arduia.backup.generator

import java.text.SimpleDateFormat
import java.util.*

class MetadataFileNameGen :FileNameGenerator {

    companion object{
        private const val PREFIX = "Backup_"
        private const val EXTENSION = ".expense"
        private const val DATE_PATTERN = "ddMMMyyyy_H_m_s"
    }

    override fun generate(): String {
        val date = Date().time

        val sampleDateFormatter = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())

        val dateSuffix = sampleDateFormatter.format(date)

        return "$PREFIX$dateSuffix$EXTENSION"
    }
}
