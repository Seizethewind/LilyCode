package com.seizethewind.lilycode.ui.editor

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/** One open file. [saved] is the text last written, so [dirty] means unsaved edits. */
class Doc(val uri: Uri, val name: String, text: String) {
    var value by mutableStateOf(TextFieldValue(text))
    var saved by mutableStateOf(text)
    val dirty get() = value.text != saved

    /** Replaces the selection, so the key bar types at the cursor. */
    fun insert(s: String) {
        val sel = value.selection
        value = value.copy(text = value.text.replaceRange(sel.min, sel.max, s), selection = TextRange(sel.min + s.length))
    }
}
