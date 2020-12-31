package com.arduia.expense.ui.common.mapper

import android.view.View
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.CurrencyDto
import com.arduia.expense.ui.onboarding.CurrencyVo

class CurrencyMapper : Mapper<CurrencyDto, CurrencyVo>{
    override fun map(input: CurrencyDto): CurrencyVo {
        return CurrencyVo(input.name, input.symbol, input.number, View.INVISIBLE)
    }
}