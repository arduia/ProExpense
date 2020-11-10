package com.arduia.expense.ui.entry

import android.text.InputFilter
import android.text.Spanned
import timber.log.Timber

class FloatingInputFilter(private val decimalLength: Int = 0) : InputFilter {


    override fun filter(
        charSequence: CharSequence?,
        p1: Int,
        p2: Int,
        dest: Spanned?,
        p4: Int,
        p5: Int
    ): CharSequence {

        if (charSequence == null) return ""
        if(dest == null) return ""

        if(dest.count { it=='.' } == 1){
            val length = dest.length
            if(dest.indexOf('.') <= (length -1 - decimalLength) )
                return ""
        }
        return charSequence
    }

}