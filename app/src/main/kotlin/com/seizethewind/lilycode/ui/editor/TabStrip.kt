package com.seizethewind.lilycode.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seizethewind.lilycode.ui.theme.Muted
import com.seizethewind.lilycode.ui.theme.Surface0
import com.seizethewind.lilycode.ui.theme.Surface1

@Composable
fun TabStrip(state: EditorState) {
    if (state.tabs.isEmpty()) return
    Row(Modifier.fillMaxWidth().background(Surface1).horizontalScroll(rememberScrollState())) {
        state.tabs.forEachIndexed { i, d ->
            val on = i == state.current
            Row(
                Modifier.background(if (on) Surface0 else Surface1).clickable { state.select(i) }.padding(start = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    (if (d.dirty) "\u25CF " else "") + d.name, fontSize = 13.sp,
                    color = if (on) MaterialTheme.colorScheme.onBackground else Muted,
                )
                IconButton({ state.close(i) }, Modifier.size(32.dp)) { Icon(Icons.Default.Close, "Close tab", Modifier.size(14.dp)) }
            }
        }
    }
}
