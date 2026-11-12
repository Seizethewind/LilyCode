package com.seizethewind.lilycode.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seizethewind.lilycode.ui.theme.Accent
import com.seizethewind.lilycode.ui.theme.Muted
import com.seizethewind.lilycode.ui.theme.Surface1

/** Line numbers beside the text field. Lines never wrap (horizontal scroll), so numbers stay aligned. */
@Composable
fun EditorBody(doc: Doc, transform: VisualTransformation, vs: ScrollState, modifier: Modifier = Modifier) {
    val style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, lineHeight = 20.sp)
    Row(modifier.fillMaxWidth().verticalScroll(vs)) {
        Text(
            (1..doc.value.text.count { it == '\n' } + 1).joinToString("\n"),
            Modifier.background(Surface1).padding(horizontal = 8.dp, vertical = 8.dp),
            color = Muted, style = style,
        )
        Box(Modifier.horizontalScroll(rememberScrollState())) {
            BasicTextField(
                doc.value, { doc.value = it },
                Modifier.padding(8.dp).defaultMinSize(minWidth = 320.dp, minHeight = 480.dp),
                readOnly = doc.readOnly,
                textStyle = style.copy(color = MaterialTheme.colorScheme.onBackground),
                cursorBrush = SolidColor(Accent),
                visualTransformation = transform,
            )
        }
    }
}
