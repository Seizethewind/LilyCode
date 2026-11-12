package com.seizethewind.lilycode.ui.editor

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.seizethewind.lilycode.data.Entry
import com.seizethewind.lilycode.data.FileStore
import com.seizethewind.lilycode.data.Project
import com.seizethewind.lilycode.data.RunOutput
import com.seizethewind.lilycode.data.TermuxRun
import com.seizethewind.lilycode.util.TermuxPath
import com.seizethewind.lilycode.util.runSpec
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Open files, the current one, and every file operation of the editor. */
class EditorState(private val ctx: Context, private val scope: CoroutineScope, val project: Project) {
    private val store = FileStore(ctx.contentResolver)
    private val tree: Uri = Uri.parse(project.uri)
    val rootId: String = DocumentsContract.getTreeDocumentId(tree)
    val tabs = mutableStateListOf<Doc>()
    var current by mutableIntStateOf(-1)
    var error by mutableStateOf<String?>(null)
    val doc: Doc? get() = tabs.getOrNull(current)

    /** Runs blocking file work off the main thread; any failure becomes [error] and returns [fallback]. */
    private suspend fun <T> io(what: String, fallback: T, block: () -> T): T = try {
        withContext(Dispatchers.IO) { block() }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        error = "$what failed: ${e.message}"
        fallback
    }

    suspend fun list(dirId: String): List<Entry> = io("List", emptyList()) { store.list(tree, dirId) }

    fun open(uri: Uri, name: String) {
        val i = tabs.indexOfFirst { it.uri == uri }
        if (i >= 0) { current = i; return }
        scope.launch {
            val text = io("Open", null) { store.read(uri) } ?: return@launch
            if (tabs.none { it.uri == uri }) show(Doc(uri, name, text))
        }
    }

    fun openDocId(id: String) = open(DocumentsContract.buildDocumentUriUsingTree(tree, id), id.substringAfterLast('/'))

    fun create(dirId: String, name: String, done: () -> Unit) {
        scope.launch {
            val e = io("Create", null) { store.create(tree, dirId, name.trim()) } ?: return@launch
            show(Doc(e.uri, e.name, ""))
            done()
        }
    }

    private fun show(d: Doc) { tabs += d; current = tabs.lastIndex }

    suspend fun saveDoc(d: Doc): Boolean {
        val text = d.value.text
        val ok = io("Save", false) { store.write(d.uri, text); true }
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
            tabs.removeAt(idx)
            current = if (idx < current) current - 1 else minOf(current, tabs.lastIndex)
        }
    }

    fun runFile() {
        val d = doc ?: return
        val path = if (d.uri.authority == TermuxPath.AUTHORITY) DocumentsContract.getDocumentId(d.uri) else null
        if (path == null) { error = "Run only works on files in Termux"; return }
        val spec = runSpec(path)
        if (spec == null) { error = "No runner for ${d.name}"; return }
        scope.launch {
            if (d.dirty && !saveDoc(d)) return@launch
            RunOutput.begin()
            try {
                TermuxRun.start(ctx, spec)
            } catch (e: Exception) {
                RunOutput.finish("Cannot start Termux: ${e.message}\nIs allow-external-apps=true set in termux.properties?")
            }
        }
    }
}
