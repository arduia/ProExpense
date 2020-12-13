package com.arduia.expense.ui.entry

import android.os.PatternMatcher
import android.text.InputFilter
import android.text.Spanned
import timber.log.Timber
import java.util.regex.Pattern

class FloatingInputFilter : InputFilter {

    private val floatingPattern = Pattern.compile("[+-]?([0-9]{0,8}([.][0-9]{0,2})?|[.][0-9]{0,2})")
    // 123
    // 123.
    // 123.456
    // .456


    override fun filter(
        charSequence: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence {

        if (charSequence == null) return ""
        if (dest == null) return ""

        val numberText = dest.toString().replaceRange(dstart, dend, charSequence)
        val isMatches = floatingPattern.matcher(numberText).matches()
        return if (isMatches) charSequence else ""
    }

}