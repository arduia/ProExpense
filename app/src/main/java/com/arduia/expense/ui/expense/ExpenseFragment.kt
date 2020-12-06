package com.arduia.expense.ui.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.arduia.expense.ui.MainHost
import com.arduia.expense.R
import com.arduia.expense.databinding.FragExpenseLogsBinding
import com.arduia.expense.di.TopDropNavOption
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.ExpenseDetailDialog
import com.arduia.expense.ui.expense.filter.ExpenseLogFilterVo
import com.arduia.expense.ui.expense.filter.FilterDialog
import com.arduia.expense.ui.expense.filter.Sorting
import com.arduia.expense.ui.expense.swipe.SwipeItemCallback
import com.arduia.expense.ui.vto.ExpenseDetailsVto
import com.arduia.expense.ui.vto.ExpenseVto
import com.arduia.mvvm.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class ExpenseFragment : NavBaseFragment() {

    private lateinit var binding: FragExpenseLogsBinding

    private var filterDialog: FilterDialog? = null

    private val viewModel by viewModels<ExpenseViewModel>()

    private var adapter: ExpenseLogAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragExpenseLogsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupExpenseLogRecyclerview()
        setupViewModel()
        PageKeyedDataSource.LoadInitialParams<Int>(1,false)
    }

    private fun setupToolbar(){
        binding.tbExpense.setOnMenuItemClickListener listener@{
            if(it.itemId == R.id.filter){
                filterDialog?.dismiss()
                filterDialog = FilterDialog()
                filterDialog?.show(childFragmentManager, ExpenseLogFilterVo(0L, 0L, Sorting.ASC))
            }
            return@listener true
        }
    }

    private fun setupExpenseLogRecyclerview(){
        adapter = ExpenseLogAdapter(layoutInflater).apply {

        }
        val rvTouchHelper = ItemTouchHelper(SwipeItemCallback())
        rvTouchHelper.attachToRecyclerView(binding.rvExpense)
        binding.rvExpense.adapter = adapter

    }

    private fun setupViewModel(){
        viewModel.expenseList.observe(viewLifecycleOwner){
            adapter?.submitList(it)
        }
    }
}
