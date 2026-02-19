package com.arduia.expense.ui.entry

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.design.components.ProExpenseButton
import com.arduia.design.components.ProExpenseCard
import com.arduia.design.components.ProExpenseTextField
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    categories: List<String> = emptyList(),
    selectedCategory: String = ""
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isRepeat by remember { mutableStateOf(false) }
    var currentSelectedCategory by remember { mutableStateOf(selectedCategory) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.expense_entry)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                // Repeat Entry Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Switch(
                        checked = isRepeat,
                        onCheckedChange = { isRepeat = it }
                    )
                    Text(
                        text = "Repeat Entry", // Hardcoded in XML too
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                ProExpenseButton(
                    text = stringResource(id = R.string.save),
                    onClick = onSaveClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 450.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                ProExpenseTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = stringResource(id = R.string.name),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )

                ProExpenseTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = stringResource(id = R.string.amount),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    )
                )

                if (categories.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            CategoryItem(
                                name = category,
                                isSelected = category == currentSelectedCategory,
                                onClick = { currentSelectedCategory = category }
                            )
                        }
                    }
                }

                ProExpenseTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = stringResource(id = R.string.note),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    singleLine = false,
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
                )
            }
        }
    }
}

@Composable
fun CategoryItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Colors from XML assumption or Design System
    // Selected: Primary color background, OnPrimary text
    // Unselected: Surface/Gray background, OnSurface text
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    ProExpenseCard(
        modifier = Modifier
            .clickable(onClick = onClick),
        containerColor = backgroundColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small // radius
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseEntryScreenPreview() {
    val mockCategories = listOf("Food", "Transport", "Shopping", "Entertainment")
    ProExpenseTheme {
        ExpenseEntryScreen(categories = mockCategories, selectedCategory = "Food")
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ExpenseEntryScreenDarkPreview() {
     val mockCategories = listOf("Food", "Transport")
    ProExpenseTheme(darkTheme = true) {
        ExpenseEntryScreen(categories = mockCategories, selectedCategory = "Food")
    }
}

@Preview(locale = "my", showBackground = true)
@Composable
fun ExpenseEntryScreenBurmesePreview() {
    val mockCategories = listOf("အစားအသောက်", "သွားလာရေး", "စျေးဝယ်", "အပန်းဖြေ")
    ProExpenseTheme {
        ExpenseEntryScreen(categories = mockCategories, selectedCategory = "အစားအသောက်")
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryItemPreview() {
    ProExpenseTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CategoryItem(name = "Food", isSelected = true, onClick = {})
            CategoryItem(name = "Transport", isSelected = false, onClick = {})
        }
    }
}
