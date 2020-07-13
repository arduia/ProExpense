package com.arduia.graph

import java.lang.IllegalArgumentException


internal data class SpendPoint(val day: Int, val rate: Float){
    init {
        if(day !in 1..7) throw IllegalArgumentException(" inserted Date is not Between 1 to 7")

        if(rate !in -1f..1f) throw  IllegalArgumentException(" inserted Rate($rate) is not Between -1 to 1.0 float value")
    }
}
