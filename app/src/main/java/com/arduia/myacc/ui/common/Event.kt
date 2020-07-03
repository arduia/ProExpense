package com.arduia.myacc.ui.common

import androidx.lifecycle.Observer

open class Event<out T> (private val content: T){

    var hasHandled = false
    private set

    fun getContentIfNotHandled(): T?{
        return when (hasHandled){
            true -> null
            false -> {
                hasHandled = true
                content
            }
        }
    }
    fun peekContent(): T = content
}


class EventObserver<T> (private val onEventUnhandledContent: (T)-> Unit): Observer<Event<T>>{

    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}

val EventUnit get() =  Event(Unit)

fun <T>event(content: T) = Event(content)