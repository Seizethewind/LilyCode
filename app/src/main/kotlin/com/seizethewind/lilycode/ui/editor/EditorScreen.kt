package com.seizethewind.lilycode.ui.editor

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.seizethewind.lilycode.data.Project
import com.seizethewind.lilycode.data.TermuxRun
import com.seizethewind.lilycode.ui.theme.Bar
import com.seizethewind.lilycode.ui.theme.Muted
import com.seizethewind.lilycode.ui.theme.Surface0
import com.seizethewind.lilycode.util.findAll
import com.seizethewind.lilycode.util.tokenize
import kotlinx.coroutines.launch

/** [initial] is a document id to open on arrival (from `lily <file>` in Termux). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(project: Project, initial: String?, onHome: () -> Unit) {
    val ctx = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    val state = remember(project) { EditorState(ctx, scope, project) }
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val snack = remember { SnackbarHostState() }
    val vs = rememberScrollState()
    val linePx = with(LocalDensity.current) { 20.sp.toPx() }
    var finding by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var active by remember { mutableIntStateOf(-1) }
    val doc = state.doc
    val text = doc?.value?.text.orEmpty()
    val hits = remember(text, query, finding) { if (finding) findAll(text, query) else emptyList() }
    val spans = remember(text, doc?.name) { tokenize(text, doc?.name.orEmpty()) }
    val transform = remember(spans, hits, query, active) { Highlighter(spans, hits, query.length, active) }
    val askRun = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) state.runFile() else state.error = "Run needs the Termux permission"
    }
    // Leaving saves every open file first, and stays here if a save fails, so nothing is lost.
    val exit = { scope.launch { if (state.saveAll()) onHome() } }
    BackHandler { exit() }
    LaunchedEffect(state, initial) { state.restore(); initial?.let(state::openDocId) }
    state.error?.let { msg -> LaunchedEffect(msg) { snack.showSnackbar(msg); state.error = null } }

    fun step(d: Int) {
        if (hits.isEmpty()) return
        active = if (active < 0) (if (d > 0) 0 else hits.lastIndex) else (active + d).mod(hits.size)
        val line = text.take(hits[active]).count { it == '\n' }
        scope.launch { vs.animateScrollTo(maxOf(0, ((line - 2) * linePx).toInt())) }
    }

    ModalNavigationDrawer(
        drawerState = drawer,
        drawerContent = { FileDrawer(state) { scope.launch { drawer.close() } } },
    ) {
        Scaffold(
            containerColor = Surface0,
            snackbarHost = { SnackbarHost(snack) },
            topBar = {
                TopAppBar(
                    navigationIcon = { IconButton({ scope.launch { drawer.open() } }) { Icon(Icons.Default.Menu, "Files") } },
                    title = {
                        val tag = if (doc?.readOnly == true) " (read-only)" else ""
                        Text((doc?.name ?: project.name) + tag, style = MaterialTheme.typography.titleMedium)
                    },
                    actions = {
                        IconButton({ state.save() }) { Icon(Icons.Default.Check, "Save") }
                        IconButton({ if (TermuxRun.allowed(ctx)) state.runFile() else askRun.launch(TermuxRun.PERM) }) {
                            Icon(Icons.Default.PlayArrow, "Run")
                        }
                        IconButton({ finding = !finding }) { Icon(Icons.Default.Search, "Find") }
                        IconButton({ exit() }) { Icon(Icons.Default.Home, "Home") }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Bar),
                )
            },
            bottomBar = { Column { OutputPanel(); KeyBar { k -> doc?.insert(if (k == "Tab") "  " else k) } } },
        ) { pad ->
            Column(Modifier.padding(pad).fillMaxSize()) {
                TabStrip(state)
                if (finding && doc != null) {
                    FindBar(query, { query = it; active = -1 }, hits.size, active, { step(it) }) {
                        finding = false; query = ""; active = -1
                    }
                }
                if (doc == null) Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Open a file from the menu", color = Muted) }
                else EditorBody(doc, transform, vs, Modifier.weight(1f))
            }
        }
    }
}
