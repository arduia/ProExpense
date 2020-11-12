package com.arduia.expense.ui.common

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.getColorList(@ColorRes color: Int) = ContextCompat.getColorStateList(requireContext(), color)