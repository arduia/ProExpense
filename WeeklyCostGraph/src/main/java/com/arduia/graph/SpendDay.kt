package com.arduia.graph

import androidx.annotation.IntDef
import androidx.annotation.IntRange

@IntDef(
    SpendGraph.SPEND_DAY_SUN,
    SpendGraph.SPEND_DAY_MON,
    SpendGraph.SPEND_DAY_TUE,
    SpendGraph.SPEND_DAY_WED,
    SpendGraph.SPEND_DAY_THU,
    SpendGraph.SPEND_DAY_FRI,
    SpendGraph.SPEND_DAY_SAT
    )
internal annotation class  SpendDay {
}
