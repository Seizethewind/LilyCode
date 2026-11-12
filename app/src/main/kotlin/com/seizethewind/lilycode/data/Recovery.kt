package com.seizethewind.lilycode.data

import android.content.Context
import android.net.Uri
import com.seizethewind.lilycode.util.joinDraft
import com.seizethewind.lilycode.util.splitDraft
import java.io.File
import java.security.MessageDigest

/** Unsaved text kept for [uri]. [stamp] is the file's disk stamp the text was based on. */
class Draft(val uri: Uri, val stamp: String?, val text: String)

/**
 * App-private copies of unsaved text, and of text being saved. A copy is dropped once the file is written, so a
 * kill, a failed write or a half-finished truncating write loses nothing.
 */
class Recovery(ctx: Context) {
    private val dir = File(ctx.filesDir, "recovery").apply { mkdirs() }

    private fun file(uri: Uri): File {
        val hash = MessageDigest.getInstance("SHA-256").digest(uri.toString().toByteArray())
        return File(dir, hash.joinToString("") { "%02x".format(it) })
    }

    fun keep(uri: Uri, text: String, stamp: String?) = file(uri).writeText(joinDraft(uri.toString(), stamp, text))

    fun drop(uri: Uri) { file(uri).delete() }

    /** Kept copies whose file uri starts with [prefix]. */
    fun pending(prefix: String): List<Draft> = dir.listFiles().orEmpty().mapNotNull { f ->
        splitDraft(f.readText())?.takeIf { it.uri.startsWith(prefix) }?.let { Draft(Uri.parse(it.uri), it.stamp, it.text) }
    }
}
