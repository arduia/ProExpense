package com.arduia.expense.data.local

import androidx.room.TypeConverter
import com.arduia.expense.domain.Amount

class  AmountTypeConverter {

    @TypeConverter
    fun fromRawAmount(amount: Int): Amount{
        return Amount.createFromStore(amount)
    }

    @TypeConverter
    fun toRawAmount(amount: Amount): Int{
        return amount.getStore()
    }
}