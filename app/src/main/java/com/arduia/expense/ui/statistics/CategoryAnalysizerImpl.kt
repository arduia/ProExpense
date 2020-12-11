package com.arduia.expense.ui.statistics

import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.ui.common.ExpenseCategoryProvider
import java.lang.Exception
import java.text.DecimalFormat
import java.util.*

class CategoryAnalyzerImpl(private val categoryProvider: ExpenseCategoryProvider) :
    CategoryAnalyzer {

    private val valueHolderMap = hashMapOf<Int, Double>()

    private val decimalFormat = (DecimalFormat.getNumberInstance(Locale.ENGLISH) as DecimalFormat).apply {
        applyPattern("0.0")
    }
    override fun analyze(entities: List<ExpenseEnt>): List<CategoryStatisticVo> {
        entities.forEach {
            if (isNewKey(key = it.category)) {
                addNewCategoryAndValue(it.category, it.amount)
            } else {
                updateCategoryValue(it.category, it.amount)
            }
        }
        return getStatisticResultsVo()
    }

    private fun getStatisticResultsVo(): List<CategoryStatisticVo> {
        if (valueHolderMap.isEmpty()) return emptyList()

        val total: Double = valueHolderMap.values.sum()
//        val max: Double = valueHolderMap.maxOf { it.value }

        return valueHolderMap.map {
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

    private fun isNewKey(key: Int) = (valueHolderMap[key] == null)

    private fun addNewCategoryAndValue(key: Int, value: Float) {
        valueHolderMap[key] = value.toDouble()
    }

    private fun updateCategoryValue(key: Int, nextValue: Float) {
        val old = valueHolderMap[key]
            ?: throw Exception("Category($key) didn't exit. this should be add as new.")
        valueHolderMap[key] = old + nextValue
    }

}