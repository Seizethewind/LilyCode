package com.seizethewind.lilycode.ui.home

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
import com.seizethewind.lilycode.data.Project
import com.seizethewind.lilycode.ui.components.InputField
import com.seizethewind.lilycode.ui.theme.Accent
import com.seizethewind.lilycode.ui.theme.Bar
import com.seizethewind.lilycode.ui.theme.Muted
import com.seizethewind.lilycode.ui.theme.Surface0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    projects: List<Project>,
    onNew: () -> Unit,
    onOpen: (Project) -> Unit,
    onRemove: (Project) -> Unit,
) {
    var searching by remember { mutableStateOf(false) }
    var q by remember { mutableStateOf("") }
    val shown = projects.filter { it.name.contains(q, ignoreCase = true) }
    Scaffold(
        containerColor = Surface0,
        topBar = {
            TopAppBar(
                title = { Text("Lily Code") },
                actions = {
                    IconButton({ searching = !searching; q = "" }) { Icon(Icons.Default.Search, "Search") }
                    IconButton({}) { Icon(Icons.Default.MoreVert, "More") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bar),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onNew, containerColor = Accent) {
                Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("New project")
            }
        },
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().padding(16.dp)) {
            Text("Projects", color = Muted, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            if (searching) {
                InputField(q, { q = it }, "Search projects", Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
            }
            if (shown.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(if (projects.isEmpty()) "No projects yet" else "No match", color = Muted)
                }
            } else {
                LazyColumn { items(shown, key = { it.uri }) { ProjectRow(it, { onOpen(it) }, { onRemove(it) }) } }
            }
        }
    }
}
