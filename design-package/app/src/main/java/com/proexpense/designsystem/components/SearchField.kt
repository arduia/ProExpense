package com.proexpense.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proexpense.designsystem.icons.ProIcons
import com.proexpense.designsystem.theme.DarkGray
import com.proexpense.designsystem.theme.Gray500
import com.proexpense.designsystem.theme.Line
import com.proexpense.designsystem.theme.Manrope
import com.proexpense.designsystem.theme.White

/* SearchField — lightweight clearable search. Display shell (host owns the
 * BasicTextField). See search-field.md. */
@Composable
fun SearchField(
    query: String,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search notes, amount, category…",
    onClick: () -> Unit = {},
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(White)
            .border(1.dp, Line, shape)
            .pressScale(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(ProIcons.Search, contentDescription = null, tint = Gray500, modifier = Modifier.padding(end = 0.dp))
        Text(
            text = query.ifEmpty { placeholder },
            color = if (query.isEmpty()) Gray500 else DarkGray,
            fontFamily = Manrope,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f),
        )
        if (query.isNotEmpty()) {
            Icon(
                ProIcons.Close, contentDescription = "Clear", tint = Gray500,
                modifier = Modifier.pressScale(onClick = onClear),
            )
        }
    }
}
