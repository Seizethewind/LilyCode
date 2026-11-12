package com.seizethewind.lilycode.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.DocumentsContract.Document
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.CodingErrorAction

/** [parentId] is the id of the folder that lists it. */
class Entry(val uri: Uri, val docId: String, val name: String, val isDir: Boolean, val parentId: String)

/** A file's text. [note] is set when it opens read-only, and says why. */
class Read(val text: String, val note: String?)

/** File access through the Storage Access Framework. Blocking: call off the main thread. */
class FileStore(private val cr: ContentResolver) {
    fun list(tree: Uri, dirId: String): List<Entry> {
        val kids = DocumentsContract.buildChildDocumentsUriUsingTree(tree, dirId)
        val cols = arrayOf(Document.COLUMN_DOCUMENT_ID, Document.COLUMN_DISPLAY_NAME, Document.COLUMN_MIME_TYPE)
        val out = ArrayList<Entry>()
        cr.query(kids, cols, null, null, null)?.use { c ->
            while (c.moveToNext()) {
                val id = c.getString(0)
                val dir = c.getString(2) == Document.MIME_TYPE_DIR
                out += Entry(DocumentsContract.buildDocumentUriUsingTree(tree, id), id, c.getString(1) ?: id, dir, dirId)
            }
        }
        return out.sortedWith(compareBy({ !it.isDir }, { it.name.lowercase() }))
    }

    /** Binary files are refused. Big or non-UTF-8 files open read-only, so saving can never corrupt them. */
    fun read(uri: Uri): Read {
        val (bytes, cut) = cr.openInputStream(uri)?.use { it.readUpTo(MAX_BYTES) } ?: error("Cannot open file")
        if (bytes.any { it == 0.toByte() }) error("Binary file")
        if (cut) return Read(String(bytes, Charsets.UTF_8), "Showing the first 1 MB only. Read-only.")
        return try {
            val strict = Charsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT)
            Read(strict.decode(ByteBuffer.wrap(bytes)).toString(), null)
        } catch (e: CharacterCodingException) {
            Read(String(bytes, Charsets.UTF_8), "Not valid UTF-8. Opened read-only so saving cannot corrupt it.")
        }
    }

    // shortcut: truncate-then-write, not atomic; Recovery keeps a copy until this returns
    fun write(uri: Uri, text: String) {
        (cr.openOutputStream(uri, "wt") ?: error("Cannot write file")).use { it.write(text.toByteArray()) }
    }

    fun create(tree: Uri, dirId: String, name: String): Entry {
        require(name.isNotBlank() && '/' !in name && name != "." && name != "..") { "Invalid file name" }
        val dir = DocumentsContract.buildDocumentUriUsingTree(tree, dirId)
        // octet-stream so providers do not append an extension
        val uri = DocumentsContract.createDocument(cr, dir, "application/octet-stream", name)
            ?: error("Cannot create $name")
        return Entry(uri, DocumentsContract.getDocumentId(uri), name, false, dirId)
    }

    fun name(uri: Uri): String = cr.query(uri, arrayOf(Document.COLUMN_DISPLAY_NAME), null, null, null)
        ?.use { if (it.moveToFirst()) it.getString(0) else null } ?: uri.lastPathSegment ?: "project"

    /** The first [max] bytes, and whether the stream had more. */
    private fun InputStream.readUpTo(max: Int): Pair<ByteArray, Boolean> {
        val out = ByteArrayOutputStream()
        val buf = ByteArray(8192)
        while (out.size() < max) {
            val n = read(buf, 0, minOf(buf.size, max - out.size()))
            if (n < 0) return out.toByteArray() to false
            out.write(buf, 0, n)
        }
        return out.toByteArray() to (read() >= 0)
    }

    private companion object { const val MAX_BYTES = 1_000_000 }
}
