package com.arduia.currencystore

abstract class Store<A: Number, S: Number>(val rate: Rate<S>) {

    abstract fun getActual(): A

    abstract fun getStore(): S

    abstract fun setStore(value: S)
}