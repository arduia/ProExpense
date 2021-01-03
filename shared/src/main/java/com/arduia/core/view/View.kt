package com.arduia.core.view

import android.view.View


fun View.asVisible() {
    visibility = View.VISIBLE
}

fun View.asInvisible(){
    visibility = View.INVISIBLE
}

fun View.asGone(){
    visibility = View.GONE
}
