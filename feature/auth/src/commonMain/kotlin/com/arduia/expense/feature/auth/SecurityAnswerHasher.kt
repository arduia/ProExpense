package com.arduia.expense.feature.auth

interface SecurityAnswerHasher {
    fun hash(normalizedAnswer: String): String
}
