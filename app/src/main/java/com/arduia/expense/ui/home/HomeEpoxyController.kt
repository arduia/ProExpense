package com.arduia.expense.ui.home

import android.view.View
import com.airbnb.epoxy.AutoModel
import com.airbnb.epoxy.EpoxyController
import com.arduia.expense.ui.expenselogs.ExpenseUiModel

class HomeEpoxyController(
    private val onRecentItemClick: (ExpenseUiModel) -> Unit,
    private val onMoreItemClick: View.OnClickListener
) :
    EpoxyController() {

    private var recentUiModel = RecentUiModel(listOf())

    @AutoModel
    lateinit var recent: RecentEpoxyModel_

    @AutoModel
    lateinit var incomeOutcomeModel: IncomeOutcomeEpoxyModel_

    @AutoModel
    lateinit var weeklyGraph: WeeklyGraphEpoxyModel_

    private var incomeOutcome = IncomeOutcomeUiModel("", "", "", "")
    private var weekGraph = WeeklyGraphUiModel("", mapOf())

    override fun buildModels() {
        incomeOutcome {
            id("incomeOutcome", 1)
            data(incomeOutcome)
        }

        weeklyGraph {
            id("graph", 2)
            data(weekGraph)
        }

        recent {
            id(3)
            moreClickListener(onMoreItemClick)
            recentData(recentUiModel)
            onItemClickListener(onRecentItemClick)
        }
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