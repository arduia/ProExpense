package com.arduia.expense.di

import com.arduia.expense.data.BudgetRepository
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.SecurityStateReader
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.fake.FakeBudgetRepository
import com.arduia.expense.fake.FakeCategoryRepository
import com.arduia.expense.fake.FakeDebtRepository
import com.arduia.expense.fake.FakeEventRepository
import com.arduia.expense.fake.FakeFinanceRecordRepository
import com.arduia.expense.fake.FakeProfileRepository
import com.arduia.expense.fake.FakeSecurityStateReader
import com.arduia.expense.fake.FakeSharedCostRepository
import com.arduia.expense.ui.categories.CategoriesViewModel
import com.arduia.expense.ui.currency.CurrencyViewModel
import com.arduia.expense.ui.debt.DebtViewModel
import com.arduia.expense.ui.events.EventsViewModel
import com.arduia.expense.ui.home.HomeViewModel
import com.arduia.expense.ui.journal.JournalViewModel
import com.arduia.expense.ui.logging.ExpenseEntryViewModel
import com.arduia.expense.ui.reports.ReportsViewModel
import com.arduia.expense.ui.sharedcost.SharedCostViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test

class AppModuleTest {

    @AfterTest
    fun tearDown() {
        GlobalContext.getOrNull()?.let { GlobalContext.stopKoin() }
    }

    // Backs the repository contracts with fakes so the graph resolves without opening the DB.
    private val fakeRepositoryModule = module {
        single<FinanceRecordRepository> { FakeFinanceRecordRepository() }
        single<BudgetRepository> { FakeBudgetRepository() }
        single<ProfileRepository> { FakeProfileRepository() }
        single<SecurityStateReader> { FakeSecurityStateReader() }
        single<EventRepository> { FakeEventRepository() }
        single<DebtRepository> { FakeDebtRepository() }
        single<CategoryRepository> { FakeCategoryRepository() }
        single<SharedCostRepository> { FakeSharedCostRepository() }
    }

    // Rule: every ViewModel factory in viewModelModule must be constructable from the data graph.
    // Guards against missing/unsatisfiable dependencies (e.g. injecting a non-registered lambda).
    @Test
    fun everyViewModelResolves() {
        val koin = koinApplication { modules(fakeRepositoryModule, viewModelModule) }.koin
        val scope = CoroutineScope(Dispatchers.Unconfined)

        koin.get<HomeViewModel> { parametersOf(scope) }
        koin.get<JournalViewModel> { parametersOf(scope) }
        koin.get<ReportsViewModel> { parametersOf(scope) }
        koin.get<CurrencyViewModel> { parametersOf(scope) }
        koin.get<EventsViewModel> { parametersOf(scope) }
        koin.get<DebtViewModel> { parametersOf(scope) }
        koin.get<CategoriesViewModel> { parametersOf(scope) }
        koin.get<SharedCostViewModel> { parametersOf(scope) }
        koin.get<ExpenseEntryViewModel>()

        koin.close()
    }
}
