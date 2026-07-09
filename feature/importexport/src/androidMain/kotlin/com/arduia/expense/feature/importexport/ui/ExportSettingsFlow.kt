package com.arduia.expense.feature.importexport.ui

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.FileProvider
import com.arduia.expense.data.ExportFormat
import com.arduia.expense.data.Result
import com.arduia.expense.feature.importexport.ExportDataUseCase
import com.arduia.expense.feature.importexport.ExportFileWriter
import com.arduia.expense.feature.importexport.ExportGroupedDataUseCase
import com.arduia.expense.feature.importexport.R
import com.arduia.expense.feature.importexport.ui.preview.previewMoreExportFiles
import com.arduia.expense.ui.design.ProToastHost
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExportSettingsFlow(
    onBack: () -> Unit,
    onExport: () -> Unit = {},
    modifier: Modifier = Modifier,
    exportGroupedData: ExportGroupedDataUseCase = koinInject(),
    exportData: ExportDataUseCase = koinInject(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var password by remember { mutableStateOf("") }
    var formatIndex by remember { mutableIntStateOf(0) }
    val exportFailedMessage = stringResource(R.string.more_export_failed)

    Box(modifier = modifier.fillMaxSize()) {
        MoreExportScreen(
            files = previewMoreExportFiles,
            password = password,
            onPasswordChange = { password = it },
            formatIndex = formatIndex,
            onFormatChange = { formatIndex = it },
            onExport = {
                onExport()
                scope.launch {
                    val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
                    val filesResult: Result<Map<String, String>> =
                        if (formatIndex == 0) {
                            exportGroupedData()
                        } else {
                            when (val result = exportData(ExportFormat.JSON)) {
                                is Result.Success -> Result.Success(mapOf("expenses.json" to result.data))
                                is Result.Error -> result
                            }
                        }
                    when (filesResult) {
                        is Result.Success -> {
                            val zip =
                                ExportFileWriter.writeZip(
                                    context = context,
                                    files = filesResult.data,
                                    zipFileName = "pro-expense-export-$timestamp.zip",
                                    password = password,
                                )
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", zip)
                            val shareIntent =
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "application/zip"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                            context.startActivity(Intent.createChooser(shareIntent, null))
                        }
                        is Result.Error -> errorMessage = exportFailedMessage
                    }
                }
            },
            onBack = onBack,
        )

        ProToastHost(
            message = errorMessage,
            onDismiss = { errorMessage = null },
        )
    }
}

@Preview(
    name = "Export settings flow",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ExportSettingsFlowPreview() {
    ProExpenseTheme {
        ExportSettingsFlow(onBack = {}, onExport = {})
    }
}
