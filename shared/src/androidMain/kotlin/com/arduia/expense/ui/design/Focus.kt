package com.arduia.expense.ui.design

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

/**
 * Requests focus and shows the keyboard once a screen's primary input composes — the concrete,
 * most common instance of the Product UX lens check in AGENTS.md Step 3: the user's whole intent
 * in opening the screen is to fill that field in, so it should already be ready to type into.
 */
@Composable
fun rememberAutoFocusRequester(): FocusRequester {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    return focusRequester
}
