package com.seizethewind.lilycode.data

import android.content.Context
import android.net.Uri
import java.io.File
import java.security.MessageDigest

/**
 * App-private copy of the text being saved, kept until the write succeeds. If the write fails or the app
 * dies halfway (a truncating write is not atomic), the text is still here to be restored.
 */
class Recovery(ctx: Context) {
    private val dir = File(ctx.filesDir, "recovery").apply { mkdirs() }

    private fun file(uri: Uri): File {
        val hash = MessageDigest.getInstance("SHA-256").digest(uri.toString().toByteArray())
        return File(dir, hash.joinToString("") { "%02x".format(it) })
    }

    fun keep(uri: Uri, text: String) = file(uri).writeText("$uri\n$text")

    fun drop(uri: Uri) { file(uri).delete() }

    /** Kept copies whose file uri starts with [prefix], as (uri, text). */
    fun pending(prefix: String): List<Pair<Uri, String>> = dir.listFiles().orEmpty().mapNotNull { f ->
        val s = f.readText()
        val nl = s.indexOf('\n')
        if (nl > 0 && s.startsWith(prefix)) Uri.parse(s.substring(0, nl)) to s.substring(nl + 1) else null
    }
}
