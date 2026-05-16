package com.arduia.expense.domain.filter

interface Range<S, E> {
    val start: S
    val end: E
}