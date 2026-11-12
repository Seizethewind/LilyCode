package com.seizethewind.lilycode.ui.editor

import android.net.Uri

private class Snap(val uri: Uri, val text: String, val dirty: Boolean)

private fun EditorState.snap() = tabs.map { Snap(it.uri, it.value.text, it.dirty) }

// A tab that is clean again (saved, or edited back to its saved text) must not leave an old draft to be restored.
private fun EditorState.write(snaps: List<Snap>) =
    snaps.forEach { if (it.dirty) recovery.keep(it.uri, it.text) else recovery.drop(it.uri) }

/** Copies every unsaved tab to app storage, off the main thread. Not before the session is restored. */
suspend fun EditorState.saveDrafts() {
    if (!ready) return
    val snaps = snap()
    io("Draft", Unit) { write(snaps) }
}

/** The same, but blocking, for when the app goes to the background: Android may kill it right after. */
fun EditorState.saveDraftsNow() {
    if (ready) write(snap())
}
