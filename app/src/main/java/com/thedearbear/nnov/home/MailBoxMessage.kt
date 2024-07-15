package com.thedearbear.nnov.home

import java.time.LocalDateTime

data class MailBoxMessage(
    val id: Int,
    val title: String,
    val body: String,
    val author: String,
    val receivers: List<String>,
    val date: LocalDateTime
)
