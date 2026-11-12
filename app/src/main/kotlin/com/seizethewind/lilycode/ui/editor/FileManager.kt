package com.seizethewind.lilycode.ui.editor

import android.provider.DocumentsContract
import com.seizethewind.lilycode.data.Entry
import com.seizethewind.lilycode.util.isUnder
import com.seizethewind.lilycode.util.remapId
import kotlinx.coroutines.launch

/** A copied or cut entry waiting to be pasted. */
class Clip(val entry: Entry, val move: Boolean)

/**
 * The drawer's file actions. Each runs off the main thread, keeps open tabs pointing at the right file,
 * then calls [done] so the drawer reloads. Failures show as the editor's error message.
 */
class FileManager(private val state: EditorState) {
    private val ops = state.ops

    fun newFile(dirId: String, name: String, done: () -> Unit) {
        state.scope.launch {
            val n = name.trim()
            val id = state.io("Create", null) { ops.newFile(dirId, n) }
            if (id != null) state.show(Doc(state.uriOf(id), n, ""))
            done()
        }
    }

    fun newFolder(dirId: String, name: String, done: () -> Unit) {
        state.scope.launch {
            state.io("Create folder", null) { ops.newFolder(dirId, name.trim()) }
            done()
        }
    }

    fun rename(e: Entry, name: String, done: () -> Unit) {
        state.scope.launch {
            val n = name.trim()
            val id = state.io("Rename", null) { ops.rename(e, n) }
            if (id != null) follow(e.docId, id, n)
            done()
        }
    }

    fun paste(c: Clip, toId: String, done: () -> Unit) {
        state.scope.launch {
            val e = c.entry
            val id = state.io(if (c.move) "Move" else "Copy", null) { if (c.move) ops.move(e, toId) else ops.copy(e, toId) }
            if (id != null && c.move) follow(e.docId, id, e.name)
            done()
        }
    }

    fun delete(e: Entry, done: () -> Unit) {
        state.scope.launch {
            if (state.io("Delete", false) { ops.delete(e); true }) forget(e.docId)
            done()
        }
    }

    // shortcut: tabs are matched by path-like document ids; with opaque ids a moved tab keeps its old uri and its save fails safely
    private fun follow(old: String, dest: String, name: String) {
        state.tabs.forEach { d ->
            val id = remapId(DocumentsContract.getDocumentId(d.uri), old, dest) ?: return@forEach
            d.uri = state.uriOf(id)
            d.stamp = null // a move can change the reported time; change detection resumes after the next save
            if (id == dest) d.name = name
        }
        state.persist()
    }

    private fun forget(id: String) {
        state.tabs.removeAll { isUnder(DocumentsContract.getDocumentId(it.uri), id) }
        state.current = state.current.coerceIn(-1, state.tabs.lastIndex)
        state.persist()
    }
}
