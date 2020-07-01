package com.arduia.myacc.ui.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log.d
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arduia.graph.SpendPoint
import com.arduia.myacc.R
import com.arduia.myacc.data.local.Transaction
import com.arduia.myacc.databinding.FragHomeBinding
import com.arduia.myacc.ui.BaseFragment
import com.arduia.myacc.ui.common.CategoryProvider
import com.arduia.myacc.ui.common.MarginItemDecoration
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt


class HomeFragment : BaseFragment(){

    private val viewBinding by lazy {  createViewBinding() }

    private val viewModel by viewModels<HomeViewModel>()

    private val recentAdapter by lazy {
        RecentListAdapter(
            layoutInflater )
    }

    private val linearLayoutManager by lazy {
        LinearLayoutManager(requireContext())
    }

    private val inputMethod by lazy {
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = viewBinding.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycle.addObserver(viewModel)
        setupView()
        setupViewModel()
    }

    //Setup View
    private fun setupView(){

        viewBinding.fbAdd.setColorFilter(Color.WHITE)

        viewBinding.fbAdd.setOnClickListener { showSheet() }

        viewBinding.btnMenuOpen.setOnClickListener { openDrawer() }

        viewBinding.btnMoreTransaction.setOnClickListener {
            findNavController().navigate(R.id.dest_transaction)
        }

        viewBinding.sheetEntry.btnEntryClose.setOnClickListener {
            clearSpendSheet()
            hideSheet()
        }

        viewBinding.sheetEntry.btnSave.setOnClickListener {
            saveSpend()
        }

        viewBinding.sheetEntry.tvCost.addTextChangedListener {
            viewBinding.sheetEntry.btnSave.isEnabled = !it.isNullOrEmpty()
        }

        viewBinding.sheetEntry.tvDescription.setOnEditorActionListener listener@{ _, aID, _ ->
            if(aID == EditorInfo.IME_ACTION_NEXT){
                saveSpend()
            }
            return@listener true
        }
        viewBinding.sheetEntry.chipCategory.setOnCheckedChangeListener { _, id ->
            viewBinding.sheetEntry.chipCategory.check(id)
        }
    }

    private fun setupViewModel(){
        viewModel.recentData.observe(viewLifecycleOwner, Observer {
            recentAdapter.submitList(it)
        })

        viewModel.expenseDataChanged.observe(viewLifecycleOwner, Observer {
            recentAdapter.notifyDataSetChanged()
        })
    }
    private fun hideKeyboard(){
        inputMethod.hideSoftInputFromWindow(viewBinding.root.windowToken, 0)
    }

    private fun hideSheet(){
        viewBinding.root.requestFocus()
        viewBinding.mlHome.transitionToStart()
        hideKeyboard()
    }

    private fun showSheet(){
        viewBinding.mlHome.transitionToState(R.id.expended_entry_constraint_set)
    }
    private fun saveSpend(){

        val name = viewBinding.sheetEntry.tvName.text.toString()
        val cost = viewBinding.sheetEntry.tvCost.text.toString()
        val description = viewBinding.sheetEntry.tvDescription.text.toString()

        if(cost.isEmpty()){
            viewBinding.sheetEntry.tvCost.error = "Cost is Empty"
            return
        }

        val saveTransaction = Transaction(
            name = name,
            value = cost.toLongOrNull() ?: throw Exception("Phrase Exception for $cost"),
            note = description,
            expense = "Income",
            category = "Fast",
            finance_type = "CASH",
            created_date = Date().time,
            modified_date = Date().time
        )

        viewModel.saveSpendData(transaction = saveTransaction)
        clearSpendSheet()
        hideSheet()
    }

    private fun clearSpendSheet(){
        viewBinding.sheetEntry.tvName.setText("")
        viewBinding.sheetEntry.tvCost.setText("")
        viewBinding.sheetEntry.tvDescription.setText("")
    }


    override fun onResume() {
        super.onResume()
        viewBinding.imgGraph.spendPoints = getSamplePoints()
    }

    private fun getSamplePoints() =
        mutableListOf<SpendPoint>().apply {
        add(SpendPoint(1, randomRate()))
        add(SpendPoint(2, randomRate()))
        add(SpendPoint(3, randomRate()))
        add(SpendPoint(4, randomRate()))
//        add(SpendPoint(5, randomRate()))
//        add(SpendPoint(6, randomRate()))
//        add(SpendPoint(7, randomRate()))
    }

    private fun randomRate() = (Random.nextInt(0..100).toFloat() / 100)

    private fun createViewBinding() =
        FragHomeBinding.inflate(layoutInflater).apply {
            //Once Configuration
        rvRecent.adapter = recentAdapter
        rvRecent.layoutManager = linearLayoutManager
        rvRecent.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.spacing_list_item).toInt(),
                resources.getDimension(R.dimen.margin_list_item).toInt()
            ))
    }

    companion object{
        private const val TAG = "MY_HomeFragment"
    }

}
