package com.arduia.expense.data.local

import androidx.annotation.IntDef
import androidx.annotation.IntRange

class UpdateStatusDataModel {
    companion object {
        const val STATUS_NO_UPDATE = -1
        const val STATUS_NORMAL_UPDATE = 1
        const val STATUS_CRITICAL_UPDATE = 2
    }
}
