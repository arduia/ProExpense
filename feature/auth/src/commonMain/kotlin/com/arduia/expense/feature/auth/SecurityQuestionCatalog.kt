package com.arduia.expense.feature.auth

/**
 * Canonical security-question ids, in display order.
 *
 * Ids only — the question text is localized per platform (Android string resources, iOS
 * `Localizable.strings`). Shared so the id persisted by [PinAuthRepository.setSecurityQuestion] on
 * one platform resolves to the same question on the other, which matters for a restored backup.
 */
object SecurityQuestionCatalog {
    const val PET = "pet"
    const val CITY = "city"
    const val SCHOOL = "school"
    const val MAIDEN = "maiden"
    const val NICKNAME = "nickname"

    val IDS: List<String> = listOf(PET, CITY, SCHOOL, MAIDEN, NICKNAME)
}
