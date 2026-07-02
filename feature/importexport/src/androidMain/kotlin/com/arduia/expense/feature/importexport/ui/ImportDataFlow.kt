package com.arduia.expense.feature.importexport.ui

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.arduia.expense.data.ExportFormat
import com.arduia.expense.data.Result
import com.arduia.expense.feature.importexport.ImportDataUseCase
import com.arduia.expense.feature.importexport.ImportZipReader
import com.arduia.expense.feature.importexport.PreviewImportUseCase
import com.arduia.expense.feature.importexport.R
import com.arduia.expense.feature.importexport.ui.preview.MoreImportUiState
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun ImportDataFlow(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    previewImport: PreviewImportUseCase = koinInject(),
    importData: ImportDataUseCase = koinInject(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf(MoreImportUiState()) }
    var pendingUri by remember { mutableStateOf<Uri?>(null) }
    var pendingContent by remember { mutableStateOf<String?>(null) }
    var pendingFormat by remember { mutableStateOf(ExportFormat.CSV) }
    val readError = stringResource(R.string.more_import_read_error)
    val wrongPassword = stringResource(R.string.more_import_password_wrong)

    suspend fun readAndPreview(uri: Uri, fileName: String, password: String) {
        val isZip = fileName.endsWith(".zip", ignoreCase = true)
        val format = if (fileName.endsWith(".json", ignoreCase = true)) ExportFormat.JSON else ExportFormat.CSV
        val content: String? = if (isZip) {
            val zipRead = runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    ImportZipReader.readExpensesCsv(stream, password)
                }
            }.getOrNull()
            when (zipRead) {
                is ImportZipReader.ZipRead.Success -> zipRead.csvContent
                is ImportZipReader.ZipRead.NeedsPassword -> {
                    state = state.copy(
                        needsPassword = true,
                        previewCount = null,
                        errorMessage = if (password.isNotBlank()) wrongPassword else null,
                    )
                    return
                }
                else -> null
            }
        } else {
            runCatching {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            }.getOrNull()
        }
        if (content == null) {
            state = state.copy(errorMessage = readError, needsPassword = false)
            return
        }
        when (val result = previewImport(content, format)) {
            is Result.Success -> {
                if (result.data.isEmpty()) {
                    state = state.copy(errorMessage = readError, needsPassword = false)
                } else {
                    pendingContent = content
                    pendingFormat = format
                    state = state.copy(previewCount = result.data.size, needsPassword = false, errorMessage = null)
                }
            }
            is Result.Error -> state = state.copy(errorMessage = readError, needsPassword = false)
        }
    }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val fileName = queryDisplayName(context, uri) ?: uri.lastPathSegment.orEmpty()
        pendingUri = uri
        pendingContent = null
        state = MoreImportUiState(fileName = fileName)
        scope.launch { readAndPreview(uri, fileName, password = "") }
    }

    Box(modifier = modifier.fillMaxSize()) {
        MoreImportScreen(
            state = state,
            onChooseFile = {
                pickerLauncher.launch(
                    arrayOf("text/csv", "text/comma-separated-values", "application/json", "application/zip", "*/*"),
                )
            },
            onPasswordChange = { state = state.copy(password = it) },
            onUnlock = {
                val uri = pendingUri ?: return@MoreImportScreen
                val fileName = state.fileName ?: return@MoreImportScreen
                scope.launch { readAndPreview(uri, fileName, password = state.password) }
            },
            onImport = {
                val content = pendingContent ?: return@MoreImportScreen
                state = state.copy(isImporting = true)
                scope.launch {
                    when (val result = importData(content, pendingFormat)) {
                        is Result.Success -> {
                            state = state.copy(
                                isImporting = false,
                                resultMessage = context.getString(
                                    R.string.more_import_result,
                                    result.data.importedCount,
                                    result.data.skippedCount,
                                ),
                            )
                            pendingContent = null
                        }
                        is Result.Error -> state = state.copy(isImporting = false, errorMessage = readError)
                    }
                }
            },
            onBack = onBack,
        )
    }
}

private fun queryDisplayName(context: Context, uri: Uri): String? =
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex >= 0 && cursor.moveToFirst()) cursor.getString(nameIndex) else null
    }

@Preview(
    name = "Import data flow",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ImportDataFlowPreview() {
    ProExpenseTheme {
        ImportDataFlow(onBack = {})
    }
}
