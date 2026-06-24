package com.arduia.expense.feature.importexport.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.data.ClearDataRepository
import com.arduia.expense.feature.importexport.R
import com.arduia.expense.feature.importexport.ui.preview.previewMoreClearOptions
import com.arduia.expense.ui.design.ProAlertDialog
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun ClearDataFlow(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    clearDataRepository: ClearDataRepository = koinInject(),
) {
    val colors = ProExpenseTheme.colors
    var checkedIds by remember { mutableStateOf(setOf("expenses")) }
    var showConfirm by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper),
    ) {
        MoreClearScreen(
            options = previewMoreClearOptions,
            checkedIds = checkedIds,
            onToggle = { id ->
                checkedIds = if (id in checkedIds) checkedIds - id else checkedIds + id
            },
            onClear = { showConfirm = true },
            onBack = onBack,
        )

        ProAlertDialog(
            visible = showConfirm,
            icon = ProIconGlyph.Close,
            iconTint = colors.danger,
            iconBackground = colors.dangerTint,
            title = stringResource(R.string.more_clear_confirm_title),
            body = buildAnnotatedString { append(stringResource(R.string.more_clear_confirm_body)) },
            confirmLabel = stringResource(R.string.more_clear_confirm_action),
            onConfirm = {
                scope.launch {
                    if (checkedIds.contains("everything")) {
                        clearDataRepository.clearAll()
                    } else {
                        if (checkedIds.contains("expenses")) clearDataRepository.clearExpenses()
                        if (checkedIds.contains("events")) clearDataRepository.clearEvents()
                        if (checkedIds.contains("debts")) clearDataRepository.clearDebts()
                        if (checkedIds.contains("shared_costs")) clearDataRepository.clearSharedCosts()
                    }
                    showConfirm = false
                    checkedIds = emptySet()
                    onBack()
                }
            },
            dismissLabel = stringResource(R.string.more_clear_confirm_cancel),
            onDismiss = { showConfirm = false },
            confirmVariant = ProButtonVariant.Danger,
        )
    }
}

@Preview(
    name = "Clear data flow",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ClearDataFlowPreview() {
    ProExpenseTheme {
        ClearDataFlow(onBack = {})
    }
}
