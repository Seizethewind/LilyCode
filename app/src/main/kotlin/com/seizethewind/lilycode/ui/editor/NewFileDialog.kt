package com.seizethewind.lilycode.ui.editor

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.seizethewind.lilycode.ui.components.InputField
import com.seizethewind.lilycode.ui.theme.Surface1

@Composable
fun NewFileDialog(onCreate: (String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface1,
        title = { Text("New file") },
        text = { InputField(name, { name = it }, "name.ext", Modifier.fillMaxWidth()) },
        confirmButton = { TextButton({ onCreate(name) }, enabled = name.isNotBlank()) { Text("Create") } },
        dismissButton = { TextButton(onDismiss) { Text("Cancel") } },
    )
}
