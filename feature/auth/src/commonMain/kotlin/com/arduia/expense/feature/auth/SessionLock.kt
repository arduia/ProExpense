package com.arduia.expense.feature.auth

/**
 * US-AUTH-4's default is "always re-lock on background." The opt-in per-session setting
 * (Settings > "Stay unlocked while switching apps") keeps the app unlocked across an app-switch
 * (`ON_STOP`/`ON_START`) instead — a real process restart still clears the caller's in-memory
 * unlocked flag regardless of this setting, since that state never survives a fresh process.
 */
fun shouldRelockOnBackground(stayUnlockedInBackground: Boolean): Boolean = !stayUnlockedInBackground
