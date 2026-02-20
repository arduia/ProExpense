package com.arduia.expense.ui.statistics

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.components.ProExpenseCard
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R
import com.arduia.expense.ui.statistics.molecule.StatisticEvent
import com.arduia.expense.ui.statistics.molecule.StatisticState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticScreen(
    state: StatisticState,
    onEvent: (StatisticEvent) -> Unit,
    onNavigationIconClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.statistics)) },
                navigationIcon = {
                    IconButton(onClick = onNavigationIconClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onEvent(StatisticEvent.FilterIconClicked) },
                        enabled = !state.isEmptyExpenseData
                    ) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_calendar),
                            contentDescription = "Filter"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 450.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                ProExpenseCard(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.category_statistics),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        
                        if (!state.isEmptyExpenseData && state.dateRangeInfo.isNotEmpty()) {
                            Text(
                                text = state.dateRangeInfo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        if (state.categoryStatisticList.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.no_data),
                                style = MaterialTheme.typography.bodyLarge, // Body1
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp)
                            )
                        } else {
                            state.categoryStatisticList.forEach { item ->
                                CategoryStatisticItem(
                                    name = stringResource(id = item.nameId),
                                    progress = item.progress,
                                    progressText = item.progressText
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryStatisticItem(
    name: String,
    progress: Float, // 0.0 to 1.0 (or 0 to 100 if reusing legacy) - Assuming XML max=100 so needs conversion if using 0-1
    progressText: String
) {
    // Convert 0-100 range from XML/Legacy logic to 0.0-1.0 for Compose ProgressIndicator if needed.
    // Assuming UI Model passes 0.0-1.0 or normalized value? 
    // Looking at XML: app:progressView_max="100", app:progressView_progress="50". 
    // Let's assume input 'progress' is normalized 0.0 to 1.0 for Compose. 
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "ProgressAnimation"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge, // Body1
                modifier = Modifier.align(Alignment.TopStart)
            )
            Text(
                text = progressText,
                style = MaterialTheme.typography.titleSmall, // Subtitle2
                modifier = Modifier.align(Alignment.TopEnd),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp) // Matches XML 20dp
                .clip(MaterialTheme.shapes.medium), // Matches radius 16dp roughly
            color = MaterialTheme.colorScheme.primary,
            trackColor = Color(0xFFE0E0E0), // ~ color_statistic_background
            strokeCap = StrokeCap.Round,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticScreenPreview() {
    val mockData = listOf(
        CategoryStatisticUiModel(R.string.food, 0.75f, "75%"),
        CategoryStatisticUiModel(R.string.transportation, 0.25f, "25%")
    )
    ProExpenseTheme {
        StatisticScreen(state = StatisticState(categoryStatisticList = mockData, isEmptyExpenseData = false, dateRangeInfo = "01 Aug 2023 - 31 Aug 2023"), onEvent = {})
    }
}

@Preview(name = "Empty State", showBackground = true)
@Composable
fun StatisticScreenEmptyPreview() {
    ProExpenseTheme {
        StatisticScreen(state = StatisticState(categoryStatisticList = emptyList(), isEmptyExpenseData = true), onEvent = {})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun StatisticScreenDarkPreview() {
     val mockData = listOf(
        CategoryStatisticUiModel(R.string.food, 0.75f, "75%")
    )
    ProExpenseTheme(darkTheme = true) {
        StatisticScreen(state = StatisticState(categoryStatisticList = mockData, isEmptyExpenseData = false, dateRangeInfo = "01 Aug 2023 - 31 Aug 2023"), onEvent = {})
    }
}
@Preview(showBackground = true)
@Composable
fun CategoryStatisticItemPreview() {
    ProExpenseTheme {
        CategoryStatisticItem(
            name = "Food",
            progress = 0.7f,
            progressText = "70%"
        )
    }
}
