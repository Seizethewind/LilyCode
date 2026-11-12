package com.seizethewind.lilycode.ui.editor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.seizethewind.lilycode.ui.components.InputField
import com.seizethewind.lilycode.ui.theme.Surface1

@Composable
fun NameDialog(title: String, initial: String, onOk: (String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface1,
        title = { Text(title) },
        text = { InputField(name, { name = it }, "name", Modifier.fillMaxWidth()) },
        confirmButton = { TextButton({ onOk(name) }, enabled = name.isNotBlank()) { Text("OK") } },
        dismissButton = { TextButton(onDismiss) { Text("Cancel") } },
    )
}
