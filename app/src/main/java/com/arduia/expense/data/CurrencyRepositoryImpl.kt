package com.arduia.expense.data

import com.arduia.expense.data.exception.RepositoryException
import com.arduia.expense.data.local.CacheDao
import com.arduia.expense.data.local.CurrencyDao
import com.arduia.expense.data.local.CurrencyDto
import com.arduia.expense.model.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    dao: CurrencyDao
) : CurrencyRepository {

    private val currencyListFlow = MutableStateFlow<List<CurrencyDto>>(emptyList())
    private val cacheNumberFlow = MutableStateFlow<String>("")

    init {
        dao.getCurrencies()
            .onEach { currencyListFlow.value = it }
            .launchIn(GlobalScope)
    }

    override fun getCurrencies(): FlowResult<List<CurrencyDto>> {
        return currencyListFlow.asStateFlow()
            .map { SuccessResult(it) as Result<List<CurrencyDto>>}
            .onStart { emit(LoadingResult) }
            .catch { e -> ErrorResult(RepositoryException(e)) }
    }

    override fun getSelectedCacheCurrency(): FlowResult<CurrencyDto> {
        return currencyListFlow.asStateFlow()
            .combine(cacheNumberFlow.asStateFlow()) { list, num ->
                list.find { dto -> dto.number == num } ?: throw Exception("item $num not found!")
            }
            .map { SuccessResult(it) }
            .catch { e -> ErrorResult(RepositoryException(e)) }
    }

    override suspend fun setSelectedCacheCurrency(num: String) {
        cacheNumberFlow.value = num
    }
}