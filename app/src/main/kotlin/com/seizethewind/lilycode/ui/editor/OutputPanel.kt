package com.seizethewind.lilycode.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seizethewind.lilycode.data.RunOutput
import com.seizethewind.lilycode.ui.theme.Bar
import com.seizethewind.lilycode.ui.theme.Muted
import com.seizethewind.lilycode.ui.theme.Surface0

/** Shows the last Termux run's output above the key bar; hidden when there is nothing to show. */
@Composable
fun OutputPanel() {
    if (!RunOutput.running && RunOutput.text.isEmpty()) return
    Column(Modifier.fillMaxWidth().background(Surface0).heightIn(max = 180.dp)) {
        Row(Modifier.fillMaxWidth().background(Bar), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text(if (RunOutput.running) "Running\u2026" else "Output", Modifier.padding(start = 12.dp), color = Muted, fontSize = 12.sp)
            IconButton(RunOutput::clear, Modifier.size(32.dp)) { Icon(Icons.Default.Close, "Close output", Modifier.size(16.dp)) }
        }
        Text(
            RunOutput.text,
            Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()).padding(8.dp),
            color = MaterialTheme.colorScheme.onBackground, fontFamily = FontFamily.Monospace, fontSize = 12.sp,
        )
    }
}
