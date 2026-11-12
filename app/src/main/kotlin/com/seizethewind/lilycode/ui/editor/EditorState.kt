package com.seizethewind.lilycode.ui.editor

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.seizethewind.lilycode.data.Entry
import com.seizethewind.lilycode.data.FileOps
import com.seizethewind.lilycode.data.FileStore
import com.seizethewind.lilycode.data.Project
import com.seizethewind.lilycode.data.Projects
import com.seizethewind.lilycode.data.Recovery
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Open files, the current one, and opening, saving, closing and restoring them. */
class EditorState(val ctx: Context, val scope: CoroutineScope, val project: Project) {
    private val store = FileStore(ctx.contentResolver)
    private val projects = Projects(ctx)
    private val recovery = Recovery(ctx)
    private val tree: Uri = Uri.parse(project.uri)
    private var restored = false
    val ops = FileOps(ctx, tree, store)
    val rootId: String = DocumentsContract.getTreeDocumentId(tree)
    val tabs = mutableStateListOf<Doc>()
    var current by mutableIntStateOf(-1)
    var error by mutableStateOf<String?>(null)
    val doc: Doc? get() = tabs.getOrNull(current)

    /** Runs blocking file work off the main thread; any failure becomes [error] and returns [fallback]. */
    suspend fun <T> io(what: String, fallback: T, block: suspend () -> T): T = try {
        withContext(Dispatchers.IO) { block() }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        error = "$what failed: ${e.message}"
        fallback
    }

    fun uriOf(id: String): Uri = DocumentsContract.buildDocumentUriUsingTree(tree, id)

    private fun leaf(uri: Uri) = DocumentsContract.getDocumentId(uri).substringAfterLast('/')

    suspend fun list(dirId: String): List<Entry> = io("List", emptyList()) { store.list(tree, dirId) }

    fun open(uri: Uri, name: String) {
        val i = tabs.indexOfFirst { it.uri == uri }
        if (i >= 0) { select(i); return }
        scope.launch {
            val r = io("Open", null) { store.read(uri) } ?: return@launch
            if (tabs.none { it.uri == uri }) {
                show(Doc(uri, name, r.text, r.note != null))
                r.note?.let { error = it }
            }
        }
    }

    fun openDocId(id: String) = open(uriOf(id), id.substringAfterLast('/'))

    fun select(i: Int) { current = i; persist() }

    fun show(d: Doc) { tabs += d; select(tabs.lastIndex) }

    fun persist() = projects.saveSession(project, tabs.map { it.uri.toString() }, current)

    /** Reopens last session's tabs, then any text a failed or interrupted save left behind. Runs once. */
    suspend fun restore() {
        if (restored) return
        restored = true
        val (uris, cur) = projects.session(project)
        for (u in uris) {
            val uri = Uri.parse(u)
            val r = runCatching { withContext(Dispatchers.IO) { store.read(uri) } }.getOrNull() ?: continue
            tabs += Doc(uri, leaf(uri), r.text, r.note != null)
        }
        current = cur.coerceIn(-1, tabs.lastIndex)
        val kept = withContext(Dispatchers.IO) { recovery.pending(project.uri) }
        for ((uri, text) in kept) {
            val open = tabs.firstOrNull { it.uri == uri }
            if (open != null) open.value = TextFieldValue(text) else tabs += Doc(uri, leaf(uri), text, saved = "")
        }
        if (kept.isNotEmpty()) error = "Recovered text from an unfinished save. Save to keep it."
        persist()
    }

    /** The text is copied to app storage first, and that copy is dropped only after the write succeeds. */
    suspend fun saveDoc(d: Doc): Boolean {
        if (d.readOnly) return true
        val text = d.value.text
        val uri = d.uri
        val ok = io("Save", false) { recovery.keep(uri, text); store.write(uri, text); recovery.drop(uri); true }
        if (ok) d.saved = text
        return ok
    }

    suspend fun saveAll(): Boolean = tabs.filter { it.dirty }.all { saveDoc(it) }

    fun save() { doc?.let { d -> scope.launch { saveDoc(d) } } }

    /** Saves first; if saving fails the tab stays open so no text is lost. */
    fun close(i: Int) {
        val d = tabs.getOrNull(i) ?: return
        scope.launch {
            if (d.dirty && !saveDoc(d)) return@launch
            val idx = tabs.indexOf(d)
            if (idx < 0) return@launch
            tabs.removeAt(idx)
            current = if (idx < current) current - 1 else minOf(current, tabs.lastIndex)
            persist()
        }
    }
}
