package com.arduia.myacc.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class BaseLiveData<T> : MutableLiveData<T>(){
    fun asLiveData():LiveData<T> = this
}

typealias EventLiveData<T> = BaseLiveData<Event<T>>

infix fun <T> MutableLiveData<T>.set(value: T) {
    setValue(value)
}

infix fun <T> MutableLiveData<T>.post(value: T){
    postValue(value)
}