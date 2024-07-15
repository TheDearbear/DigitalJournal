package com.thedearbear.nnov.ui.state

import com.thedearbear.nnov.home.MailBoxMessage

data class MailBoxMessageState(
    val lastMessageId: Int = -1,
    val message: MailBoxMessage? = null,
    val showLoader: Boolean = false
)
