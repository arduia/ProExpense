package com.arduia.expense.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.arduia.expense.R
import com.arduia.expense.feature.auth.SecurityQuestions

@Composable
fun securityQuestionLabels(): List<Pair<String, String>> =
    SecurityQuestions.all.map { question ->
        val label = when (question.textKey) {
            "security_question_pet" -> stringResource(R.string.security_question_pet)
            "security_question_city" -> stringResource(R.string.security_question_city)
            "security_question_school" -> stringResource(R.string.security_question_school)
            "security_question_maiden" -> stringResource(R.string.security_question_maiden)
            "security_question_nickname" -> stringResource(R.string.security_question_nickname)
            else -> question.textKey
        }
        question.id to label
    }

@Composable
fun securityQuestionLabel(questionId: String?): String? {
    if (questionId == null) return null
    return securityQuestionLabels().firstOrNull { it.first == questionId }?.second
}
