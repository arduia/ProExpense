package com.arduia.expense.ui.entry

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.ui.MainHost
import com.arduia.expense.ui.entry.molecule.ExpenseEntryEvent
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class ExpenseEntryFragment : Fragment() {

    private val args: ExpenseEntryFragmentArgs by navArgs()
    private val viewModel by viewModels<ExpenseEntryViewModel>()

    @Inject
    lateinit var mainHost: MainHost

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ProExpenseTheme {
                    val state by viewModel.state.collectAsState()

                    // Initialize mode based on nav args
                    LaunchedEffect(Unit) {
                        val argId = args.expenseId
                        if (argId < 0) {
                            viewModel.take(ExpenseEntryEvent.ChooseSaveMode(argId))
                        } else {
                            viewModel.take(ExpenseEntryEvent.ChooseUpdateMode(argId))
                        }
                    }

                    // Handle date picker
                    LaunchedEffect(state.showDatePicker) {
                        state.showDatePicker?.let { calendar ->
                            showDatePicker(calendar)
                        }
                    }

                    // Handle time picker
                    LaunchedEffect(state.showTimePicker) {
                        state.showTimePicker?.let { calendar ->
                            showTimePicker(calendar)
                        }
                    }

                    // Handle navigation
                    LaunchedEffect(state.navigateBack) {
                        if (state.navigateBack) {
                            findNavController().popBackStack()
                            viewModel.take(ExpenseEntryEvent.NavigationHandled)
                        }
                    }

                    // Handle snackbar
                    LaunchedEffect(state.showSnackbar) {
                        state.showSnackbar?.let {
                            mainHost.showSnackMessage(it)
                            viewModel.take(ExpenseEntryEvent.SnackbarShown)
                        }
                    }

                    ExpenseEntryScreen(
                        state = state,
                        onEvent = viewModel::take,
                        onNavigateBack = { findNavController().popBackStack() }
                    )
                }
            }
        }
    }

    private fun showDatePicker(calendar: Calendar) {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance().apply {
                    timeInMillis = calendar.timeInMillis
                    set(year, month, dayOfMonth)
                }
                viewModel.take(ExpenseEntryEvent.DateSelected(cal.timeInMillis))
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        ).apply {
            setOnCancelListener {
                viewModel.take(ExpenseEntryEvent.DatePickerDismissed)
            }
        }.show()
    }

    private fun showTimePicker(calendar: Calendar) {
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                viewModel.take(ExpenseEntryEvent.TimeSelected(hourOfDay, minute))
            },
            calendar[Calendar.HOUR_OF_DAY],
            calendar[Calendar.MINUTE],
            false
        ).apply {
            setOnCancelListener {
                viewModel.take(ExpenseEntryEvent.TimePickerDismissed)
            }
        }.show()
    }
}
