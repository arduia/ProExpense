package com.arduia.expense.ui.design

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arduia.expense.shared.R
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlinx.coroutines.delay

@Composable
fun ProToast(
    message: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier = modifier
            .background(colors.onSurface, ProExpenseTheme.shapes.toast)
            .padding(horizontal = dimens.space18, vertical = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space8),
    ) {
        ProIcon(
            glyph = ProIconGlyph.Check,
            contentDescription = stringResource(R.string.toast_check_icon),
            tint = colors.success,
            size = dimens.iconInline,
        )
        Text(
            text = message,
            style = typography.bodySemiBold,
            color = colors.onPrimaryWarm,
        )
    }
}

@Composable
fun ProToastHost(
    message: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val motion = ProExpenseTheme.motion

    LaunchedEffect(message) {
        if (message != null) {
            delay(motion.toastDurationMillis.toLong())
            onDismiss()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = message != null,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(bottom = ProExpenseTheme.dimensions.space24),
            enter = fadeIn() + slideInVertically { fullHeight -> fullHeight / 2 },
            exit = fadeOut() + slideOutVertically { fullHeight -> fullHeight / 2 },
        ) {
            if (message != null) {
                ProToast(message = message)
            }
        }
    }
}
