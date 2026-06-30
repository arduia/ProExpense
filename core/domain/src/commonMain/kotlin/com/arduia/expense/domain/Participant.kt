package com.arduia.expense.domain

data class Participant(
    val id: ParticipantId,
    val name: String,
) {
    init {
        require(name.isNotBlank()) { "Participant name must not be blank" }
    }
}
