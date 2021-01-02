package com.arduia.core.arch

/**
 * Architecture Component
 *
 * Base Mapper Type for every mapping class
 */
interface Mapper <I,O>{

    fun map(input: I): O

    interface Builder<I, O>

    interface Factory<I,O>
}