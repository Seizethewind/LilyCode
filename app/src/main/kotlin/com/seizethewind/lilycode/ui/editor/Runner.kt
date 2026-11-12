package com.seizethewind.lilycode.ui.editor

import android.provider.DocumentsContract
import com.seizethewind.lilycode.data.RunOutput
import com.seizethewind.lilycode.data.TermuxRun
import com.seizethewind.lilycode.util.TermuxPath
import com.seizethewind.lilycode.util.runSpec
import kotlinx.coroutines.launch

/** Saves the current file, then runs it in Termux. The result shows in the output panel. */
fun EditorState.runFile() {
    val d = doc ?: return
    val path = if (d.uri.authority == TermuxPath.AUTHORITY) DocumentsContract.getDocumentId(d.uri) else null
    if (path == null || !path.startsWith("/")) { error = "Run only works on files in Termux"; return }
    val spec = runSpec(path)
    if (spec == null) { error = "No runner for ${d.name}"; return }
    scope.launch {
        if (d.dirty && !saveDoc(d)) return@launch
        RunOutput.begin()
        try {
            TermuxRun.start(ctx, spec)
        } catch (e: Exception) {
            RunOutput.finish("Cannot start Termux: ${e.message}\nIs allow-external-apps=true set in termux.properties?")
        }
    }
}
