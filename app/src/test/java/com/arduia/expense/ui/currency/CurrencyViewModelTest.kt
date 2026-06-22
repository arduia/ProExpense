package com.arduia.expense.ui.currency

import com.arduia.expense.data.getOrNull
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.fake.FakeProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CurrencyViewModelTest {

    private fun TestScope.currencyViewModel(profile: FakeProfileRepository) = CurrencyViewModel(
        profileRepository = profile,
        scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
    )

    // Rule: the selected code is seeded from the stored profile currency.
    @Test
    fun seedsFromProfile() = runTest {
        val vm = currencyViewModel(FakeProfileRepository(currency = CurrencyCode("EUR")))
        assertEquals("EUR", vm.selectedCode.value)
    }

    // Rule: selecting a currency persists it and updates the exposed code.
    @Test
    fun selectPersistsAndUpdates() = runTest {
        val profile = FakeProfileRepository(name = "Maya", currency = CurrencyCode("USD"))
        val vm = currencyViewModel(profile)

        vm.select("GBP")

        assertEquals("GBP", vm.selectedCode.value)
        val saved = profile.getProfile().getOrNull()!!
        assertEquals(CurrencyCode("GBP"), saved.homeCurrency)
        assertEquals("Maya", saved.name) // name preserved
    }
}
