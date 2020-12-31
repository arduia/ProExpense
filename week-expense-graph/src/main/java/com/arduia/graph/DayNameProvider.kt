package com.arduia.graph

/**
 * Provide Day Name String for Graph
 *
 * To Support Different Language
 */
interface DayNameProvider {

    fun getName(day: Int): String

}
