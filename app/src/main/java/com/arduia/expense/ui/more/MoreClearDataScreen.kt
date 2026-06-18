package com.arduia.expense.ui.more

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arduia.expense.ui.design.ProExpenseButton
import com.arduia.expense.ui.design.ProExpenseButtonSize
import com.arduia.expense.ui.design.ProExpenseButtonVariant
import com.arduia.expense.ui.design.ProExpensePaperBackground
import com.arduia.expense.ui.profile.CheckGlyph
import com.arduia.expense.ui.theme.ProExpenseTheme

/** A scope of local data the user can wipe. [destructive] reserves the red label for "Everything". */
enum class ClearScope(
    val label: String,
    val subtitle: String,
    val destructive: Boolean = false,
) {
    Expenses("Expenses", "All logged entries"),
    Events("Events", "Budgets & linked tags"),
    Debts("Debts", "Lent / owed records"),
    SharedCosts("Shared costs", "Saved splits"),
    Everything("Everything", "Reset the app fully", destructive = true),
}

/**
 * Selectively wipe local data. Multi-select checkboxes; the destructive action asks for
 * confirmation and can't be undone. See design/screens/06-more/06-more-clear.
 */
@Composable
fun MoreClearDataScreen(
    selected: Set<ClearScope>,
    onToggle: (ClearScope) -> Unit,
    onConfirmClear: (Set<ClearScope>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    var showConfirm by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        MoreNavBar(title = "Clear Data", onBack = onBack)

        Text(
            text = "Choose what to wipe. Each clear asks for confirmation and can't be undone.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MoreHorizontalPadding),
            style = ProExpenseTheme.typography.body,
            color = colors.muted,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = MoreHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ClearScope.entries.forEach { scope ->
                ClearOptionRow(
                    scope = scope,
                    checked = scope in selected,
                    onToggle = { onToggle(scope) },
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        ProExpenseButton(
            text = "Clear selected…",
            onClick = { if (selected.isNotEmpty()) showConfirm = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MoreHorizontalPadding)
                .padding(top = 12.dp, bottom = 38.dp),
            variant = ProExpenseButtonVariant.Danger,
            size = ProExpenseButtonSize.Large,
            enabled = selected.isNotEmpty(),
        )
    }

    if (showConfirm) {
        ClearConfirmDialog(
            count = selected.size,
            onConfirm = {
                showConfirm = false
                onConfirmClear(selected)
            },
            onDismiss = { showConfirm = false },
        )
    }
}

@Composable
private fun ClearOptionRow(
    scope: ClearScope,
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val labelColor = if (scope.destructive) colors.danger else colors.ink

    Surface(
        onClick = onToggle,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = scope.label
                toggleableState = if (checked) ToggleableState.On else ToggleableState.Off
            },
        shape = ProExpenseTheme.shapes.card,
        color = colors.card,
        contentColor = colors.ink,
        border = BorderStroke(1.dp, colors.line),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scope.label,
                    style = ProExpenseTheme.typography.bodyEmphasis.copy(fontSize = 14.5.sp),
                    color = labelColor,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = scope.subtitle,
                    style = ProExpenseTheme.typography.caption,
                    color = colors.muted,
                )
            }
            CheckBox(checked = checked)
        }
    }
}

@Composable
private fun CheckBox(
    checked: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors

    Surface(
        modifier = modifier.size(24.dp),
        shape = RoundedCornerShape(6.dp),
        color = if (checked) colors.primary else colors.card,
        border = if (checked) null else BorderStroke(1.4.dp, colors.lineStrong),
    ) {
        if (checked) {
            Box(contentAlignment = Alignment.Center) {
                CheckGlyph(color = colors.paperWarm, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun ClearConfirmDialog(
    count: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = ProExpenseTheme.colors

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        titleContentColor = colors.ink,
        textContentColor = colors.ink2,
        title = {
            Text(
                text = "Clear selected data?",
                style = ProExpenseTheme.typography.sectionHead,
                color = colors.ink,
            )
        },
        text = {
            Text(
                text = "This wipes $count selected ${if (count == 1) "scope" else "scopes"} from " +
                    "this device. It can't be undone.",
                style = ProExpenseTheme.typography.body,
                color = colors.ink2,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Clear",
                    style = ProExpenseTheme.typography.bodyEmphasis.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.danger,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    style = ProExpenseTheme.typography.body,
                    color = colors.muted,
                )
            }
        },
    )
}

@Preview(showBackground = true, widthDp = 414, heightDp = 868)
@Composable
private fun MoreClearDataScreenPreview() {
    var selected by remember { mutableStateOf(setOf(ClearScope.Expenses)) }
    ProExpenseTheme {
        ProExpensePaperBackground {
            MoreClearDataScreen(
                selected = selected,
                onToggle = { scope ->
                    selected = if (scope in selected) selected - scope else selected + scope
                },
                onConfirmClear = {},
                onBack = {},
            )
        }
    }
}
