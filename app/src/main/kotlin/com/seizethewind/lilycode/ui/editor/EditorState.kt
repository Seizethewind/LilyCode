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

/** Open files, the current one, and opening, saving and closing them. Restore is in Session.kt, drafts in Drafts.kt, disk changes in Sync.kt. */
class EditorState(val ctx: Context, val scope: CoroutineScope, val project: Project) {
    internal val store = FileStore(ctx.contentResolver)
    internal val projects = Projects(ctx)
    internal val recovery = Recovery(ctx)
    internal var restored = false
    internal var ready = false
    private val tree: Uri = Uri.parse(project.uri)
    val ops = FileOps(ctx, tree, store)
    val rootId: String = DocumentsContract.getTreeDocumentId(tree)
    val tabs = mutableStateListOf<Doc>()
    var current by mutableIntStateOf(-1)
    var error by mutableStateOf<String?>(null)
    var conflict by mutableStateOf<Doc?>(null)
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

    suspend fun list(dirId: String): List<Entry> = io("List", emptyList()) { store.list(tree, dirId) }

    fun open(uri: Uri, name: String) {
        val i = tabs.indexOfFirst { it.uri == uri }
        if (i >= 0) { select(i); return }
        scope.launch {
            val r = io("Open", null) { store.read(uri) } ?: return@launch
            if (tabs.none { it.uri == uri }) {
                show(Doc(uri, name, r.text, r.note != null, stamp = r.stamp))
                r.note?.let { error = it }
            }
        }
    }

    fun openDocId(id: String) = open(uriOf(id), id.substringAfterLast('/'))

    fun select(i: Int) { current = i; persist() }

    fun show(d: Doc) { tabs += d; select(tabs.lastIndex) }

    fun persist() = projects.saveSession(project, tabs.map { it.uri.toString() }, current)

    /**
     * Saves [d]. If the file changed on disk since it was read, nothing is written and [conflict] asks the user;
     * [force] skips that check. The text is copied to app storage first, and that copy is dropped only after the write.
     */
    suspend fun saveDoc(d: Doc, force: Boolean = false): Boolean {
        if (d.readOnly) return true
        val text = d.value.text
        val uri = d.uri
        val base = d.stamp
        if (!force && base != null && io("Save", base) { store.stamp(uri) ?: base } != base) { conflict = d; return false }
        val ok = io("Save", false) { recovery.keep(uri, text, base); store.write(uri, text); recovery.drop(uri); true }
        if (ok) { d.saved = text; d.stamp = io("Save", null) { store.stamp(uri) } }
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
