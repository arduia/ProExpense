package com.arduia.expense.ui.entry

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arduia.core.extension.px
import com.arduia.expense.ui.MainHost
import com.arduia.expense.R
import com.arduia.expense.databinding.FragExpenseEntryBinding
import com.arduia.expense.ui.common.*
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.mvvm.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ExpenseEntryFragment : Fragment() {

    private var _binding: FragExpenseEntryBinding? = null
    private val binding get() = _binding!!

    private val args: ExpenseEntryFragmentArgs by navArgs()

    private val viewModel by viewModels<ExpenseEntryViewModel>()

    @Inject
    lateinit var mainHost: MainHost

    private lateinit var categoryAdapter: CategoryListAdapter

    @Inject
    lateinit var categoryProvider: ExpenseCategoryProvider

    private lateinit var dateFormat: DateFormat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragExpenseEntryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateFormat = SimpleDateFormat("d MMM yyyy h:mm a", Locale.ENGLISH)
        setupView()
        setupViewModel()
        chooseEntryMode()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        updateCategoryListAfterAnimation()
    }

    private fun setupView() {
        setupCategoryListAdapter()
        setupCategoryListView()
        setupEntryCloseButton()
        setupEntryAmountEditText()
        setupLockButton()
    }

    private fun showTimePickerDialog(calendar: Calendar) {
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                viewModel.selectTime(hourOfDay, minute, 0)
            },
            calendar[Calendar.HOUR_OF_DAY],
            calendar[Calendar.MINUTE],
            false
        ).show()
    }

    private fun showDatePicker(time: Calendar) {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                time.set(year, month, dayOfMonth)
                viewModel.selectDateTime(time.timeInMillis)
            },
            time[Calendar.YEAR],
            time[Calendar.MONTH],
            time[Calendar.DAY_OF_MONTH]
        ).show()

    }

    private fun setupLockButton() {
        binding.cvLock.setOnClickListener {
            viewModel.invertLockMode()
        }
    }

    private fun setupViewModel() {
        observeDataInsertedEvent()
        observeDataUpdatedEvent()
        observeEntryModeEvent()
        observeEventExpenseDataState()
        observeSelectedCategoryState()
        observeOnLockMode()
        observeOnNext()
        observeDate()
        observeCurrencySymbol()
        observeDateTimeSelectEvent()
    }

    private fun observeCurrencySymbol() {
        viewModel.currencySymbol.observe(viewLifecycleOwner, binding.edlAmount::setSuffixText)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideInputKeyboard()
    }

    private fun setupCategoryListView() {
        binding.rvCategory.adapter = categoryAdapter
        binding.rvCategory.addItemDecoration(
            MarginItemDecoration(
                requireContext().px(4),
                requireContext().px(4),
                true
            )
        )
        binding.toolbar.setOnMenuItemClickListener menu@{
            when (it.itemId) {
                R.id.calendar -> {
                    viewModel.onDateSelect()
                }
                R.id.time -> {
                    viewModel.onTimeSelect()
                }
            }
            return@menu true
        }
    }

    private fun setupEntryAmountEditText() {
        binding.edtAmount.filters = arrayOf(FloatingInputFilter())
    }

    private fun setupCategoryListAdapter() {
        categoryAdapter = CategoryListAdapter(layoutInflater)
        categoryAdapter.setOnItemClickListener {
            viewModel.selectCategory(it)
        }
    }

    private fun setupEntryCloseButton() {
        binding.toolbar.setNavigationOnClickListener {
            backToPreviousFragment()
        }
    }

    private fun observeDateTimeSelectEvent() {
        viewModel.onChooseDateShow.observe(viewLifecycleOwner, EventObserver {
            showDatePicker(it)
        })

        viewModel.onChooseTimeShow.observe(viewLifecycleOwner, EventObserver {
            showTimePickerDialog(it)
        })
    }

    private fun observeSelectedCategoryState() {
        viewModel.selectedCategory.observe(viewLifecycleOwner, Observer { item ->
            setSelectedItem(item)
        })
    }

    private fun observeDataInsertedEvent() {
        viewModel.onDataInserted.observe(viewLifecycleOwner, EventObserver {
            backToPreviousFragment()
        })
    }

    private fun observeOnNext() {
        viewModel.onNext.observe(viewLifecycleOwner, EventObserver {
            cleanUi()
            focusOnName()
            showItemSaved()
        })
    }

    private fun showItemSaved() {
        mainHost.showSnackMessage("Saved!")
    }

    private fun observeDataUpdatedEvent() {
        viewModel.onDataUpdated.observe(viewLifecycleOwner, EventObserver {
            showDataUpdatedMessage()
            backToPreviousFragment()
        })
    }


    private fun observeOnLockMode() {
        viewModel.lockMode.observe(viewLifecycleOwner) {
            //Replace with Drawable State Lists
            when (it) {

                LockMode.LOCKED -> {
                    binding.cvLock.backgroundTintList =
                        ColorStateList.valueOf(requireContext().themeColor(R.attr.colorPrimary))
                    binding.imvLock.imageTintList =
                        ColorStateList.valueOf(requireContext().themeColor(R.attr.colorOnPrimary))
                    binding.btnSave.text = getString(R.string.next)
                }

                LockMode.UNLOCK -> {
                    binding.cvLock.backgroundTintList =
                        ColorStateList.valueOf(requireContext().themeColor(R.attr.colorSurface))
                    binding.imvLock.imageTintList =
                        ColorStateList.valueOf(requireContext().themeColor(R.attr.colorOnSurface))
                    binding.btnSave.text = getString(R.string.save)
                }

            }
        }
    }

    private fun cleanUi() {
        with(binding) {
            edtAmount.setText("")
            edtName.setText("")
            edtNote.setText("")
        }
    }

    private fun focusOnName() {
        binding.edtName.requestFocus()
    }

    private fun observeEntryModeEvent() {
        viewModel.onCurrentModeChanged.observe(viewLifecycleOwner, EventObserver { mode ->
            changeViewToSelectedMode(mode)
        })
    }

    private fun observeEventExpenseDataState() {
        viewModel.entryData.observe(viewLifecycleOwner, Observer { data ->
            bindExpenseDetail(data)
        })
    }

    private fun observeDate() {
        viewModel.currentEntryTime.observe(viewLifecycleOwner) {
            binding.toolbar.subtitle = dateFormat.format(Date(it))
        }
    }

    private fun chooseEntryMode() {

        val argId = args.expenseId
        val isInvalidId = argId < 0
        if (isInvalidId) {
            viewModel.chooseSaveMode()
        } else {
            viewModel.chooseUpdateMode()
        }
    }

    private fun changeViewToSelectedMode(mode: ExpenseEntryMode) {
        when (mode) {
            ExpenseEntryMode.UPDATE -> changeToUpdateMode()
            ExpenseEntryMode.INSERT -> changeToSaveMode()
        }
    }

    private fun bindExpenseDetail(data: ExpenseUpdateDataVto) {
        binding.edtName.setText(data.name)
        binding.edtAmount.setText(data.amount)
        binding.edtNote.setText(data.note)
        viewModel.selectCategory(data.category)
        categoryAdapter.submitList(listOf(data.category))
    }

    private fun updateCategoryListAfterAnimation() {
        lifecycleScope.launch(Dispatchers.Main) {
            val duration = resources.getInteger(R.integer.duration_entry_pop_up).toLong()
            delay(duration)
            updateCategoryList()
        }
    }

    private fun updateCategoryList() {
        val list = getCategoryList()
        val item = categoryAdapter.selectedItem
        if (item != null) {
            moveItemToFirstIndex(list = list, item = item)
        }
        categoryAdapter.submitList(list)
    }

    private fun moveItemToFirstIndex(list: MutableList<ExpenseCategory>, item: ExpenseCategory) {
        list.remove(item)
        list.add(0, item)
    }

    private fun getCategoryList(): MutableList<ExpenseCategory> {
        return categoryProvider.getCategoryList().toMutableList()
    }

    private fun changeToUpdateMode() = with(binding) {
        toolbar.title = getString(R.string.update_data)
        btnSave.text = getString(R.string.update)
        btnSave.setOnClickListener { updateData() }
        viewModel.setCurrentExpenseId(args.expenseId)
        binding.cvLock.isEnabled = false
        binding.cvLock.visibility = View.GONE
    }

    private fun changeToSaveMode() = with(binding) {
        toolbar.title = getString(R.string.expense_entry)
        btnSave.text = getString(R.string.save)
        btnSave.setOnClickListener { saveData() }
        edtName.requestFocus()
        setInitialDefaultCategory()
        binding.cvLock.visibility = View.VISIBLE
    }

    private fun setInitialDefaultCategory() {
        val default = categoryProvider.getCategoryByID(ExpenseCategory.FOOD)
        categoryAdapter.submitList(listOf(default))
        viewModel.selectCategory(default)
    }

    private fun saveData() {
        val expense = getCurrentExpenseDetail()
        val isEmpty = expense.amount.isEmpty()

        if (isEmpty) {
            showAmountEmptyError()
            return
        }
        viewModel.saveExpenseData(expense)
    }


    private fun updateData() {
        val expense = getCurrentExpenseDetail()
        val isEmpty = expense.amount.isEmpty()

        if (isEmpty) {
            showAmountEmptyError()
            return
        }
        viewModel.updateExpenseData(expense)
    }

    private fun showAmountEmptyError() {
        binding.edtAmount.error = getString(R.string.empty_cost)
    }

    private fun getCurrentExpenseDetail(): ExpenseDetailsVto {

        val name = getNameText()
        val amount = getAmountText()
        val note = getNoteText()
        val category = getSelectedCategory()
        val id = getExpenseId()

        return ExpenseDetailsVto(
            id = id,
            name = name,
            date = "",
            category = category.id,
            amount = amount,
            finance = "",
            note = note,
            symbol = ""
        )
    }

    private fun getExpenseId(): Int {
        val argId = args.expenseId
        val isInvalid = (argId < 0)
        return if (isInvalid) 0
        else argId
    }

    private fun getNameText() = binding.edtName.text.toString()

    private fun getAmountText() = binding.edtAmount.text.toString()

    private fun getNoteText() = binding.edtNote.text.toString()

    private fun getSelectedCategory() = categoryAdapter.selectedItem
        ?: throw Exception("Category Item is not selected!")

    private fun hideInputKeyboard() {
        val inputMethodManager =
            (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        inputMethodManager?.hideSoftInputFromWindow(binding.edtName.windowToken, 0)
    }

    private fun showDataUpdatedMessage() {
        mainHost.showSnackMessage(getString(R.string.data_updated))
    }

    private fun backToPreviousFragment() {
        findNavController().popBackStack()
    }

    private fun setSelectedItem(item: ExpenseCategory) {
        categoryAdapter.selectedItem = item
    }

    companion object {
        private const val TAG = "ExpenseUpdate"
    }

}
