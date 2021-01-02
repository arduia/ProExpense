package com.arduia.expense.ui.common.mapper

import android.view.View
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.CurrencyDto
import com.arduia.expense.ui.onboarding.CurrencyUiModel
import javax.inject.Inject

class CurrencyUiModelMapper @Inject constructor(): Mapper<CurrencyDto, CurrencyUiModel>{
    override fun map(input: CurrencyDto): CurrencyUiModel {
        return CurrencyUiModel(input.name, input.symbol, input.number, View.INVISIBLE)
    }
}