package com.arduia.expense.data

import com.arduia.expense.data.exception.RepositoryException
import com.arduia.expense.data.local.CacheDao
import com.arduia.expense.data.local.CurrencyDao
import com.arduia.expense.data.local.CurrencyDto
import com.arduia.expense.model.ErrorResult
import com.arduia.expense.model.FlowResult
import com.arduia.expense.model.SuccessResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

class CurrencyRepositoryImpl(
    dao: CurrencyDao
) : CurrencyRepository {

    private val currencyListCh = ConflatedBroadcastChannel<List<CurrencyDto>>()
    private val cacheNumberCH = ConflatedBroadcastChannel<String>()

    init {
        dao.getCurrencies()
            .onEach(currencyListCh::send)
            .launchIn(GlobalScope)
    }

    override fun getCurrencies(): FlowResult<List<CurrencyDto>> {
        return currencyListCh.asFlow()
            .map { SuccessResult(it) }
            .catch { e -> ErrorResult(RepositoryException(e)) }
    }

    override fun getSelectedCacheCurrency(): FlowResult<CurrencyDto> {
        return currencyListCh.asFlow()
            .combine(cacheNumberCH.asFlow()) { list, num ->
                list.find { dto -> dto.number == num } ?: throw Exception("item $num not found!")
            }
            .map { SuccessResult(it) }
            .catch { e -> ErrorResult(RepositoryException(e)) }
    }

    override suspend fun setSelectedCacheCurrency(num: String) {
        cacheNumberCH.offer(num)
    }
}