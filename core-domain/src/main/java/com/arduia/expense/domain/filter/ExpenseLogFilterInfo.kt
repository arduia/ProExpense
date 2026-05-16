package com.arduia.expense.domain.filter

import com.arduia.expense.ui.common.filter.Sorting

data class ExpenseLogFilterInfo(
    val dateRangeLimit: DateRange,
    val dateRangeSelected: DateRange,
    val sorting: Sorting
) {

    class Builder {
        private var limit: DateRange = ExpenseDateRange(0, 0)
        private var selected: DateRange = limit
        private var sorting = Sorting.DESC

        fun setDateLimit(range: DateRange): Builder {
            this.limit = range
            return this
        }

        fun setSelectedLimit(range: DateRange): Builder {
            this.selected = range
            return this
        }

        fun setSorting(sorting: Sorting): Builder {
            this.sorting
            return this
        }

        fun build() =
            ExpenseLogFilterInfo(
                dateRangeLimit = limit,
                dateRangeSelected = selected,
                sorting = sorting
            )
    }
}
