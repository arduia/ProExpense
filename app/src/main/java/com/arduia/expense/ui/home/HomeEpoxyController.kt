package com.arduia.expense.ui.home

import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.arduia.expense.ui.expenselogs.ExpenseUiModel

class HomeEpoxyController(
    private val onRecentItemClick: (ExpenseUiModel) -> Unit,
    private val onMoreItemClick: View.OnClickListener
) : EpoxyController() {

    private var recentUiModel = RecentUiModel(listOf())
    private var incomeOutcome = IncomeOutcomeUiModel("", "", "", "")
    private var weekGraph = WeeklyGraphUiModel("", mapOf())

    override fun buildModels() {
        IncomeOutcomeEpoxyModel_()
            .id("income_outcome")
            .data(incomeOutcome)
            .addTo(this)

        WeeklyGraphEpoxyModel_()
            .id("weekly_graph")
            .data(weekGraph)
            .addTo(this)

        RecentEpoxyModel_()
            .id("recent")
            .moreClickListener(onMoreItemClick)
            .recentData(recentUiModel)
            .onItemClickListener(onRecentItemClick)
            .addTo(this)
    }

    fun updateRecent(data: RecentUiModel) {
        this.recentUiModel = data
        requestModelBuild()
    }

    fun updateIncomeOutcome(data: IncomeOutcomeUiModel) {
        this.incomeOutcome = data
        requestModelBuild()
    }

    fun updateGraphRate(data: WeeklyGraphUiModel) {
        this.weekGraph = data
        requestModelBuild()
    }
}