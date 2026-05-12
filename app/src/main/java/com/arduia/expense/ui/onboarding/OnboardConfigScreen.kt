package com.arduia.expense.ui.onboarding

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.components.ProExpenseButton
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R
import com.arduia.expense.ui.settings.ChooseCurrencyScreen
import com.arduia.expense.ui.settings.ChooseLanguageScreen
import kotlinx.coroutines.launch

@Composable
fun OnboardConfigScreen(
    onFinished: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface), // Background from XML
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 450.dp)
                .fillMaxSize()
                .padding(bottom = 16.dp) // Save space for button if needed, but button is in column
        ) {
            // App Icon
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(130.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )

            // Welcome Text
            Text(
                text = stringResource(id = R.string.app_welcome),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
            )

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp),
                userScrollEnabled = false // XML says binding.vpConfig.isUserInputEnabled = false
            ) { page ->
                when (page) {
                    0 -> ChooseLanguageScreen()
                    1 -> ChooseCurrencyScreen()
                }
            }

            // Continue Button
            ProExpenseButton(
                text = if (pagerState.currentPage == 0) stringResource(id = R.string.next) else "Continue to Home", // String resource might differ
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage == 0) {
                            pagerState.animateScrollToPage(1)
                        } else {
                            onFinished()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardConfigScreenPreview() {
    ProExpenseTheme {
        OnboardConfigScreen()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun OnboardConfigScreenDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        OnboardConfigScreen()
    }
}
