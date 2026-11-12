package com.seizethewind.lilycode.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seizethewind.lilycode.ui.components.InputField
import com.seizethewind.lilycode.ui.theme.Muted
import com.seizethewind.lilycode.ui.theme.Surface1

/** Find-in-file bar. [active] is the current hit's index, or -1 before the first step. */
@Composable
fun FindBar(
    query: String,
    onQuery: (String) -> Unit,
    count: Int,
    active: Int,
    onStep: (Int) -> Unit,
    onClose: () -> Unit,
) {
    Row(Modifier.fillMaxWidth().background(Surface1).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        InputField(query, onQuery, "Find", Modifier.weight(1f))
        Text(
            if (active in 0 until count) "${active + 1}/$count" else "$count",
            Modifier.padding(horizontal = 8.dp), color = Muted, fontSize = 12.sp,
        )
        IconButton({ onStep(-1) }) { Icon(Icons.Default.KeyboardArrowUp, "Previous match") }
        IconButton({ onStep(1) }) { Icon(Icons.Default.KeyboardArrowDown, "Next match") }
        IconButton(onClose) { Icon(Icons.Default.Close, "Close find") }
    }
}
