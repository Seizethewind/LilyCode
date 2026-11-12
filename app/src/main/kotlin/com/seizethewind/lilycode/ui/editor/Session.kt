package com.seizethewind.lilycode.ui.editor

import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private fun leaf(uri: Uri) = DocumentsContract.getDocumentId(uri).substringAfterLast('/')

/**
 * Reopens last session's tabs, then any unsaved text left in app storage. A recovered draft keeps the disk stamp it
 * was based on, so saving it cannot silently overwrite a newer file. Runs once; drafts wait for it.
 */
suspend fun EditorState.restore() {
    if (restored) return
    restored = true
    try {
        val (uris, cur) = projects.session(project)
        for (u in uris) {
            val uri = Uri.parse(u)
            val r = runCatching { withContext(Dispatchers.IO) { store.read(uri) } }.getOrNull() ?: continue
            tabs += Doc(uri, leaf(uri), r.text, r.note != null, stamp = r.stamp)
        }
        current = cur.coerceIn(-1, tabs.lastIndex)
        val kept = withContext(Dispatchers.IO) { recovery.pending(project.uri) }
        for (k in kept) {
            val open = tabs.firstOrNull { it.uri == k.uri }
            if (open != null) { open.value = TextFieldValue(k.text); open.stamp = k.stamp }
            else tabs += Doc(k.uri, leaf(k.uri), k.text, saved = "", stamp = k.stamp)
        }
        if (kept.isNotEmpty()) error = "Recovered unsaved text. Save to keep it."
        persist()
    } finally {
        ready = true
    }
}
