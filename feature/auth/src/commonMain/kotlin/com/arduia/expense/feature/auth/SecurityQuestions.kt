package com.arduia.expense.feature.auth

data class SecurityQuestion(
    val id: String,
    val textKey: String,
)

object SecurityQuestions {
  val all: List<SecurityQuestion> = listOf(
      SecurityQuestion("pet", "security_question_pet"),
      SecurityQuestion("city", "security_question_city"),
      SecurityQuestion("school", "security_question_school"),
      SecurityQuestion("maiden", "security_question_maiden"),
      SecurityQuestion("nickname", "security_question_nickname"),
  )

    fun byId(id: String): SecurityQuestion? = all.firstOrNull { it.id == id }
}
