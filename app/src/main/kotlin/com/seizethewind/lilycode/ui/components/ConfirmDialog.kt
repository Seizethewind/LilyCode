package com.seizethewind.lilycode.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.seizethewind.lilycode.ui.theme.Surface1

@Composable
fun ConfirmDialog(text: String, confirm: String, onOk: () -> Unit, onDismiss: () -> Unit) = AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = Surface1,
    text = { Text(text) },
    confirmButton = { TextButton(onOk) { Text(confirm) } },
    dismissButton = { TextButton(onDismiss) { Text("Cancel") } },
)
