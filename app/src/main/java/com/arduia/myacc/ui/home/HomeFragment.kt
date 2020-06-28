package com.arduia.myacc.ui.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arduia.graph.SpendPoint
import com.arduia.myacc.R
import com.arduia.myacc.data.local.Transaction
import com.arduia.myacc.databinding.FragHomeBinding
import com.arduia.myacc.ui.BaseFragment
import com.arduia.myacc.ui.adapter.CostAdapter
import com.arduia.myacc.ui.adapter.MarginItemDecoration
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt


class HomeFragment : BaseFragment(){

    private val viewBinding by lazy {
            FragHomeBinding.inflate(layoutInflater).apply {
                lifecycle.addObserver(viewModel)
                setupView()
                setupViewModel()
            }
    }

    private val viewModel by viewModels<HomeViewModel>()

    private val costAdapter by lazy { CostAdapter(layoutInflater) }

    private val inputMethod by lazy {
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = viewBinding.root

    //Setup View
    private fun FragHomeBinding.setupView(){

        fbAdd.setColorFilter(Color.WHITE)
        fbAdd.setOnClickListener { mlHome.transitionToState(R.id.expended_entry_constraint_set) }

        btnMenuOpen.setOnClickListener { openDrawer() }

        rvRecent.adapter = costAdapter
        rvRecent.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        rvRecent.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.spacing_list_item).toInt(),
                resources.getDimension(R.dimen.margin_list_item).toInt()
            ))

        with(sheetEntry){
            btnEntryClose.setOnClickListener {
                mlHome.transitionToStart()
            }
            btnSave.setOnClickListener {
                saveSpend()
            }

            tvDescription.setOnEditorActionListener listener@{ _, aID, _ ->
                if(aID == EditorInfo.IME_ACTION_NEXT){
                    hideKeyboard()
                }
                return@listener true
            }
        }

    }

    private fun showKeyboard(){
        inputMethod.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun hideKeyboard(){
        inputMethod.hideSoftInputFromWindow(
            viewBinding.sheetEntry.tvDescription.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS)
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
        viewBinding.mlHome.transitionToStart()
        clearSpendSheet()
    }

    private fun clearSpendSheet(){
        viewBinding.sheetEntry.tvName.setText("")
        viewBinding.sheetEntry.tvCost.setText("")
        viewBinding.sheetEntry.tvDescription.setText("")
    }

    private fun setupViewModel(){
        viewModel.recentData.observe(viewLifecycleOwner, Observer {
          costAdapter.submitList(it)
        })
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
        add(SpendPoint(5, randomRate()))
        add(SpendPoint(6, randomRate()))
        add(SpendPoint(7, randomRate()))
    }

    private fun randomRate() = (Random.nextInt(0..100).toFloat() / 100)

}
