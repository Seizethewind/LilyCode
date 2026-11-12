package com.seizethewind.lilycode.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seizethewind.lilycode.ui.theme.Accent
import com.seizethewind.lilycode.ui.theme.Line
import com.seizethewind.lilycode.ui.theme.Muted

/** Single-line text input with a hint; takes focus when it appears. Used for search, find and names. */
@Composable
fun InputField(value: String, onChange: (String) -> Unit, hint: String, modifier: Modifier = Modifier) {
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    BasicTextField(
        value, onChange,
        modifier.focusRequester(focus).background(Line).padding(horizontal = 12.dp, vertical = 10.dp),
        singleLine = true,
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp),
        cursorBrush = SolidColor(Accent),
        decorationBox = { inner -> Box { if (value.isEmpty()) Text(hint, color = Muted, fontSize = 14.sp); inner() } },
    )
}
