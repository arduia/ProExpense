package com.arduia.expense.ui.common.ext

import android.app.ActivityOptions
import androidx.fragment.app.Fragment
import com.arduia.expense.R

fun Fragment.restartActivity() {
    val currentActivity = requireActivity()
    val intent = currentActivity.intent
    currentActivity.finish()
    val animationBundle =
        ActivityOptions.makeCustomAnimation(
            requireContext(),
            R.anim.expense_enter_left, android.R.anim.fade_out
        ).toBundle()
    startActivity(intent, animationBundle)
}