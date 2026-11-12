package com.seizethewind.lilycode.ui.editor

import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private fun leaf(uri: Uri) = DocumentsContract.getDocumentId(uri).substringAfterLast('/')

/** Reopens last session's tabs, then any unsaved text left in app storage. Runs once; drafts wait for it. */
suspend fun EditorState.restore() {
    if (restored) return
    restored = true
    try {
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
        if (kept.isNotEmpty()) error = "Recovered unsaved text. Save to keep it."
        persist()
    } finally {
        ready = true
    }
}
