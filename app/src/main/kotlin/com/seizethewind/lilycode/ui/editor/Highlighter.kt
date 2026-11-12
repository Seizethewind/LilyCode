package com.seizethewind.lilycode.ui.editor

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.seizethewind.lilycode.ui.theme.ActiveBg
import com.seizethewind.lilycode.ui.theme.MatchBg
import com.seizethewind.lilycode.ui.theme.SynComment
import com.seizethewind.lilycode.ui.theme.SynKeyword
import com.seizethewind.lilycode.ui.theme.SynNumber
import com.seizethewind.lilycode.ui.theme.SynString
import com.seizethewind.lilycode.util.Kind
import com.seizethewind.lilycode.util.Span

/** Colors syntax [spans] and find [hits] (each [len] long; [active] is the current hit's index). */
class Highlighter(
    private val spans: List<Span>,
    private val hits: List<Int>,
    private val len: Int,
    private val active: Int,
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val n = text.length // spans can be one edit stale, so never style past the end
        val out = buildAnnotatedString {
            append(text.text)
            spans.filter { it.end <= n }.forEach { addStyle(SpanStyle(color = color(it.kind)), it.start, it.end) }
            hits.forEachIndexed { i, at ->
                if (at + len <= n) addStyle(SpanStyle(background = if (i == active) ActiveBg else MatchBg), at, at + len)
            }
        }
        return TransformedText(out, OffsetMapping.Identity)
    }

    private fun color(k: Kind) = when (k) {
        Kind.Keyword -> SynKeyword
        Kind.Str -> SynString
        Kind.Comment -> SynComment
        Kind.Number -> SynNumber
    }
}
