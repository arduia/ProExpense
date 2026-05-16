package com.arduia.expense.ui.expenselogs.swipe

import java.lang.Exception
import java.util.HashMap

class SwipeStateHolder{

    private val stateList: HashMap<Int,Int> = hashMapOf()

    fun updateState(id: Int, @SwipeItemState.SwipeState state: Int){
        stateList[id] = state
    }

    fun getStateOrError(id: Int): Int{
        return getStateOrNull(id) ?: throw Exception("id($id) not found")
    }

    fun removeState(id: Int){
        stateList.remove(id)
    }

    fun getCount(@SwipeItemState.SwipeState state: Int): Int{

        if(stateList.isEmpty()) return 0

        return stateList.count {item -> item.value == state}
    }

    fun getSelectIdList(): List<Int>{

        if(stateList.isEmpty()) return emptyList()

        return  stateList.filter { it.value == SwipeItemState.STATE_LOCK_START }.map { it.key }
    }

    fun clear(){
        stateList.clear()
    }

    fun getStateOrNull(id: Int): Int? = stateList[id]

}
