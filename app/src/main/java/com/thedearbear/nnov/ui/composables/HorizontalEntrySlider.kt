package com.thedearbear.nnov.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HorizontalEntrySlider(
    modifier: Modifier = Modifier,
    previous: () -> Boolean = { false },
    entry: @Composable () -> Unit,
    next: () -> Boolean = { false },
    onEntryChanged: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onEntryChanged(-1) },
            enabled = previous()
        ) {
            Icon(Icons.AutoMirrored.Default.KeyboardArrowLeft, "Previous week")
        }

        entry()
        
        IconButton(
            onClick = { onEntryChanged(1) },
            enabled = next()
        ) {
            Icon(Icons.AutoMirrored.Default.KeyboardArrowRight, contentDescription = "Next week")
        }
    }
}