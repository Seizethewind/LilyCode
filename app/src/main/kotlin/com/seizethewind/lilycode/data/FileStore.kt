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

class Entry(val uri: Uri, val docId: String, val name: String, val isDir: Boolean)

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
                out += Entry(DocumentsContract.buildDocumentUriUsingTree(tree, id), id, c.getString(1) ?: id, dir)
            }
        }
        return out.sortedWith(compareBy({ !it.isDir }, { it.name.lowercase() }))
    }

    /** Refuses big, binary and non-UTF-8 files so saving can never corrupt them. */
    fun read(uri: Uri): String {
        val bytes = cr.openInputStream(uri)?.use { it.readUpTo(MAX_BYTES) } ?: error("Cannot open file")
        if (bytes.any { it == 0.toByte() }) error("Binary file")
        try {
            return Charsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT)
                .decode(ByteBuffer.wrap(bytes)).toString()
        } catch (e: CharacterCodingException) {
            error("Not a UTF-8 text file")
        }
    }

    // shortcut: truncate-then-write, not atomic; upgrade to temp file + rename if providers allow it
    fun write(uri: Uri, text: String) {
        (cr.openOutputStream(uri, "wt") ?: error("Cannot write file")).use { it.write(text.toByteArray()) }
    }

    fun create(tree: Uri, dirId: String, name: String): Entry {
        require(name.isNotBlank() && '/' !in name && name != "." && name != "..") { "Invalid file name" }
        val dir = DocumentsContract.buildDocumentUriUsingTree(tree, dirId)
        // octet-stream so providers do not append an extension
        val uri = DocumentsContract.createDocument(cr, dir, "application/octet-stream", name)
            ?: error("Cannot create $name")
        return Entry(uri, DocumentsContract.getDocumentId(uri), name, false)
    }

    fun name(uri: Uri): String = cr.query(uri, arrayOf(Document.COLUMN_DISPLAY_NAME), null, null, null)
        ?.use { if (it.moveToFirst()) it.getString(0) else null } ?: uri.lastPathSegment ?: "project"

    private fun InputStream.readUpTo(max: Int): ByteArray {
        val out = ByteArrayOutputStream()
        val buf = ByteArray(8192)
        while (true) {
            val n = read(buf)
            if (n < 0) break
            if (out.size() + n > max) error("File is larger than 1 MB")
            out.write(buf, 0, n)
        }
        return out.toByteArray()
    }

    private companion object { const val MAX_BYTES = 1_000_000 }
}
