package com.arduia.myacc.ui.entry

import androidx.annotation.DrawableRes
import com.arduia.myacc.ui.vto.CostCategory

class UpdateDataVto(val id: Int,
                    val name: String,
                    val date: Long,
                    val category: CostCategory,
                    val amount: String,
                    val note: String)