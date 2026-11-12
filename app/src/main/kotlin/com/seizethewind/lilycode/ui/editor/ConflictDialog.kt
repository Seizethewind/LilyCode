package com.seizethewind.lilycode.ui.editor

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.seizethewind.lilycode.ui.theme.Surface1
import kotlinx.coroutines.launch

/** Shown when a save finds the file changed on disk. Tapping outside keeps editing and writes nothing. */
@Composable
fun ConflictDialog(state: EditorState) {
    val d = state.conflict ?: return
    AlertDialog(
        onDismissRequest = { state.conflict = null },
        containerColor = Surface1,
        title = { Text("File changed on disk") },
        text = { Text("${d.name} was changed outside LilyCode since you opened it. Overwrite it with your version, or reload it and lose your edits?") },
        confirmButton = { TextButton({ state.scope.launch { state.overwrite(d) } }) { Text("Overwrite") } },
        dismissButton = { TextButton({ state.conflict = null; state.scope.launch { state.reload(d) } }) { Text("Reload") } },
    )
}
