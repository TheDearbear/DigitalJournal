package com.thedearbear.nnov.ui.state

import com.thedearbear.nnov.home.MailBoxMessage

data class MailBoxState(
    val lastInboxId: Int = -1,
    val inbox: List<MailBoxMessage> = listOf(),
    val lastSentId: Int = -1,
    val sent: List<MailBoxMessage> = listOf(),
    val showLoader: Boolean = false
)
