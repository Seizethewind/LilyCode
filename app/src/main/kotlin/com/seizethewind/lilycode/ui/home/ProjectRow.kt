package com.seizethewind.lilycode.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.seizethewind.lilycode.data.Project
import com.seizethewind.lilycode.ui.theme.Surface0

@Composable
fun ProjectRow(p: Project, onOpen: () -> Unit, onRemove: () -> Unit) = ListItem(
    headlineContent = { Text(p.name) },
    trailingContent = { IconButton(onRemove) { Icon(Icons.Default.Close, "Remove from list") } },
    colors = ListItemDefaults.colors(containerColor = Surface0),
    modifier = Modifier.clickable(onClick = onOpen),
)
