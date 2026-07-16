package com.arduia.expense.feature.auth

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// US-AUTH-4 Scenario 4 (default re-lock) and Scenario 5 (opt-in stay-unlocked).
class ShouldRelockOnBackgroundTest {
    @Test
    fun invoke_relocksOnBackgroundWhenSettingIsDefaultOff() {
        assertTrue(shouldRelockOnBackground(stayUnlockedInBackground = false))
    }

    @Test
    fun invoke_staysUnlockedOnBackgroundWhenSettingIsOn() {
        assertFalse(shouldRelockOnBackground(stayUnlockedInBackground = true))
    }
}
