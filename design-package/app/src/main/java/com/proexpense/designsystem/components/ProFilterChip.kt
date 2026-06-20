package com.proexpense.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proexpense.designsystem.theme.DarkGray
import com.proexpense.designsystem.theme.Gray100
import com.proexpense.designsystem.theme.Gray300
import com.proexpense.designsystem.theme.Ink2
import com.proexpense.designsystem.theme.Manrope

/* FilterChip — monochrome single-select list filter; ink-filled when active.
 * Distinct from CategoryChip. See filter-chip.md. */
@Composable
fun ProFilterChip(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        color = if (active) Gray100 else Ink2,
        fontFamily = Manrope,
        fontSize = 12.sp,
        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
        maxLines = 1,
        modifier = modifier
            .clip(CircleShape)
            .background(if (active) DarkGray else Color.Transparent)
            .border(1.dp, if (active) DarkGray else Gray300, CircleShape)
            .pressScale(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}
