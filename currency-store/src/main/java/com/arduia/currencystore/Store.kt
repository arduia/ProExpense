package com.arduia.currencystore

/**
 * To exchange between store currency value and actual currency value
 * Use in decimal value accuracy to convert after an expense amount is stored in database
 */
abstract class Store<A: Number, S: Number>(val rate: Rate<S>) {

    abstract fun getActual(): A

    abstract fun getStore(): S

    abstract fun setStore(value: S)

}