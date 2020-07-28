package com.arduia.expense.di

import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.ui.backup.BackupMapper
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.mapping.ExpenseMapper
import com.arduia.expense.ui.vto.BackupVto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import java.text.DateFormat
import java.text.DecimalFormat
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object MapperModule {

    @Provides
    fun provideExpenseMapper(categoryProvider: ExpenseCategoryProvider,
                             @IntegerDecimal decimalFormat: DecimalFormat): ExpenseMapper
        = ExpenseMapper(categoryProvider, currencyFormatter = decimalFormat)

    @Provides
    fun privateBackupMapper(dateFormat: DateFormat): BackupMapper
            = object :BackupMapper{
        override fun mapToBackupVto(ent: BackupEnt) =
            BackupVto(ent.name, ent.backupID, dateFormat.format(ent.created_date), ent.progress_items)
    }

}
