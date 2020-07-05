package com.arduia.expense.ui.expense

import androidx.paging.PositionalDataSource

class FilterableDataSourceWrapper <T>(private val source: PositionalDataSource<T>,
                                      private val filterList:  List<T>.() -> List<T>):
    PositionalDataSource<T>(){

    override fun addInvalidatedCallback(onInvalidatedCallback: InvalidatedCallback) {
        source.addInvalidatedCallback(onInvalidatedCallback)
    }

    override fun removeInvalidatedCallback(onInvalidatedCallback: InvalidatedCallback) {
        source.removeInvalidatedCallback(onInvalidatedCallback)
    }

    override fun invalidate() {
        source.invalidate()
    }

    override fun isInvalid(): Boolean {
        return source.isInvalid
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
       source.loadRange(params,object : LoadRangeCallback<T>(){
           override fun onResult(data: MutableList<T>) {
               val result = data.filterList()
               callback.onResult(result)
           }
       })
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
        source.loadInitial(params, object:  LoadInitialCallback<T>(){
            override fun onResult(data: MutableList<T>, position: Int, totalCount: Int) {

                val result = data.filterList()
                val convertedCount = result.size
               callback.onResult(result , position, convertedCount)
            }

            override fun onResult(data: MutableList<T>, position: Int) {
                val result = data.filterList()
                callback.onResult(result, position)
            }
        })
    }

}
