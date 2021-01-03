package com.arduia.expense.data.backup

import android.content.Context
import android.os.Build
import com.arduia.backup.BackupSheet
import com.arduia.backup.BackupSource
import com.arduia.backup.SheetFieldInfo
import com.arduia.backup.SheetRow
import com.arduia.backup.task.BackupResult
import com.arduia.expense.backup.schema.BackupSchema
import com.arduia.expense.backup.schema.table.Field
import com.arduia.expense.backup.schema.table.Table
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.model.awaitValueOrError
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

//no longer use after Backup Schema framework is completed!,to support feature schema
class SchemaBackupSheet @Inject constructor(source: BackupSource<BackupSchema>) : BackupSheet<BackupSchema>(source) {
    override val sheetName = "metadata"
    private val gson = Gson()

    init {
        isExportCountEnable = false
    }

    override fun getFieldInfo(): SheetFieldInfo {
        val mutableMap = mutableMapOf<String, String>()
        mutableMap[FIELD_NAME] = "string"
        mutableMap[FILED_VALUE] = "string"
        return SheetFieldInfo.createFromMap(mutableMap)
    }

    override fun mapToEntity(row: SheetRow): BackupSchema {
        // That should Happen
        return BackupSchema(currencyCode = "840", exportTables = listOf(), appVersionCode = 0)
    }

    override fun mapToSheetRow(item: BackupSchema): SheetRow {
        val map = mutableMapOf<String, String>()
        map[FIELD_NAME] = "schema"
        map[FILED_VALUE] = gson.toJson(item)
        return SheetRow.createFromMap(map)
    }

    companion object {
        private const val FIELD_NAME = "Name"
        private const val FILED_VALUE = "Value"
    }
}

class SchemaBackupSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val currencyRepo: CurrencyRepository
) :
    BackupSource<BackupSchema> {
    override suspend fun write(item: BackupSchema) {}

    override suspend fun writeAll(items: List<BackupSchema>) {}

    override suspend fun readAll(): List<BackupSchema> {
        return listOf(
            BackupSchema(
                appVersionCode = context.getApplicationVersionCode(), exportTables = listOf(
                    getCurrentTable()
                ),
                currencyCode = currencyRepo.getSelectedCacheCurrency().awaitValueOrError().number
            )
        )
    }

    override suspend fun totalCountAll(): Int {
        return 1
    }

    private fun getCurrentTable(): Table {
        val names = "expense"
        val fields = mutableListOf<Field>()
        fields += Field("name", "string")
        fields += Field("date", "long")
        fields += Field("category", "integer")
        fields += Field("amount", "long")
        fields += Field("note", " string")
        return Table(listOf(names), fields)
    }
}


fun Context.getApplicationVersionCode(): Long {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageManager.getPackageInfo(packageName, 0).longVersionCode
    } else packageManager.getPackageInfo(packageName, 0).versionCode.toLong()
}
