package com.arduia.expense.ui.mapping

import android.content.Context
import com.arduia.core.arch.Mapper
import com.arduia.expense.R
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.ui.vto.BackupVto
import java.text.DateFormat

class BackupVoMapper(context: Context, private val dateFormatter: DateFormat) :
    Mapper<BackupEnt, BackupVto> {

    private val itemSuffix = context.getString(R.string.single_item_suffix)
    private val multiItemSuffix = context.getString(R.string.multi_item_suffix)

    override fun map(input: BackupEnt) =
        BackupVto(
            id = input.backupId,
            name = input.name,
            date = dateFormatter.format(input.createdDate),
            items = input.itemTotal.toString() + if (input.itemTotal > 0) multiItemSuffix else itemSuffix,
            onProgress = input.isCompleted.not()
        )
}