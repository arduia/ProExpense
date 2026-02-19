package com.arduia.expense.ui.expenselogs

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.components.ProExpenseButton
import com.arduia.design.components.ProExpenseCard
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R

@Composable
fun ExpenseDetailDialogScreen(
    categoryName: String = "Sugar",
    amount: String = "35,00",
    currencySymbol: String = "Ks",
    date: String = "25/2/2020",
    note: String = "Somethings..",
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onOkayClick: () -> Unit = {}
) {
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
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.expnese_detail),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onEditClick) {
                     Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Edit",
                         tint = MaterialTheme.colorScheme.onSurface,
                         modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detail Card
            ProExpenseCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface,
                elevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Category & Name
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ProExpenseCard(
                            modifier = Modifier.size(55.dp),
                            shape = MaterialTheme.shapes.medium, // Circular in XML but standard reuse here
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_household),
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Amount
                    DetailRow(
                        label = stringResource(id = R.string.amount),
                        value = amount,
                        suffix = currencySymbol
                    )

                    // Date
                    DetailRow(
                        label = stringResource(id = R.string.date),
                        value = date
                    )

                    // Note
                    DetailRow(
                        label = stringResource(id = R.string.note),
                        value = note
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProExpenseButton(
                text = stringResource(id = R.string.okay),
                onClick = onOkayClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, suffix: String? = null) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
             Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.width(80.dp) // Align labels roughly
            )
             Row(verticalAlignment = Alignment.Bottom) {
                 Text(
                     text = value,
                     style = MaterialTheme.typography.bodyLarge,
                     fontWeight = FontWeight.Bold
                 )
                 if (suffix != null) {
                     Spacer(modifier = Modifier.width(4.dp))
                     Text(
                         text = suffix,
                         style = MaterialTheme.typography.bodySmall
                     )
                 }
             }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseDetailDialogScreenPreview() {
    ProExpenseTheme {
        ExpenseDetailDialogScreen()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ExpenseDetailDialogScreenDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        ExpenseDetailDialogScreen()
    }
}
