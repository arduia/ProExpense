package com.arduia.expense.ui.vto

data class BackupVto(
    val name: String,
    val id: Int,
    val date: String,
    val items: String,
    val onProgress: Boolean
)