package com.thedearbear.nnov.journal

data class Homework(
    val id: Int,
    val message: String,
    val personal: Boolean,
    val files: List<HomeworkFile>
)
