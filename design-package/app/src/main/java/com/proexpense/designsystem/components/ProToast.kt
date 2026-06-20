package com.proexpense.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proexpense.designsystem.icons.ProIcons
import com.proexpense.designsystem.theme.DarkGray
import com.proexpense.designsystem.theme.Gray100
import com.proexpense.designsystem.theme.Green300
import com.proexpense.designsystem.theme.Manrope

/* ProToast — dark rounded confirmation pill. Pair with a SnackbarHost (or a
 * timed visibility) so it auto-dismisses ~2500ms. See toast.md. */
@Composable
fun ProToast(
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = ProIcons.Check,
    iconTint: Color = Green300,
) {
    Row(
        modifier = modifier
            .softShadow(elevation = 8.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(DarkGray)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.padding(end = 0.dp))
        Text(message, color = Gray100, fontFamily = Manrope, fontSize = 13.sp, maxLines = 1)
    }
}
