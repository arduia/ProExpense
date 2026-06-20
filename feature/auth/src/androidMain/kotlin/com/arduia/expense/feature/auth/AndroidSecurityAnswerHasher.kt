package com.arduia.expense.feature.auth

import java.security.MessageDigest

class AndroidSecurityAnswerHasher : SecurityAnswerHasher {
    override fun hash(normalizedAnswer: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(normalizedAnswer.encodeToByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
