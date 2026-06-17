package com.arduia.expense.ui.onboarding

data class OnboardingSlide(
    val title: String,
    val body: String,
)

const val ONBOARDING_PAGE_COUNT = 5

val OnboardingSlides = listOf(
    OnboardingSlide(
        title = "Welcome",
        body = "Your personal finance notebook.",
    ),
    OnboardingSlide(
        title = "Quick Log",
        body = "Log expenses in seconds — amount, category, done.",
    ),
    OnboardingSlide(
        title = "Shared Costs",
        body = "Split bills instantly — total, people, done.",
    ),
    OnboardingSlide(
        title = "Event Budget",
        body = "Plan and track any event budget — trips, weddings, parties.",
    ),
    OnboardingSlide(
        title = "Journal",
        body = "Review your spending like a diary, day by day.",
    ),
)
