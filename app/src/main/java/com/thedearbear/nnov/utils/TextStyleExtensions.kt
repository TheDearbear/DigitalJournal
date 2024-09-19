package com.thedearbear.nnov.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration

fun materialTextLinkStyles(): TextLinkStyles {
    return TextLinkStyles(
        // Material 500
        style = SpanStyle(color = Color(63, 81, 181, 255)),
        hoveredStyle = SpanStyle(textDecoration = TextDecoration.Underline),
        // Material 900
        pressedStyle = SpanStyle(color = Color(26, 35, 126, 255))
    )
}