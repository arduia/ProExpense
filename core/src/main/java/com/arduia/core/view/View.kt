package com.arduia.core.view

import android.view.View


fun View.asVisible() {
    visibility = View.GONE
}

fun View.asInvisible(){
    visibility = View.VISIBLE
}

fun View.asGone(){
    visibility = View.GONE
}
