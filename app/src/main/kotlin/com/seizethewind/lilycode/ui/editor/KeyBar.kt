package com.seizethewind.lilycode.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.seizethewind.lilycode.ui.theme.Muted
import com.seizethewind.lilycode.ui.theme.Surface1

private val keys = listOf("Tab", "{", "}", "(", ")", "[", "]", ";", "\"", "'", "<", ">", "/", "=", "+", "-", "*")

@Composable
fun KeyBar(onKey: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().background(Surface1).navigationBarsPadding().imePadding()
        .horizontalScroll(rememberScrollState())) {
        keys.forEach { k -> TextButton({ onKey(k) }) { Text(k, color = Muted) } }
    }
}
