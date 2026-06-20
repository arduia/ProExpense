package com.proexpense.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.proexpense.designsystem.theme.DarkGray
import com.proexpense.designsystem.theme.ProType
import com.proexpense.designsystem.theme.Scrim
import com.proexpense.designsystem.theme.White

/* ProBottomSheet — ModalBottomSheet with the 36×4 handle, 22dp top corners,
 * white container and the warm scrim. See bottom-sheet.md. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    content: @Composable () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        containerColor = White,
        scrimColor = Scrim,
        dragHandle = {
            Box(Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 4.dp), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(DarkGray.copy(alpha = 0.18f))
                        .padding(horizontal = 18.dp, vertical = 2.dp)
                ) {}
            }
        },
        modifier = modifier,
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 22.dp).padding(bottom = 38.dp)) {
            Text(title, style = ProType.SheetTitle, color = DarkGray, modifier = Modifier.padding(bottom = 14.dp))
            content()
        }
    }
}
