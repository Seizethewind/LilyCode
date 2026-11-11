package com.seizethewind.lilycode.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Bar = Color(0xFF23293C)
val Surface0 = Color(0xFF121216)
val Surface1 = Color(0xFF1C1C20)
val Line = Color(0xFF27272A)
val Muted = Color(0xFFA1A1AA)
val Accent = Color(0xFF2E7EE5)

@Composable
fun LilyTheme(content: @Composable () -> Unit) = MaterialTheme(
    colorScheme = darkColorScheme(
        primary = Accent, background = Surface0, surface = Surface1,
        onBackground = Color(0xFFE5E7EB), onSurface = Color(0xFFE5E7EB),
    ),
    content = content,
)
