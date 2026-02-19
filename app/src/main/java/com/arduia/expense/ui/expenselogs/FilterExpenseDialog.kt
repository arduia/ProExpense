package com.arduia.expense.ui.expenselogs

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.components.ProExpenseButton
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R
// import androidx.compose.material3.ExperimentalMaterial3Api // For DatePicker if used, but XML used TextViews/Cards so custom picker logic

@Composable
fun FilterExpenseDialog(
    onApplyClick: () -> Unit = {},
    startDate: String = "Dec 11, 2018", // Simplified for preview
    endDate: String = "Feb 21, 2011"
) {
    var selectedSortIndex by remember { mutableIntStateOf(0) } // 0 = Desc, 1 = Asc
    val sortOptions = listOf("Desc", "Asc") // Replace with String Resources ideally

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 450.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.filter),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Date Range
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // From
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(id = R.string.from),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    DateCard(month = "Dec", day = "11", year = "2018")
                }



                // To
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(id = R.string.to), // UI says "to" under "from" baseline in XML roughly
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.surface // Hide label for alignment if needed, but XML shows it
                    )

                    // Wait, logic above is duplicated.
                    // XML: tv_from constraint, tv_to constraint baseline to tv_from.
                    // Let's just render DateCard.
                }

                // To (Corrected)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text( // XML actually aligns "to" text with "from" text
                        text = stringResource(id = R.string.to),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                     DateCard(month = "Feb", day = "21", year = "2011")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.order_date_in),
                style = MaterialTheme.typography.bodyLarge,
                 modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Sort Toggle
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                sortOptions.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = sortOptions.size),
                        onClick = { selectedSortIndex = index },
                        selected = index == selectedSortIndex
                    ) {
                        Text(label)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProExpenseButton(
                text = stringResource(id = R.string.apply_filter),
                onClick = onApplyClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun DateCard(month: String, day: String, year: String, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape =  MaterialTheme.shapes.small
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Month Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .height(30.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = month,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Day
            Text(
                text = day,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Year
            Text(
                text = year,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterExpenseDialogPreview() {
    ProExpenseTheme {
        FilterExpenseDialog()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun FilterExpenseDialogDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        FilterExpenseDialog()
    }
}

@Preview(locale = "my", showBackground = true)
@Composable
fun FilterExpenseDialogBurmesePreview() {
    ProExpenseTheme {
        FilterExpenseDialog()
    }
}
