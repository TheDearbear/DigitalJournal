package com.thedearbear.nnov.journal

import java.time.LocalTime

data class Lesson(
    val number: Int,
    val subNumber: Int = 0,
    val name: String,
    val homework: List<Homework>,
    val marks: List<String>,
    val time: Pair<LocalTime, LocalTime>?
)
