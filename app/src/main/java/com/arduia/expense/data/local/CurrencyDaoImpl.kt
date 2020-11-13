package com.arduia.expense.data.local

import android.content.res.AssetManager
import com.google.gson.GsonBuilder
import java.lang.RuntimeException

class CurrencyDaoImpl(private val assetManager: AssetManager) : CurrencyDao {
    override suspend fun getCurrencies(): List<CurrencyDto> {
        try {
            val file = assetManager.open(CURRENCY_FILE_PATH)
            return GsonBuilder()
                .create()
                .fromJson(file.reader(), Currencies::class.java)
                .sortedBy { it.rank }
        } catch (e: Exception) {
            throw RuntimeException("currencies", e)
        }
    }

    companion object {
        const val CURRENCY_FILE_PATH = "currencies.json"
    }
}

private class Currencies : java.util.ArrayList<CurrencyDto>()