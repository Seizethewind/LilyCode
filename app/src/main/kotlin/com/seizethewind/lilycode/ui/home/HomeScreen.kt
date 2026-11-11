package com.seizethewind.lilycode.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seizethewind.lilycode.ui.theme.Accent
import com.seizethewind.lilycode.ui.theme.Bar
import com.seizethewind.lilycode.ui.theme.Muted
import com.seizethewind.lilycode.ui.theme.Surface0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onOpen: () -> Unit) = Scaffold(
    containerColor = Surface0,
    topBar = {
        TopAppBar(
            title = { Text("Lily Code") },
            actions = {
                IconButton({}) { Icon(Icons.Default.Search, "Search") }
                IconButton({}) { Icon(Icons.Default.MoreVert, "More") }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Bar),
        )
    },
    floatingActionButton = {
        ExtendedFloatingActionButton(onClick = onOpen, containerColor = Accent) {
            Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("New project")
        }
    },
) { pad ->
    Column(Modifier.padding(pad).fillMaxSize().padding(16.dp)) {
        Text("Projects", color = Muted, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxSize(), Alignment.Center) { Text("No projects yet", color = Muted) }
    }
}
