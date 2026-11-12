package com.seizethewind.lilycode.data

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.DocumentsContract.Document
import com.seizethewind.lilycode.util.RunSpec
import com.seizethewind.lilycode.util.TERMUX_BIN
import com.seizethewind.lilycode.util.TermuxPath
import com.seizethewind.lilycode.util.isUnder

/**
 * Create, rename, delete, move and copy. Each returns the new document id.
 * Termux's documents provider cannot create, rename or move, so in a Termux folder these run as
 * shell commands in Termux. Blocking and suspending: call off the main thread.
 */
class FileOps(private val ctx: Context, private val tree: Uri, private val store: FileStore) {
    private val cr = ctx.contentResolver
    private val root = DocumentsContract.getTreeDocumentId(tree)
    private val shell = tree.authority == TermuxPath.AUTHORITY

    private fun doc(id: String) = DocumentsContract.buildDocumentUriUsingTree(tree, id)

    private fun idOf(uri: Uri?, what: String) = DocumentsContract.getDocumentId(uri ?: error("Cannot $what"))

    /** Rejects bad names and names already taken in [dirId], so nothing is ever overwritten. */
    private fun free(dirId: String, name: String) {
        require(name.isNotBlank() && '/' !in name && name != "." && name != "..") { "Invalid name" }
        if (store.list(tree, dirId).any { it.name == name }) error("$name already exists")
    }

    /** Shell paths must stay inside the project folder. */
    private fun path(id: String) = TermuxPath.inside(id, root) ?: error("Outside the project folder")

    private suspend fun sh(cmd: String, vararg args: String) {
        if (!TermuxRun.allowed(ctx)) error("Press Run once to grant the Termux permission")
        val r = TermuxRun.exec(ctx, RunSpec(TERMUX_BIN + cmd, args.toList(), root))
        if (r.exit != 0) error(r.text.ifEmpty { "$cmd failed" })
    }

    private fun notInto(e: Entry, toId: String) = check(!isUnder(toId, e.docId)) { "Cannot put a folder inside itself" }

    suspend fun newFile(dirId: String, name: String): String {
        free(dirId, name)
        if (!shell) return store.create(tree, dirId, name).docId
        return path("$dirId/$name").also { sh("touch", "--", it) }
    }

    suspend fun newFolder(dirId: String, name: String): String {
        free(dirId, name)
        if (!shell) return idOf(DocumentsContract.createDocument(cr, doc(dirId), Document.MIME_TYPE_DIR, name), "create folder")
        return path("$dirId/$name").also { sh("mkdir", "--", it) }
    }

    suspend fun rename(e: Entry, name: String): String {
        free(e.parentId, name)
        if (!shell) return idOf(DocumentsContract.renameDocument(cr, e.uri, name), "rename")
        return path("${e.parentId}/$name").also { sh("mv", "-n", "--", path(e.docId), it) }
    }

    suspend fun delete(e: Entry) {
        if (shell) sh("rm", "-r", "--", path(e.docId))
        else if (!DocumentsContract.deleteDocument(cr, e.uri)) error("Cannot delete ${e.name}")
    }

    suspend fun move(e: Entry, toId: String): String {
        free(toId, e.name)
        notInto(e, toId)
        if (!shell) return idOf(DocumentsContract.moveDocument(cr, e.uri, doc(e.parentId), doc(toId)), "move")
        return path("$toId/${e.name}").also { sh("mv", "-n", "--", path(e.docId), it) }
    }

    suspend fun copy(e: Entry, toId: String): String {
        free(toId, e.name)
        notInto(e, toId)
        if (!shell) return idOf(DocumentsContract.copyDocument(cr, e.uri, doc(toId)), "copy")
        return path("$toId/${e.name}").also { sh("cp", "-r", "-n", "--", path(e.docId), it) }
    }
}
