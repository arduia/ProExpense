package com.arduia.backup.generator

import com.arduia.backup.FileNameGenerator
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class BackupNameGenerator (private val dateFormatter: DateFormat = SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH)):
    FileNameGenerator {

    companion object{
        private const val PREFIX = "Backup"
        private const val DATE_PATTERN = "_MMMd_yyyy_Hms"
    }

    override fun generate(): String {
        val date = Date().time

        val dateSuffix = dateFormatter.format(date)

        return "$PREFIX$dateSuffix"
    }
}
