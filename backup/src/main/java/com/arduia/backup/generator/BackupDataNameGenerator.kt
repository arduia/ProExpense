package com.arduia.backup.generator

import java.text.SimpleDateFormat
import java.util.*

class BackupDataNameGenerator(private val tableName: String): FileNameGenerator{

    companion object{
        private const val PREFIX = "PE_"
        private const val EXTENSION = ".csv"
        private const val DATE_PATTERN = "_ddMyyyyHms"
    }

    override fun generate(): String {
        val date = Date().time

        val sampleDateFormatter = SimpleDateFormat( DATE_PATTERN, Locale.getDefault())

        val dateSuffix = sampleDateFormatter.format(date)

        return "$PREFIX${tableName.toUpperCase(Locale.ROOT)}$dateSuffix$EXTENSION"
    }
}
