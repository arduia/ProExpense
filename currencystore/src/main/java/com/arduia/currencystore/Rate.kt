package com.arduia.currencystore

interface Rate<I: Number> {
    fun getRate(): I
}