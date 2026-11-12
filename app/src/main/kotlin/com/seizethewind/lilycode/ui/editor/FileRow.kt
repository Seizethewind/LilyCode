package com.seizethewind.lilycode.ui.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.seizethewind.lilycode.data.Entry
import com.seizethewind.lilycode.ui.theme.Surface1

/** One file or folder in the drawer, with a menu of actions. */
@Composable
fun FileRow(
    e: Entry,
    onOpen: () -> Unit,
    onRename: () -> Unit,
    onCopy: () -> Unit,
    onMove: () -> Unit,
    onDelete: () -> Unit,
) {
    var menu by remember { mutableStateOf(false) }
    ListItem(
        { Text(if (e.isDir) e.name + "/" else e.name) },
        Modifier.clickable(onClick = onOpen),
        trailingContent = {
            Box {
                IconButton({ menu = true }) { Icon(Icons.Default.MoreVert, "Actions for ${e.name}") }
                DropdownMenu(menu, { menu = false }) {
                    listOf("Rename" to onRename, "Copy" to onCopy, "Move" to onMove, "Delete" to onDelete).forEach { (label, act) ->
                        DropdownMenuItem({ Text(label) }, { menu = false; act() })
                    }
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = Surface1),
    )
}
