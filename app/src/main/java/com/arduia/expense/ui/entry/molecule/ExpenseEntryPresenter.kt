package com.arduia.expense.ui.entry.molecule

import android.util.Log
import androidx.compose.runtime.*
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.CurrencyRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.domain.Amount
import com.arduia.expense.model.SuccessResult
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.ui.common.category.ExpenseCategory
import com.arduia.expense.ui.common.category.ExpenseCategoryProvider
import com.arduia.expense.ui.common.molecule.Presenter
import com.arduia.expense.ui.entry.ExpenseEntryMode
import com.arduia.expense.ui.entry.ExpenseUpdateDataUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.*

class ExpenseEntryPresenter(
    private val expenseRepo: ExpenseRepository,
    private val currencyRepo: CurrencyRepository,
    private val mapper: Mapper<ExpenseEnt, ExpenseUpdateDataUiModel>,
    private val categoryProvider: ExpenseCategoryProvider
) : Presenter<ExpenseEntryEvent, ExpenseEntryState> {

    @Composable
    override fun present(events: kotlinx.coroutines.flow.Flow<ExpenseEntryEvent>): ExpenseEntryState {
        var mode by remember { mutableStateOf(ExpenseEntryMode.INSERT) }
        var name by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("") }
        var note by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
        var categories by remember { mutableStateOf<List<ExpenseCategory>>(emptyList()) }
        var currencySymbol by remember { mutableStateOf("") }
        var entryDateTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }
        var isRepeatEnabled by remember { mutableStateOf(false) }
        var amountError by remember { mutableStateOf<String?>(null) }
        var showDatePicker by remember { mutableStateOf<Calendar?>(null) }
        var showTimePicker by remember { mutableStateOf<Calendar?>(null) }
        var navigateBack by remember { mutableStateOf(false) }
        var showSnackbar by remember { mutableStateOf<String?>(null) }

        var currentExpenseId by remember { mutableStateOf(0) }
        var entryCreatedDate by remember { mutableStateOf<Long?>(null) }

        // Load categories on first composition
        LaunchedEffect(Unit) {
            categories = categoryProvider.getCategoryList()
            // Set default category (Food)
            if (selectedCategory == null && categories.isNotEmpty()) {
                selectedCategory = try {
                    categoryProvider.getCategoryByID(ExpenseCategory.FOOD)
                } catch (e: Exception) {
                    categories.firstOrNull()
                }
            }
        }

        // Observe currency symbol
        LaunchedEffect(Unit) {
            currencyRepo.getSelectedCacheCurrency().collectLatest { result ->
                if (result is SuccessResult) {
                    currencySymbol = result.data.code
                }
            }
        }

        // Process events
        LaunchedEffect(events) {
            events.collect { event ->
                when (event) {
                    is ExpenseEntryEvent.ChooseSaveMode -> {
                        mode = ExpenseEntryMode.INSERT
                        entryDateTimeMillis = System.currentTimeMillis()
                    }
                    is ExpenseEntryEvent.ChooseUpdateMode -> {
                        mode = ExpenseEntryMode.UPDATE
                        currentExpenseId = event.expenseId
                        // Load existing data
                        try {
                            val ent = withContext(Dispatchers.IO) {
                                val result = expenseRepo.getExpense(event.expenseId).first()
                                if (result is com.arduia.expense.model.Result.Success) result.data else throw Exception("Expense Not Found")
                            }
                            entryDateTimeMillis = ent.modifiedDate
                            entryCreatedDate = ent.createdDate
                            val data = mapper.map(ent)
                            name = data.name
                            amount = data.amount
                            note = data.note
                            selectedCategory = data.category
                            currentExpenseId = data.id

                            categories = categoryProvider.getCategoryList()
                        } catch (e: Exception) {
                            Log.e("ExpenseEntryPresenter", "Failed to load expense", e)
                        }
                    }
                    is ExpenseEntryEvent.NameChanged -> {
                        name = event.value
                    }
                    is ExpenseEntryEvent.AmountChanged -> {
                        val newValue = event.value.replace(",", ".")
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d{0,10}(\\.\\d{0,2})?$"))) {
                            amount = newValue
                            amountError = null
                        }
                    }
                    is ExpenseEntryEvent.NoteChanged -> {
                        note = event.value
                    }
                    is ExpenseEntryEvent.CategorySelected -> {
                        selectedCategory = event.category
                    }
                    is ExpenseEntryEvent.RepeatToggled -> {
                        isRepeatEnabled = event.isChecked
                    }
                    is ExpenseEntryEvent.SaveClicked -> {
                        if (amount.isBlank()) {
                            amountError = "Empty Cost"
                            return@collect
                        }
                        try {
                            val category = selectedCategory ?: return@collect
                            val expenseEnt = ExpenseEnt(
                                expenseId = 0,
                                name = name,
                                amount = Amount.createFromActual(BigDecimal(amount)),
                                note = note,
                                category = category.id,
                                createdDate = Date().time,
                                modifiedDate = entryDateTimeMillis
                            )
                            withContext(Dispatchers.IO) {
                                expenseRepo.insertExpense(expenseEnt)
                            }
                            if (isRepeatEnabled) {
                                // Reset fields for next entry
                                name = ""
                                amount = ""
                                note = ""
                                selectedCategory = try {
                                    categoryProvider.getCategoryByID(ExpenseCategory.FOOD)
                                } catch (e: Exception) {
                                    categories.firstOrNull()
                                }
                                entryDateTimeMillis = System.currentTimeMillis()
                                showSnackbar = "Saved!"
                            } else {
                                navigateBack = true
                            }
                        } catch (e: Exception) {
                            Log.e("ExpenseEntryPresenter", "Failed to save expense", e)
                        }
                    }
                    is ExpenseEntryEvent.UpdateClicked -> {
                        if (amount.isBlank()) {
                            amountError = "Empty Cost"
                            return@collect
                        }
                        try {
                            val category = selectedCategory ?: return@collect
                            val expenseEnt = ExpenseEnt(
                                expenseId = currentExpenseId,
                                name = name,
                                amount = Amount.createFromActual(BigDecimal(amount)),
                                note = note,
                                category = category.id,
                                createdDate = entryCreatedDate ?: Date().time,
                                modifiedDate = entryDateTimeMillis
                            )
                            withContext(Dispatchers.IO) {
                                expenseRepo.updateExpense(expenseEnt)
                            }
                            showSnackbar = "Data Updated"
                            navigateBack = true
                        } catch (e: Exception) {
                            Log.e("ExpenseEntryPresenter", "Failed to update expense", e)
                        }
                    }
                    is ExpenseEntryEvent.DateSelectClicked -> {
                        showDatePicker = Calendar.getInstance().apply {
                            timeInMillis = entryDateTimeMillis
                        }
                    }
                    is ExpenseEntryEvent.TimeSelectClicked -> {
                        showTimePicker = Calendar.getInstance().apply {
                            timeInMillis = entryDateTimeMillis
                        }
                    }
                    is ExpenseEntryEvent.DateSelected -> {
                        entryDateTimeMillis = event.timeMillis
                        showDatePicker = null
                    }
                    is ExpenseEntryEvent.TimeSelected -> {
                        val cal = Calendar.getInstance().apply {
                            timeInMillis = entryDateTimeMillis
                        }
                        cal[Calendar.HOUR_OF_DAY] = event.hour
                        cal[Calendar.MINUTE] = event.minute
                        cal[Calendar.MILLISECOND] = 0
                        entryDateTimeMillis = cal.timeInMillis
                        showTimePicker = null
                    }
                    is ExpenseEntryEvent.DatePickerDismissed -> {
                        showDatePicker = null
                    }
                    is ExpenseEntryEvent.TimePickerDismissed -> {
                        showTimePicker = null
                    }
                    is ExpenseEntryEvent.SnackbarShown -> {
                        showSnackbar = null
                    }
                    is ExpenseEntryEvent.NavigationHandled -> {
                        navigateBack = false
                    }
                }
            }
        }

        return ExpenseEntryState(
            mode = mode,
            name = name,
            amount = amount,
            note = note,
            selectedCategory = selectedCategory,
            categories = categories,
            currencySymbol = currencySymbol,
            entryDateTimeMillis = entryDateTimeMillis,
            isRepeatEnabled = isRepeatEnabled,
            amountError = amountError,
            showDatePicker = showDatePicker,
            showTimePicker = showTimePicker,
            navigateBack = navigateBack,
            showSnackbar = showSnackbar
        )
    }
}
