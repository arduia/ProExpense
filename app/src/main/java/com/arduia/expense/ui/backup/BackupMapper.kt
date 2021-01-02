package com.arduia.expense.ui.backup

import android.content.Context
import com.arduia.core.arch.Mapper
import com.arduia.expense.R
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.di.IntegerDecimal
import com.arduia.expense.ui.common.formatter.DateFormatter
import dagger.hilt.android.qualifiers.ActivityContext
import java.text.DecimalFormat
import javax.inject.Inject

class BackupUiModelMapper @Inject constructor(
    @ActivityContext context: Context,
    private val dateFormatter: DateFormatter,
    @IntegerDecimal private val numberFormat: DecimalFormat
) :
    Mapper<BackupEnt, BackupUiModel> {

    private val itemSuffix = context.getString(R.string.single_item_suffix)
    private val multiItemSuffix = context.getString(R.string.multi_item_suffix)


    override fun map(input: BackupEnt) =
        BackupUiModel(
            id = input.backupId,
            name = input.name,
            date = dateFormatter.format(input.createdDate),
            items = numberFormat.format(input.itemTotal) + " " + if (input.itemTotal > 0) multiItemSuffix else itemSuffix,
            onProgress = input.isCompleted.not()
        )
}