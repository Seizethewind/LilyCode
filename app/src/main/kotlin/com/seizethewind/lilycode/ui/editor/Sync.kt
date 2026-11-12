package com.seizethewind.lilycode.ui.editor

import androidx.compose.ui.text.input.TextFieldValue

/**
 * Replaces [d]'s text with what is on disk, dropping its unsaved edits. With [onlyIfClean] it does nothing if the
 * user typed while the file was being read. A file that can no longer be edited (now too big or not UTF-8) is left alone.
 */
suspend fun EditorState.reload(d: Doc, onlyIfClean: Boolean = false) {
    val r = io("Reload", null) { store.read(d.uri) } ?: return
    if (r.note != null) { error = "${d.name} changed on disk and can no longer be edited. Close its tab to reopen it."; return }
    if (onlyIfClean && d.dirty) return
    d.value = TextFieldValue(r.text)
    d.saved = r.text
    d.stamp = r.stamp
    io("Reload", Unit) { recovery.drop(d.uri) }
}

/** The user chose to replace the disk copy with [d]'s text. */
suspend fun EditorState.overwrite(d: Doc) {
    conflict = null
    saveDoc(d, force = true)
}

/** Pulls in changes made elsewhere (for example in Termux) to every tab that has no unsaved edits. */
suspend fun EditorState.refreshClean() {
    if (!ready) return
    for (d in tabs.toList()) {
        val base = d.stamp
        if (d.dirty || d.readOnly || base == null) continue
        val now = io("Check", null) { store.stamp(d.uri) }
        if (now != null && now != base) reload(d, onlyIfClean = true)
    }
}
