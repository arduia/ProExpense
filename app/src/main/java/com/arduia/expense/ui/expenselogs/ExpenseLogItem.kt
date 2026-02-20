package com.arduia.expense.ui.expenselogs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arduia.expense.R

@Composable
fun ExpenseLogHeaderItem(
    date: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 20.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ExpenseLogItem(
    logItem: ExpenseLogUiModel.Log,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSelectionToggle: () -> Unit
) {
    val density = LocalDensity.current
    val swipeThresholdX = with(density) { 60.dp.toPx() }

    var offsetX by remember { mutableFloatStateOf(0f) }

    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isSelected) swipeThresholdX else offsetX,
        label = "offsetAnimation"
    )

    val expense = logItem.expenseLog

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(Color(0xFFEF5350)) // red_400
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    val newOffset = offsetX + delta
                    offsetX = newOffset.coerceIn(0f, swipeThresholdX * 1.5f)
                },
                onDragStopped = {
                    if (offsetX > swipeThresholdX / 2 && !isSelected) {
                        onSelectionToggle()
                    } else if (offsetX < swipeThresholdX / 2 && isSelected) {
                        onSelectionToggle()
                    }
                    offsetX = 0f
                }
            )
    ) {
        // Background layer (visible when swiped)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: check icon (selection indicator)
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .alpha(if (animatedOffsetX > 10f) 1f else 0f)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Right side: delete action
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.clickable { onDeleteClick() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Delete",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Foreground surface (the actual expense row)
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = animatedOffsetX
                }
                .clickable { onClick() },
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Icon
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFB3E5FC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = expense.category),
                        contentDescription = null,
                        tint = Color(0xFF616161), // dark_gray
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name and Date
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = expense.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.alpha(0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = expense.date,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alpha(0.9f)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Amount and Currency
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = expense.amount,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = expense.currencySymbol,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .alpha(0.9f)
                            .padding(start = 4.dp, top = 4.dp)
                    )
                }
            }
        }
    }
}
