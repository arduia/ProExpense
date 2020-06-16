package com.arduia.graph

import java.lang.IllegalArgumentException


data class SpendPoint(  var day:Int, var rate:Float){
    init {
        if(day !in 1..7)  throw IllegalArgumentException(" inserted Date is not Between 1 to 7")

        if(rate !in 0f..1f) throw  IllegalArgumentException(" inserted Rate($rate) is not Between 0.0 to 1.0 float value")
    }
}
