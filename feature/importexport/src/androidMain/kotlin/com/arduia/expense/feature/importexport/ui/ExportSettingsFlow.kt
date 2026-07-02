package com.arduia.expense.feature.importexport.ui

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.FileProvider
import com.arduia.expense.data.Result
import com.arduia.expense.feature.importexport.ExportFileWriter
import com.arduia.expense.feature.importexport.ExportGroupedDataUseCase
import com.arduia.expense.feature.importexport.R
import com.arduia.expense.feature.importexport.ui.preview.previewMoreExportFiles
import com.arduia.expense.ui.design.ProToastHost
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun ExportSettingsFlow(
    onBack: () -> Unit,
    onExport: () -> Unit = {},
    modifier: Modifier = Modifier,
    exportGroupedData: ExportGroupedDataUseCase = koinInject(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var password by remember { mutableStateOf("") }
    val exportFailedMessage = stringResource(R.string.more_export_failed)

    Box(modifier = modifier.fillMaxSize()) {
        MoreExportScreen(
            files = previewMoreExportFiles,
            password = password,
            onPasswordChange = { password = it },
            onExport = {
                onExport()
                scope.launch {
                    when (val result = exportGroupedData()) {
                        is Result.Success -> {
                            val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
                            val zip = ExportFileWriter.writeZip(
                                context = context,
                                files = result.data,
                                zipFileName = "pro-expense-export-$timestamp.zip",
                                password = password,
                            )
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", zip)
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
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
