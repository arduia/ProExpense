package com.arduia.expense.ui.expense

import android.annotation.SuppressLint
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.lifecycle.ComputableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.PositionalDataSource
import java.util.concurrent.Executor

class FilterableLivePagedListBuilder <Key, Value>
    ( private val dataSourceFactory: DataSource.Factory<Key, Value>,
      private val pageConfig: PagedList.Config ){

    constructor(dataSource:  DataSource.Factory<Key, Value>, pageSize: Int):
        this(dataSource, PagedList.Config.Builder().setPageSize(pageSize).build())

    private var mInitialLoadKey : Key? = null

    private var mBoundaryCallback : PagedList.BoundaryCallback<Value> ? = null

    private var mFilterFunction: (List<Value>) -> List<Value> = { it }

    @SuppressLint("RestrictedApi")
    private var mFetchExecutor: Executor = ArchTaskExecutor.getIOThreadExecutor()

    private var dataSource: DataSource<Key, Value>? = null

    fun setInitialLoadKey(key: Key): FilterableLivePagedListBuilder<Key,Value> {
        mInitialLoadKey = key
        return this
    }

    fun setFetchExecutor(executor: Executor): FilterableLivePagedListBuilder<Key,Value>{
        mFetchExecutor = executor
        return this
    }


    fun setBoundaryCallback(callback: PagedList.BoundaryCallback<Value>){
        mBoundaryCallback = callback
    }

    fun filter(filterFunction: (Value) -> Boolean): FilterableLivePagedListBuilder<Key,Value>{
         mFilterFunction = { it.filter(filterFunction) }
        return this
    }

    fun invalidate(){
        dataSource?.invalidate()
    }


    @SuppressLint("RestrictedApi")
    fun build() = object : ComputableLiveData<PagedList<Value>>(mFetchExecutor){

        private var mList: PagedList<Value>? = null

        private val mCallback = DataSource.InvalidatedCallback {
            invalidate()
        }

        override fun compute(): PagedList<Value> {
            var initialLoadKey = mInitialLoadKey
            if(mList != null){
                @Suppress("UNCHECKED_CAST")
                initialLoadKey = mList!!.lastKey as Key
            }
            do {
                dataSource?.removeInvalidatedCallback(mCallback)
                val tmpSource = dataSourceFactory.create() as? PositionalDataSource<Value>
                @Suppress("UNCHECKED_CAST")
                dataSource = FilterableDataSourceWrapper(tmpSource!!, mFilterFunction) as? DataSource<Key, Value>

                dataSource!!.addInvalidatedCallback(mCallback)

                mList = PagedList.Builder(dataSource!!, pageConfig )
                    .setNotifyExecutor(ArchTaskExecutor.getMainThreadExecutor())
                    .setFetchExecutor(mFetchExecutor)
                    .setBoundaryCallback(mBoundaryCallback)
                    .setInitialKey(initialLoadKey)
                    .build()

            } while (mList!!.isDetached)

            return mList!!
        }

    }.liveData

}
