package com.arduia.expense.ui.entry

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.components.ProExpenseButton
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R
import com.arduia.expense.ui.common.category.ExpenseCategory
import com.arduia.expense.ui.entry.molecule.ExpenseEntryEvent
import com.arduia.expense.ui.entry.molecule.ExpenseEntryState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    state: ExpenseEntryState,
    onEvent: (ExpenseEntryEvent) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val dateFormat = SimpleDateFormat("d MMM yyyy h:mm a", Locale.ENGLISH)
    val isUpdateMode = state.mode == ExpenseEntryMode.UPDATE

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isUpdateMode) stringResource(R.string.update_data)
                            else stringResource(R.string.expense_entry)
                        )
                        Text(
                            text = dateFormat.format(Date(state.entryDateTimeMillis)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEvent(ExpenseEntryEvent.DateSelectClicked) }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_calendar),
                            contentDescription = "Select Date"
                        )
                    }
                    IconButton(onClick = { onEvent(ExpenseEntryEvent.TimeSelectClicked) }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_time),
                            contentDescription = "Select Time"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Scrollable form content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Name field
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { onEvent(ExpenseEntryEvent.NameChanged(it)) },
                    label = { Text(stringResource(R.string.name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Amount field
                OutlinedTextField(
                    value = state.amount,
                    onValueChange = { onEvent(ExpenseEntryEvent.AmountChanged(it)) },
                    label = { Text(stringResource(R.string.amount)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.amountError != null,
                    supportingText = state.amountError?.let {
                        { Text(it, color = MaterialTheme.colorScheme.error) }
                    },
                    suffix = if (state.currencySymbol.isNotEmpty()) {
                        { Text(state.currencySymbol) }
                    } else null
                )

                Spacer(modifier = Modifier.height(16.dp))

                val listState = rememberLazyListState()

                // Category selector
                LazyRow(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.categories, key = { it.id }) { category ->
                        CategoryChip(
                            category = category,
                            isSelected = category.id == state.selectedCategory?.id,
                            onClick = { 
                                onEvent(ExpenseEntryEvent.CategorySelected(category))
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Note field
                OutlinedTextField(
                    value = state.note,
                    onValueChange = { onEvent(ExpenseEntryEvent.NoteChanged(it)) },
                    label = { Text(stringResource(R.string.note)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            }

            // Bottom action bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                // Repeat entry toggle (only in INSERT mode)
                if (!isUpdateMode) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onEvent(ExpenseEntryEvent.RepeatToggled(!state.isRepeatEnabled))
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = state.isRepeatEnabled,
                            onCheckedChange = { onEvent(ExpenseEntryEvent.RepeatToggled(it)) }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Repeat Entry",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Save / Update / Next button
                ProExpenseButton(
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 20.dp),
                    enabled = state.isSaveEnabled,
                    onClick = {
                        if (isUpdateMode) {
                            onEvent(ExpenseEntryEvent.UpdateClicked)
                        } else {
                            onEvent(ExpenseEntryEvent.SaveClicked)
                        }
                    }, text = when {
                        isUpdateMode -> stringResource(R.string.update)
                        state.isRepeatEnabled -> stringResource(R.string.next)
                        else -> stringResource(R.string.save)
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    category: ExpenseCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outlineVariant
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surface

    Column(
        modifier = Modifier
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = category.img),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            colorFilter = if (isSelected) ColorFilter.tint(MaterialTheme.colorScheme.primary) else null
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(category.nameId),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseEntryScreenPreview() {
    ProExpenseTheme {
        ExpenseEntryScreen(
            state = ExpenseEntryState(
                categories = listOf(
                    ExpenseCategory(ExpenseCategory.FOOD, R.string.food, R.drawable.ic_food),
                    ExpenseCategory(
                        ExpenseCategory.TRANSPORTATION,
                        R.string.transportation,
                        R.drawable.ic_transportation
                    )
                ),
                selectedCategory = ExpenseCategory(
                    ExpenseCategory.FOOD,
                    R.string.food,
                    R.drawable.ic_food
                )
            ),
            onEvent = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ExpenseEntryScreenDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        ExpenseEntryScreen(
            state = ExpenseEntryState(
                mode = ExpenseEntryMode.UPDATE,
                name = "Coffee",
                amount = "3.50",
                categories = listOf(
                    ExpenseCategory(ExpenseCategory.FOOD, R.string.food, R.drawable.ic_food)
                ),
                selectedCategory = ExpenseCategory(
                    ExpenseCategory.FOOD,
                    R.string.food,
                    R.drawable.ic_food
                )
            ),
            onEvent = {}
        )
    }
}
