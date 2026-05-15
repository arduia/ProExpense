package com.arduia.expense.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object SplashRoute

@Serializable
object OnboardingRoute

@Serializable
object HomeRoute

@Serializable
data class ExpenseEntryRoute(val expenseId: Long = -1L)

@Serializable
object ExpenseLogRoute

@Serializable
object StatisticsRoute

@Serializable
object SettingsRoute

@Serializable
object BackupRoute

@Serializable
object FeedbackRoute

@Serializable
object AboutRoute

@Serializable
data class WebRoute(val url: String, val title: String)
