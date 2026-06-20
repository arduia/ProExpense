package com.proexpense.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.proexpense.designsystem.theme.Blue500
import com.proexpense.designsystem.theme.Blue700
import com.proexpense.designsystem.theme.DarkGray
import com.proexpense.designsystem.theme.Gray100
import com.proexpense.designsystem.theme.Gray300
import com.proexpense.designsystem.theme.Green500
import com.proexpense.designsystem.theme.Manrope
import com.proexpense.designsystem.theme.OnFilled

/* ─────────────────────────────────────────────────────────────────────────
 * ProButton — six variants × three sizes. See button.md.
 * Stateless action trigger; M3 ripple kept (M3 buttons handle press).
 * ───────────────────────────────────────────────────────────────────────── */

enum class ProButtonVariant { Primary, PrimaryDeep, Sage, Dark, Secondary, Ghost }
enum class ProButtonSize { Small, Medium, Large }

private fun ProButtonSize.radius() = when (this) {
    ProButtonSize.Small -> 10.dp
    ProButtonSize.Medium -> 12.dp
    ProButtonSize.Large -> 14.dp
}

private fun ProButtonSize.padding() = when (this) {
    ProButtonSize.Small -> PaddingValues(horizontal = 14.dp, vertical = 8.dp)
    ProButtonSize.Medium -> PaddingValues(horizontal = 18.dp, vertical = 12.dp)
    ProButtonSize.Large -> PaddingValues(horizontal = 22.dp, vertical = 16.dp)
}

private fun ProButtonSize.fontSize() = when (this) {
    ProButtonSize.Small -> 12.sp
    ProButtonSize.Medium -> 14.sp
    ProButtonSize.Large -> 15.sp
}

@Composable
fun ProButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ProButtonVariant = ProButtonVariant.Primary,
    size: ProButtonSize = ProButtonSize.Medium,
    enabled: Boolean = true,
    fullWidth: Boolean = false,
) {
    val shape = RoundedCornerShape(size.radius())
    val pad = size.padding()
    val mod = if (fullWidth) modifier.fillMaxWidth() else modifier
    val label = @Composable {
        Text(
            text = text,
            fontFamily = Manrope,
            fontWeight = FontWeight.SemiBold,
            fontSize = size.fontSize(),
            letterSpacing = (-0.005).em,
        )
    }

    when (variant) {
        ProButtonVariant.Secondary -> OutlinedButton(
            onClick = onClick, enabled = enabled, shape = shape, contentPadding = pad, modifier = mod,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGray, disabledContentColor = DarkGray.copy(alpha = 0.4f)),
            border = androidx.compose.foundation.BorderStroke(1.4.dp, Gray300),
        ) { label() }

        ProButtonVariant.Ghost -> TextButton(
            onClick = onClick, enabled = enabled, shape = shape, contentPadding = pad, modifier = mod,
            colors = ButtonDefaults.textButtonColors(contentColor = DarkGray, disabledContentColor = DarkGray.copy(alpha = 0.4f)),
        ) { label() }

        else -> {
            val container = when (variant) {
                ProButtonVariant.Primary -> Blue500
                ProButtonVariant.PrimaryDeep -> Blue700
                ProButtonVariant.Sage -> Green500
                ProButtonVariant.Dark -> DarkGray
                else -> Blue500
            }
            val content = if (variant == ProButtonVariant.Dark) Gray100 else OnFilled
            Button(
                onClick = onClick, enabled = enabled, shape = shape, contentPadding = pad, modifier = mod,
                colors = ButtonDefaults.buttonColors(
                    containerColor = container,
                    contentColor = content,
                    disabledContainerColor = container.copy(alpha = 0.4f),
                    disabledContentColor = content.copy(alpha = 0.8f),
                ),
            ) { label() }
        }
    }
}
