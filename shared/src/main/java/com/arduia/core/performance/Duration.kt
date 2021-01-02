package com.arduia.core.performance

import android.util.Log.d
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

/**
 * To Measure Duration of A function or statements
 */

fun <R> printDurationNano(tag:String, suffix:String = "", execution:() -> R): R{

    var result:R? = null

    val duration = measureNanoTime {
        result = execution()
    }

    d(tag,"$suffix Duration is $duration ns")

    return result ?: throw Exception("No Result in execution Lambda")
}

fun <R> printDurationMilli(tag:String, suffix:String = "", execution:() -> R):R{

    var result:R? = null

    val duration = measureTimeMillis {
        result = execution()
    }

    d(tag,"$suffix Duration is $duration ms")

    return result ?: throw Exception("No Result in execution Lambda")
}