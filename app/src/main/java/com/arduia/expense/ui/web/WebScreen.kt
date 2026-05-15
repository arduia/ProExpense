package com.arduia.expense.ui.web

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.arduia.expense.designsystem.component.topbar.ProExpenseTopBar
import com.arduia.expense.designsystem.component.topbar.TopBarNavIcon
import com.arduia.expense.designsystem.theme.ProExpenseTheme

@Composable
fun WebScreen(
    url: String,
    title: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ProExpenseTopBar(
                title = title,
                navIcon = TopBarNavIcon.Back,
                onNavIconClick = onNavigateBack,
            )
        },
    ) { innerPadding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = false
                    loadUrl(url)
                }
            },
            update = { webView -> webView.loadUrl(url) },
        )
    }
}

@Preview
@Composable
private fun PreviewWebScreen() {
    ProExpenseTheme {
        WebScreen(
            url = "https://example.com",
            title = "Privacy Policy",
            onNavigateBack = {},
        )
    }
}
