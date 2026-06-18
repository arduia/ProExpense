package com.arduia.expense.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.formatWithSymbol
import com.arduia.expense.feature.history.HistoryRepository
import com.arduia.expense.feature.history.SummaryPeriod
import com.arduia.expense.feature.logging.LoggingRepository
import com.arduia.expense.feature.logging.PreviewLoggingRepository
import com.arduia.expense.feature.logging.ui.QuickLogFlow
import com.arduia.expense.ui.home.HomeScreen
import com.arduia.expense.ui.home.HomeShell
import com.arduia.expense.ui.home.HomeTab
import com.arduia.expense.ui.home.HomeUiState
import com.arduia.expense.ui.onboarding.FirstLaunchFlow
import com.arduia.expense.ui.onboarding.ProfileSetupState
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ExpenseApp(
    loggingRepository: LoggingRepository,
    historyRepository: HistoryRepository,
    modifier: Modifier = Modifier,
    zoneId: ZoneId = ZoneId.systemDefault(),
) {
    var showHome by remember { mutableStateOf(false) }
    var profile by remember { mutableStateOf(ProfileSetupState()) }
    var showQuickLog by remember { mutableStateOf(false) }
    var refreshToken by remember { mutableIntStateOf(0) }
    var highlightRecordId by remember { mutableStateOf<String?>(null) }
    var homeState by remember { mutableStateOf(HomeUiState("", "USD", "", "$0", emptyList())) }
    val scope = rememberCoroutineScope()
    val headerDateFormatter = remember { DateTimeFormatter.ofPattern("EEE · MMM d") }

    fun refreshHome(profileState: ProfileSetupState, highlightId: String? = highlightRecordId) {
        scope.launch {
            val now = System.currentTimeMillis()
            val summary = when (
                val result = historyRepository.getSummary(SummaryPeriod.MONTHLY, now)
            ) {
                is Result.Success -> result.data.totalInHomeCurrency
                is Result.Error -> Amount(0)
            }
            val records = when (
                val result = historyRepository.getRecords()
            ) {
                is Result.Success -> result.data.take(10)
                is Result.Error -> emptyList()
            }
            homeState = HomeUiState(
                profileName = profileState.name,
                homeCurrencyCode = profileState.homeCurrencyCode,
                dateLabel = ZonedDateTime.now(zoneId).format(headerDateFormatter),
                monthSpendLabel = summary.formatWithSymbol(profileState.homeCurrencyCode),
                recentRecords = records,
                highlightRecordId = highlightId,
            )
        }
    }

    LaunchedEffect(showHome, profile, refreshToken) {
        if (showHome) {
            refreshHome(profile)
        }
    }

    ProExpenseTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = ProExpenseTheme.colors.surface,
        ) { innerPadding ->
            if (showHome) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    HomeShell(
                        homeContent = {
                            HomeScreen(
                                state = homeState,
                                onSeeAll = {},
                                zoneId = zoneId,
                            )
                        },
                        selectedTab = HomeTab.Home,
                        onTabSelected = {},
                        onAddExpense = { showQuickLog = true },
                    )

                    if (showQuickLog) {
                        QuickLogFlow(
                            homeCurrencyCode = profile.homeCurrencyCode,
                            loggingRepository = loggingRepository,
                            onDismiss = { showQuickLog = false },
                            onSaved = { recordId ->
                                highlightRecordId = recordId
                                refreshToken++
                                showQuickLog = false
                            },
                        )
                    }
                }
            } else {
                FirstLaunchFlow(
                    onComplete = { completedProfile ->
                        profile = completedProfile
                        showHome = true
                    },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun ExpenseAppHomePreview() {
    ProExpenseTheme {
        ExpenseAppHomePreviewHost()
    }
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun ExpenseAppQuickLogPreview() {
    ProExpenseTheme {
        ExpenseAppQuickLogPreviewHost()
    }
}

@Composable
internal fun ExpenseAppHomePreviewHost(
    modifier: Modifier = Modifier,
) {
    val sampleState = HomeUiState(
        profileName = "Maya",
        homeCurrencyCode = "USD",
        dateLabel = "Wed · May 25",
        monthSpendLabel = "$12.50",
        recentRecords = emptyList(),
    )
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ProExpenseTheme.colors.surface,
    ) {
        HomeShell(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            homeContent = {
                HomeScreen(state = sampleState, onSeeAll = {})
            },
            selectedTab = HomeTab.Home,
            onTabSelected = {},
            onAddExpense = {},
        )
    }
}

@Composable
internal fun ExpenseAppQuickLogPreviewHost(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        ExpenseAppHomePreviewHost()
        QuickLogFlow(
            homeCurrencyCode = "USD",
            loggingRepository = PreviewLoggingRepository,
            onDismiss = {},
            onSaved = {},
        )
    }
}
