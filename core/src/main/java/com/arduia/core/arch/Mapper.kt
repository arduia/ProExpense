package com.arduia.core.arch

interface Mapper <I,O>{

    fun map(input: I): O

    interface Builder<I, O>

    interface Factory<I,O>
}