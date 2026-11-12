package com.seizethewind.lilycode.ui

import android.content.Intent
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.seizethewind.lilycode.data.FileStore
import com.seizethewind.lilycode.data.Project
import com.seizethewind.lilycode.data.Projects
import com.seizethewind.lilycode.ui.editor.EditorScreen
import com.seizethewind.lilycode.ui.home.HomeScreen
import com.seizethewind.lilycode.util.TermuxPath

private const val RW = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

/** Home or editor. [incoming] is a Termux path sent by `lily <file>`. */
@Composable
fun App(incoming: MutableState<String?>) {
    val ctx = LocalContext.current.applicationContext
    val store = remember { Projects(ctx) }
    var projects by remember { mutableStateOf(store.all()) }
    var open by remember { mutableStateOf<Project?>(null) }
    var doc by remember { mutableStateOf<String?>(null) }

    val pick = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            runCatching { ctx.contentResolver.takePersistableUriPermission(uri, RW) }
            val root = DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri))
            val p = Project(FileStore(ctx.contentResolver).name(root), uri.toString())
            projects = store.add(p); doc = null; open = p
        }
    }

    LaunchedEffect(incoming.value) {
        val path = incoming.value ?: return@LaunchedEffect
        incoming.value = null
        val p = store.forPath(path)
        if (p != null) { doc = path; open = p }
        else Toast.makeText(ctx, "Add the folder as a LilyCode project first", Toast.LENGTH_LONG).show()
    }

    val p = open
    if (p != null) EditorScreen(p, doc, onHome = { open = null; doc = null })
    else HomeScreen(
        projects,
        onNew = { pick.launch(DocumentsContract.buildTreeDocumentUri(TermuxPath.AUTHORITY, "/data/data/com.termux/files/home")) },
        onOpen = { doc = null; open = it },
        onRemove = { r ->
            runCatching { ctx.contentResolver.releasePersistableUriPermission(android.net.Uri.parse(r.uri), RW) }
            projects = store.remove(r.uri)
        },
    )
}
