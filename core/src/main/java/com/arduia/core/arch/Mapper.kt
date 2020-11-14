package com.arduia.core.arch

interface Mapper <I,O>{
    fun map(input: I): O
}