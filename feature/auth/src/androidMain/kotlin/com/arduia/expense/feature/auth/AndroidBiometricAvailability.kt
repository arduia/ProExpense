package com.arduia.expense.feature.auth

import android.content.Context
import androidx.biometric.BiometricManager

class AndroidBiometricAvailability(
    private val context: Context,
) : BiometricAvailability {
    override fun isAvailable(): Boolean {
        val manager = BiometricManager.from(context)
        return manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
            BiometricManager.BIOMETRIC_SUCCESS
    }
}
