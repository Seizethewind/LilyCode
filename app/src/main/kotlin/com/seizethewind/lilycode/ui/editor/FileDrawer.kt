package com.seizethewind.lilycode.ui.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seizethewind.lilycode.data.Entry
import com.seizethewind.lilycode.ui.theme.Line
import com.seizethewind.lilycode.ui.theme.Muted
import com.seizethewind.lilycode.ui.theme.Surface1

/** The project's folders and files. [onPicked] runs after a file is opened. */
@Composable
fun FileDrawer(state: EditorState, onPicked: () -> Unit) {
    val stack = remember { mutableStateListOf(state.rootId) }
    var entries by remember { mutableStateOf(emptyList<Entry>()) }
    var tick by remember { mutableIntStateOf(0) }
    var naming by remember { mutableStateOf(false) }
    val dir = stack.last()
    LaunchedEffect(dir, tick) { entries = state.list(dir) }
    val row = ListItemDefaults.colors(containerColor = Surface1)
    ModalDrawerSheet(drawerContainerColor = Surface1) {
        Row(Modifier.fillMaxWidth().padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Files", Modifier.weight(1f), color = Muted, style = MaterialTheme.typography.labelLarge)
            IconButton({ naming = true }) { Icon(Icons.Default.Add, "New file") }
        }
        HorizontalDivider(color = Line)
        if (entries.isEmpty() && stack.size == 1) {
            Box(Modifier.fillMaxWidth().padding(24.dp), Alignment.Center) { Text("Empty", color = Muted) }
        } else LazyColumn {
            if (stack.size > 1) item {
                ListItem({ Text("..") }, Modifier.clickable { stack.removeAt(stack.lastIndex) }, colors = row)
            }
            items(entries, key = { it.docId }) { e ->
                ListItem(
                    { Text(if (e.isDir) e.name + "/" else e.name) },
                    Modifier.clickable { if (e.isDir) stack.add(e.docId) else { state.open(e.uri, e.name); onPicked() } },
                    colors = row,
                )
            }
        }
    }
    if (naming) NewFileDialog(
        onCreate = { name -> naming = false; state.create(dir, name) { tick++; onPicked() } },
        onDismiss = { naming = false },
    )
}
