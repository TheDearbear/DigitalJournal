package com.thedearbear.nnov.ui.state

import com.thedearbear.nnov.home.Announcement

data class AnnouncementState(
    val announcement: Announcement? = null,
    val showLoader: Boolean = false
)
