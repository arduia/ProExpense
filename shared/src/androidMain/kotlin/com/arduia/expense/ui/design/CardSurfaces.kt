package com.arduia.expense.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun Modifier.proCardShadow(shape: Shape = ProExpenseTheme.shapes.card): Modifier {
    val layers = ProExpenseTheme.elevation.card
    return layers.fold(this) { modifier, layer ->
        if (layer.blur > 0.dp) {
            modifier.shadow(
                elevation = layer.blur,
                shape = shape,
                spotColor = layer.color,
                ambientColor = layer.color,
            )
        } else {
            modifier
        }
    }
}

@Composable
fun Modifier.proToastShadow(shape: Shape = ProExpenseTheme.shapes.toast): Modifier {
    val layer = ProExpenseTheme.elevation.toast.firstOrNull() ?: return this
    return shadow(
        elevation = layer.blur,
        shape = shape,
        spotColor = layer.color,
        ambientColor = layer.color,
    )
}

@Composable
fun ProCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    padding: Dp = ProExpenseTheme.dimensions.cardPadding,
    content: @Composable () -> Unit,
) {
    val colors = ProExpenseTheme.colors
    val shape = ProExpenseTheme.shapes.card
    val interactionSource = remember { MutableInteractionSource() }

    var cardModifier = modifier
        .proCardShadow(shape)
        .clip(shape)
        .border(BorderStroke(1.dp, colors.line), shape)
        .background(colors.surface)

    cardModifier = if (onClick != null) {
        cardModifier.proClickable(
            onClick = onClick,
            shape = shape,
            interactionSource = interactionSource,
        )
    } else {
        cardModifier
    }

    Box(modifier = cardModifier.padding(padding)) {
        content()
    }
}

@Composable
fun SpentTodayCard(
    eyebrow: String,
    amount: String,
    modifier: Modifier = Modifier,
    showSparkline: Boolean = true,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    ProCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column {
                Text(
                    text = eyebrow.uppercase(),
                    style = typography.eyebrow.copy(
                        fontSize = 10.5.sp,
                        letterSpacing = 0.08.em,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = colors.onSurfaceMuted,
                )
                Text(
                    text = amount,
                    style = typography.summaryAmount.copy(fontFamily = typography.serifFamily),
                    color = colors.onSurface,
                    modifier = Modifier.padding(top = dimens.space4),
                )
            }
            if (showSparkline) {
                SparklinePlaceholder(
                    modifier = Modifier
                        .size(width = 86.dp, height = 40.dp)
                        .padding(top = dimens.space4),
                )
            }
        }
    }
}

@Composable
private fun SparklinePlaceholder(modifier: Modifier = Modifier) {
    val colors = ProExpenseTheme.colors
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(2f, size.height * 0.75f)
            lineTo(size.width * 0.16f, size.height * 0.55f)
            lineTo(size.width * 0.30f, size.height * 0.65f)
            lineTo(size.width * 0.44f, size.height * 0.35f)
            lineTo(size.width * 0.58f, size.height * 0.45f)
            lineTo(size.width * 0.72f, size.height * 0.25f)
            lineTo(size.width * 0.86f, size.height * 0.35f)
            lineTo(size.width * 0.98f, size.height * 0.15f)
        }
        drawPath(
            path = path,
            color = colors.primary,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 1.6.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round,
            ),
        )
        drawCircle(
            color = colors.primary,
            radius = 3.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(size.width * 0.98f, size.height * 0.15f),
        )
    }
}

@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun SpentTodayCardPreview() {
    ProExpenseTheme {
        SpentTodayCard(
            eyebrow = "Spent today",
            amount = "$42.00",
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP)
@Composable
private fun ProCardPreview() {
    ProExpenseTheme {
        ProCard(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Card surface",
                style = ProExpenseTheme.typography.sectionHead,
                color = ProExpenseTheme.colors.onSurface,
            )
        }
    }
}
