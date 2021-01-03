package com.arduia.expense.ui.backup

data class BackupUiModel(
    val name: String,
    val id: Int,
    val date: String,
    val items: String,
    val onProgress: Boolean
)