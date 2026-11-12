package com.seizethewind.lilycode.ui.editor

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/**
 * One open file. [saved] is the text last written, so [dirty] means unsaved edits. A [readOnly] file
 * (too big or not UTF-8) can never be edited or written. [uri] and [name] change when the file is renamed or moved.
 * [stamp] is the file's disk stamp when it was last read or written; null turns off change detection for it.
 */
class Doc(
    uri: Uri,
    name: String,
    text: String,
    val readOnly: Boolean = false,
    saved: String = text,
    var stamp: String? = null,
) {
    var uri by mutableStateOf(uri)
    var name by mutableStateOf(name)
    var value by mutableStateOf(TextFieldValue(text))
    var saved by mutableStateOf(saved)
    val dirty get() = !readOnly && value.text != saved

    /** Replaces the selection, so the key bar types at the cursor. */
    fun insert(s: String) {
        if (readOnly) return
        val sel = value.selection
        value = value.copy(text = value.text.replaceRange(sel.min, sel.max, s), selection = TextRange(sel.min + s.length))
    }
}
