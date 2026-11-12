package com.seizethewind.lilycode.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Output of the last Termux run, shown by the editor's output panel. */
object RunOutput {
    var text by mutableStateOf("")
    var running by mutableStateOf(false)

    fun begin() { text = ""; running = true }
    fun finish(out: String) { text = out; running = false }
    fun clear() = finish("")
}
