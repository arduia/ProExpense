package com.arduia.expense.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.arduia.design.theme.Yellow500
import com.arduia.expense.R
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    drawerState: DrawerState,
    currentDestinationId: Int?,
    onItemClick: (Int) -> Unit,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen || currentDestinationId in TOP_DESTINATIONS,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                drawerShape = RoundedCornerShape(0.dp),
                modifier = Modifier
                    .width(280.dp)
                    .fillMaxHeight()
            ) {
                DrawerHeader()

                DrawerContent(
                    currentDestinationId = currentDestinationId,
                    onItemClick = { destinationId ->
                        scope.launch {
                            drawerState.close()
                            onItemClick.invoke(destinationId)
                        }
                    }
                )
            }
        },
        content = content
    )
}

@Composable
private fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.Transparent)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(id = R.string.beta).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray,
                    modifier = Modifier
                        .background(
                            color = Yellow500,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun DrawerContent(
    currentDestinationId: Int?,
    onItemClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 8.dp)
    ) {
        val items = listOf(
            DrawerItem(R.id.dest_home, R.drawable.ic_home, R.string.home),
            DrawerItem(R.id.dest_expense_logs, R.drawable.ic_expense_logs, R.string.expense_logs),
            DrawerItem(R.id.dest_statistics, R.drawable.ic_statistics, R.string.statistics),
            DrawerItem(R.id.dest_backup, R.drawable.ic_export, R.string.backup),
            DrawerItem(R.id.dest_feedback, R.drawable.ic_feedback, R.string.feedback),
            DrawerItem(R.id.dest_about, R.drawable.ic_info, R.string.about),
            DrawerItem(R.id.dest_settings, R.drawable.ic_settings, R.string.settings)
        )

        items.forEach { item ->
            DrawerItemRow(
                item = item,
                isSelected = currentDestinationId == item.destinationId,
                onClick = { onItemClick(item.destinationId) }
            )
        }
    }
}

@Composable
private fun DrawerItemRow(
    item: DrawerItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFE1F5FE) else Color.Transparent // Light blue
    val contentColor =
        if (isSelected) Color(0xFF039BE5) else MaterialTheme.colorScheme.onSurface // Darker blue

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = item.iconResId),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(20.dp))

        Text(
            text = stringResource(id = item.titleResId),
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor
        )
    }
}

private data class DrawerItem(
    val destinationId: Int,
    val iconResId: Int,
    val titleResId: Int
)

val TOP_DESTINATIONS = listOf(
    R.id.dest_home,
    R.id.dest_backup,
    R.id.dest_statistics,
    R.id.dest_feedback,
    R.id.dest_about,
    R.id.dest_settings,
    R.id.dest_expense_logs
) 
