package com.arduia.expense.ui.expense

import androidx.room.InvalidationTracker
import com.arduia.expense.data.local.ExpenseEnt

class DatabaseInvalidateMediator: InvalidationTracker.Observer(arrayOf(ExpenseEnt.TABLE_NAME)) {
    private var observerList = mutableListOf<InvalidateObserver>()

    override fun onInvalidated(tables: MutableSet<String>) {
      observerList.forEach(InvalidateObserver::invalidate)
    }

    fun addObserver(observer: InvalidateObserver){
        if(observerList.indexOf(observer) != -1) return
        observerList.add(observer)
    }

    fun removeObserver(observer: InvalidateObserver){
        if(observerList.indexOf(observer) == -1) return
        observerList.remove(observer)
    }

}