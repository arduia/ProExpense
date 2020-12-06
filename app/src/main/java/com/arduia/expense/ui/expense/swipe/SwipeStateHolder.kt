package com.arduia.expense.ui.expense.swipe

import timber.log.Timber
import java.lang.Exception
import java.util.HashMap

class SwipeStateHolder{

    private val stateList: HashMap<Int,Int> = hashMapOf()

    fun updateState(id: Int, @SwipeItemState.SwipeState state: Int){
        stateList[id] = state
        Timber.d("updateState $id $state")
    }

    fun getStateOrError(id: Int): Int{
        return getStateOrNull(id) ?: throw Exception("id($id) not found")
    }

    fun removeState(id: Int){
        stateList.remove(id)
    }

    fun clear(){
        stateList.clear()
    }

    fun getStateOrNull(id: Int): Int? = stateList[id]

}
