package com.thedearbear.nnov.ui.state

import androidx.compose.runtime.Composable

data class MainState(
    val id: Int = -1,
    val dialog: @Composable (() -> Unit)? = null,

    // Account tab
    val name: String = "",
    val email: String = "",
    val school: String = "",
    val teacher: String = "",
    val grade: String = ""
)
