package com.arduia.expense.di

import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.ui.mapping.BackupMapper
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import com.arduia.expense.ui.mapping.ExpenseMapper
import com.arduia.expense.ui.mapping.ExpenseMapperImpl
import com.arduia.expense.ui.vto.BackupVto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.FragmentScoped
import java.text.DateFormat
import java.text.DecimalFormat

@Module
@InstallIn(ActivityComponent::class)
object MapperModule {

    @Provides
    fun provideExpenseMapper(categoryProvider: ExpenseCategoryProvider,
                             dateFormatter: DateFormat,
                             @CurrencyDecimalFormat decimalFormat: DecimalFormat): ExpenseMapper
        = ExpenseMapperImpl(categoryProvider,
        dateFormatter = dateFormatter,
        currencyFormatter = decimalFormat)

    @Provides
    fun privateBackupMapper(dateFormat: DateFormat): BackupMapper
            = object : BackupMapper {
        override fun mapToBackupVto(ent: BackupEnt) =
            BackupVto(
                id = ent.backupId,
                name = ent.name,
                date = dateFormat.format(ent.createdDate),
                items = ent.itemTotal.toString(),
                onProgress = ent.isCompleted.not()
            )
    }

}
