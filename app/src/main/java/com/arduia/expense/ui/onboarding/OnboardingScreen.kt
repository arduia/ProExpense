package com.arduia.expense.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arduia.expense.designsystem.theme.ProExpenseTheme
import com.arduia.expense.ui.component.currency.CurrencyUiModel
import com.arduia.expense.ui.component.language.LanguageUiModel
import com.arduia.expense.ui.onboarding.component.CurrencyPickerPage
import com.arduia.expense.ui.onboarding.component.LanguagePickerPage
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val languages: List<LanguageUiModel> = emptyList(),
    val selectedLanguageCode: String = "",
    val currencies: List<CurrencyUiModel> = emptyList(),
    val selectedCurrencyCode: String = "",
    val isComplete: Boolean = false,
)

@Composable
fun OnboardingScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    OnboardingContent(
        uiState = uiState,
        onNavigateToHome = onNavigateToHome,
        onLanguageSelect = viewModel::onLanguageSelect,
        onCurrencySelect = viewModel::onCurrencySelect,
        onFinish = viewModel::onFinish,
        modifier = modifier,
    )
}

@Composable
private fun OnboardingContent(
    uiState: OnboardingUiState,
    onNavigateToHome: () -> Unit,
    onLanguageSelect: (String) -> Unit,
    onCurrencySelect: (String) -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pagerState.pageCount - 1

    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                when (page) {
                    0 -> LanguagePickerPage(
                        languages = uiState.languages,
                        selectedCode = uiState.selectedLanguageCode,
                        onSelect = onLanguageSelect,
                    )
                    1 -> CurrencyPickerPage(
                        currencies = uiState.currencies,
                        selectedCode = uiState.selectedCurrencyCode,
                        onSelect = onCurrencySelect,
                    )
                }
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ProExpenseTheme.spacing.grid3)
                    .navigationBarsPadding(),
                onClick = {
                    if (isLastPage) {
                        onFinish()
                        onNavigateToHome()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
            ) {
                Text(text = if (isLastPage) "Get Started" else "Next")
            }
        }
    }
}

@Preview
@Composable
private fun PreviewOnboardingContent() {
    ProExpenseTheme {
        OnboardingContent(
            uiState = OnboardingUiState(),
            onNavigateToHome = {},
            onLanguageSelect = {},
            onCurrencySelect = {},
            onFinish = {},
        )
    }
}
