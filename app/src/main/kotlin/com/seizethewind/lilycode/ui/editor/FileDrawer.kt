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
import com.seizethewind.lilycode.ui.components.ConfirmDialog
import com.seizethewind.lilycode.ui.theme.Line
import com.seizethewind.lilycode.ui.theme.Muted
import com.seizethewind.lilycode.ui.theme.Surface1

private sealed interface Ask {
    object NewFile : Ask
    object NewFolder : Ask
    class Rename(val e: Entry) : Ask
    class Delete(val e: Entry) : Ask
}

/** The project's folders and files, with new, rename, copy, move and delete. [onPicked] runs after a file is opened. */
@Composable
fun FileDrawer(state: EditorState, onPicked: () -> Unit) {
    val files = remember(state) { FileManager(state) }
    val stack = remember { mutableStateListOf(state.rootId) }
    var entries by remember { mutableStateOf(emptyList<Entry>()) }
    var tick by remember { mutableIntStateOf(0) }
    var ask by remember { mutableStateOf<Ask?>(null) }
    var clip by remember { mutableStateOf<Clip?>(null) }
    var plus by remember { mutableStateOf(false) }
    val dir = stack.last()
    val refresh: () -> Unit = { tick++ }
    LaunchedEffect(dir, tick) { entries = state.list(dir) }
    val row = ListItemDefaults.colors(containerColor = Surface1)
    ModalDrawerSheet(drawerContainerColor = Surface1) {
        Row(Modifier.fillMaxWidth().padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Files", Modifier.weight(1f), color = Muted, style = MaterialTheme.typography.labelLarge)
            Box {
                IconButton({ plus = true }) { Icon(Icons.Default.Add, "New") }
                DropdownMenu(plus, { plus = false }) {
                    DropdownMenuItem({ Text("New file") }, { plus = false; ask = Ask.NewFile })
                    DropdownMenuItem({ Text("New folder") }, { plus = false; ask = Ask.NewFolder })
                }
            }
        }
        clip?.let { c ->
            Row(Modifier.fillMaxWidth().padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text((if (c.move) "Move " else "Copy ") + c.entry.name, Modifier.weight(1f), color = Muted)
                TextButton({ files.paste(c, dir, refresh); clip = null }) { Text("Paste here") }
                IconButton({ clip = null }) { Icon(Icons.Default.Close, "Cancel") }
            }
        }
        HorizontalDivider(color = Line)
        if (entries.isEmpty() && stack.size == 1) {
            Box(Modifier.fillMaxWidth().padding(24.dp), Alignment.Center) { Text("Empty", color = Muted) }
        } else LazyColumn {
            if (stack.size > 1) item {
                ListItem({ Text("..") }, Modifier.clickable { stack.removeAt(stack.lastIndex) }, colors = row)
            }
            items(entries, key = { it.docId }) { e ->
                FileRow(
                    e,
                    onOpen = { if (e.isDir) stack.add(e.docId) else { state.open(e.uri, e.name); onPicked() } },
                    onRename = { ask = Ask.Rename(e) },
                    onCopy = { clip = Clip(e, false) },
                    onMove = { clip = Clip(e, true) },
                    onDelete = { ask = Ask.Delete(e) },
                )
            }
        }
    }
    val close = { ask = null }
    when (val a = ask) {
        null -> {}
        Ask.NewFile -> NameDialog("New file", "", { n -> close(); files.newFile(dir, n, refresh) }, close)
        Ask.NewFolder -> NameDialog("New folder", "", { n -> close(); files.newFolder(dir, n, refresh) }, close)
        is Ask.Rename -> NameDialog("Rename", a.e.name, { n -> close(); files.rename(a.e, n, refresh) }, close)
        is Ask.Delete -> ConfirmDialog(
            "Delete ${a.e.name}${if (a.e.isDir) " and everything in it" else ""}?", "Delete",
            { close(); files.delete(a.e, refresh) }, close,
        )
    }
}
