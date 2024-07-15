package com.thedearbear.nnov.ui.state

import com.thedearbear.nnov.home.Announcement

data class HomeState(
    val id: Int = -1,
    val showLoader: Boolean = false,
    val announcements: List<Announcement> = listOf()
)
