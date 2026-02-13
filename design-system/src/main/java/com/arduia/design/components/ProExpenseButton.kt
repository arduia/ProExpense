package com.arduia.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.design.theme.statistic_progress_blue
import com.arduia.design.theme.category_icon_background
import com.arduia.design.theme.category_icon_tint

@Composable
fun ProExpenseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.PRIMARY
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = when (variant) {
            ButtonVariant.PRIMARY -> ButtonDefaults.buttonColors(
                containerColor = statistic_progress_blue,
                contentColor = Color.White
            )
            ButtonVariant.SECONDARY -> ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEEEEEE),
                contentColor = category_icon_tint
            )
            ButtonVariant.TINTED -> ButtonDefaults.buttonColors(
                containerColor = category_icon_background,
                contentColor = category_icon_tint
            )
            ButtonVariant.OUTLINE -> ButtonDefaults.outlinedButtonColors(
                contentColor = statistic_progress_blue
            )
        },
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp, pressedElevation = 1.dp)
    ) {
        Text(
            text = text.uppercase(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

enum class ButtonVariant {
    PRIMARY,
    SECONDARY,
    TINTED,
    OUTLINE
}

@Preview(showBackground = true)
@Composable
fun PreviewProExpenseButton() {
    ProExpenseTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ProExpenseButton(text = "Save", onClick = {})
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                ProExpenseButton(
                    text = "Food", 
                    onClick = {}, 
                    variant = ButtonVariant.TINTED,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ProExpenseButton(
                    text = "Income", 
                    onClick = {}, 
                    variant = ButtonVariant.SECONDARY,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
