package com.arduia.design.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arduia.design.theme.ProExpenseTheme

@Composable
fun ProExpenseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    icon: Painter? = null,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(4.dp), // Legacy corner radius
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    height: Dp = 56.dp, // Legacy height_button
    textStyle: TextStyle = MaterialTheme.typography.titleMedium
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(height),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        if (icon != null) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = textStyle
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun ProExpenseButtonPreview() {
    ProExpenseTheme(darkTheme = false) {
        ProExpenseButton(
            onClick = {},
            text = "Button",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ProExpenseButtonDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        ProExpenseButton(
            onClick = {},
            text = "Button",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "Burmese", locale = "my", showBackground = true)
@Composable
fun ProExpenseButtonBurmesePreview() {
    ProExpenseTheme {
        ProExpenseButton(
            onClick = {},
            text = "အိုကေ",
            modifier = Modifier.fillMaxWidth()
        )
    }
}
