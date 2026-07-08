package com.arduia.expense.ui.design

/**
 * A language's own name is written in itself, not translated per-locale (matches every major
 * app's language picker) — so [displayName] is a fixed literal, not a string resource.
 */
enum class AppLanguage(val tag: String, val displayName: String) {
    ENGLISH("en", "English"),
    MYANMAR("my", "မြန်မာ"),
    THAI("th", "ไทย"),
    ;

    companion object {
        val DEFAULT = ENGLISH

        fun fromTag(tag: String): AppLanguage = entries.firstOrNull { it.tag == tag } ?: DEFAULT
    }
}
