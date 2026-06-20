package com.arduia.expense.ui.auth

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.arduia.expense.R

@Composable
fun PinBiometricEffect(
    shouldTrigger: Boolean,
    onSuccess: () -> Unit,
    onFailure: () -> Unit,
) {
    val context = LocalContext.current
    var prompted by remember { mutableStateOf(false) }

    LaunchedEffect(shouldTrigger) {
        if (!shouldTrigger || prompted) return@LaunchedEffect
        val activity = context as? FragmentActivity
        if (activity == null) return@LaunchedEffect
        val canAuthenticate = BiometricManager.from(context).canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK,
        )
        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) return@LaunchedEffect
        prompted = true
        val executor = ContextCompat.getMainExecutor(context)
        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
                    ) {
                        onFailure()
                    }
                }

                override fun onAuthenticationFailed() {
                    onFailure()
                }
            },
        )
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.pin_entry_title))
            .setSubtitle(context.getString(R.string.pin_entry_subtitle))
            .setNegativeButtonText(context.getString(R.string.cancel))
            .build()
        prompt.authenticate(promptInfo)
    }
}
