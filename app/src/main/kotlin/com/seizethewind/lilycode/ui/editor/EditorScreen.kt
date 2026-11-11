package com.seizethewind.lilycode.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seizethewind.lilycode.ui.theme.Accent
import com.seizethewind.lilycode.ui.theme.Bar
import com.seizethewind.lilycode.ui.theme.Line
import com.seizethewind.lilycode.ui.theme.Muted
import com.seizethewind.lilycode.ui.theme.Surface0
import com.seizethewind.lilycode.ui.theme.Surface1
import kotlinx.coroutines.launch

private val keys = listOf("Tab", "{", "}", "(", ")", "[", "]", ";", "\"", "'", "<", ">", "/", "=", "+", "-", "*")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(onHome: () -> Unit) {
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }
    ModalNavigationDrawer(
        drawerState = drawer,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Surface1) {
                Text("Files", Modifier.padding(16.dp), color = Muted, style = MaterialTheme.typography.labelLarge)
                HorizontalDivider(color = Line)
                Box(Modifier.fillMaxWidth().padding(24.dp), Alignment.Center) { Text("Empty", color = Muted) }
            }
        },
    ) {
        Scaffold(
            containerColor = Surface0,
            topBar = {
                TopAppBar(
                    navigationIcon = { IconButton({ scope.launch { drawer.open() } }) { Icon(Icons.Default.Menu, "Files") } },
                    title = { Text("untitled", style = MaterialTheme.typography.titleMedium) },
                    actions = {
                        IconButton({}) { Icon(Icons.Default.PlayArrow, "Run") }
                        IconButton({}) { Icon(Icons.Default.Search, "Find") }
                        IconButton(onHome) { Icon(Icons.Default.Home, "Home") }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Bar),
                )
            },
            bottomBar = {
                Row(Modifier.fillMaxWidth().background(Surface1).navigationBarsPadding().imePadding()
                    .horizontalScroll(rememberScrollState())) {
                    keys.forEach { k -> TextButton({ text += if (k == "Tab") "  " else k }) { Text(k, color = Muted) } }
                }
            },
        ) { pad ->
            Row(Modifier.padding(pad).fillMaxSize()) {
                Text(
                    (1..text.count { it == '\n' } + 1).joinToString("\n"),
                    Modifier.background(Surface1).padding(horizontal = 8.dp, vertical = 8.dp),
                    color = Muted, fontFamily = FontFamily.Monospace, fontSize = 14.sp, lineHeight = 20.sp,
                )
                BasicTextField(
                    text, { text = it },
                    Modifier.fillMaxSize().padding(8.dp),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = FontFamily.Monospace, fontSize = 14.sp, lineHeight = 20.sp),
                    cursorBrush = SolidColor(Accent),
                )
            }
        }
    }
}
