package com.arduia.expense.ui.statistics

import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import java.lang.Exception
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.HashMap

class CategoryAnalyzerImpl(private val categoryProvider: ExpenseCategoryProvider) :
    CategoryAnalyzer {


    private val decimalFormat = (DecimalFormat.getNumberInstance(Locale.ENGLISH) as DecimalFormat).apply {
        applyPattern("0.0")
    }

    override fun analyze(entities: List<ExpenseEnt>): List<CategoryStatisticVo> {
        val valueHolder = hashMapOf<Int, Double>()
        entities.forEach {
            if (isNewKey(valueHolder, key = it.category)) {
                addNewCategoryAndValue(valueHolder, it.category, it.amount.getActual())
            } else {
                updateCategoryValue(valueHolder, it.category, it.amount.getActual())
            }
        }
        return getStatisticResultsVo(valueHolder)
    }

    private fun getStatisticResultsVo(valueHolder: HashMap<Int, Double>): List<CategoryStatisticVo> {
        if (valueHolder.isEmpty()) return emptyList()

        val total: Double = valueHolder.values.sum()

        return valueHolder.map {
            val category = categoryProvider.getCategoryByID(it.key)
            val percentage = calculatePercentage(it.value, total)
            return@map CategoryStatisticVo(category.name, percentage, "${decimalFormat.format(percentage)}%")
        }.sortedByDescending { it.progress }
    }

    private fun calculatePercentage(value: Double, total: Double): Float {
        if (total < value) throw Exception("Value($value) should be less than Total($total).")
        if (total <= 0) return 0f

        return ((value * 100) / total).toFloat()
    }

    private fun isNewKey(valueHolder: HashMap<Int, Double>, key: Int) = (valueHolder[key] == null)

    private fun addNewCategoryAndValue(valueHolder: HashMap<Int, Double>,key: Int, value: Float) {
        valueHolder[key] = value.toDouble()
    }

    private fun updateCategoryValue(valueHolder: HashMap<Int, Double>, key: Int, nextValue: Float) {
        val old = valueHolder[key]
            ?: throw Exception("Category($key) didn't exit. this should be add as new.")
        valueHolder[key] = old + nextValue
    }

}