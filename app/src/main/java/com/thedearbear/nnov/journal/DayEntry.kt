package com.thedearbear.nnov.journal

import java.time.LocalDate

data class DayEntry(
    val date: LocalDate,
    val type: DayType = DayType.NORMAL,
    val typeSpecific: String? = null,
    val lessons: List<Lesson> = listOf()
)
