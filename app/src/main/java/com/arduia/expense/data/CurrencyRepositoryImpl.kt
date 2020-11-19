package com.arduia.expense.data

import com.arduia.expense.data.exception.RepositoryException
import com.arduia.expense.data.local.CacheDao
import com.arduia.expense.data.local.CurrencyDao
import com.arduia.expense.data.local.CurrencyDto
import com.arduia.expense.model.ErrorResult
import com.arduia.expense.model.FlowResult
import com.arduia.expense.model.SuccessResult
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

class CurrencyRepositoryImpl(
    private val dao: CurrencyDao,
    private val cache: CacheDao
) : CurrencyRepository {

    private val currencyListCh = ConflatedBroadcastChannel<List<CurrencyDto>>()
    private val cacheNumberCH = ConflatedBroadcastChannel<String>()

    init {
        dao.getCurrencies().onEach(currencyListCh::send)
    }

    override fun getCurrencies(): FlowResult<List<CurrencyDto>> {
        return currencyListCh.asFlow()
            .map { SuccessResult(it) }
            .catch { e -> ErrorResult(RepositoryException(e)) }
    }

    override fun getSelectedCacheCurrency(): FlowResult<CurrencyDto> {
        return cacheNumberCH.asFlow()
            .zip(currencyListCh.asFlow()) { num, list ->
                list.find { dto -> dto.number == num } ?: throw Exception("item $num not found!")
            }
            .map { SuccessResult(it) }
            .catch { e -> ErrorResult(RepositoryException(e)) }
    }

    override suspend fun setSelectedCacheCurrency(num: String) {
        cacheNumberCH.offer(num)
    }
}