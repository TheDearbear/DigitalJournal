package com.thedearbear.nnov.ui.state

import android.icu.util.DateInterval
import com.thedearbear.nnov.journal.DayEntry

data class JournalState(
    val id: Int = -1,
    val showLoader: Boolean = false,
    val period: DateInterval = DateInterval(0, 0),
    val days: List<Pair<DayEntry, Boolean>> = listOf()
)
