package com.thedearbear.nnov.home

import java.time.LocalDate

data class Announcement(
    val id: Int,
    val title: String,
    val body: String,
    val author: String,
    val date: LocalDate
)
