package com.thedearbear.nnov.tabs

import android.icu.util.DateInterval
import android.icu.util.GregorianCalendar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.thedearbear.nnov.R
import com.thedearbear.nnov.journal.DayEntry
import com.thedearbear.nnov.ui.composables.DiaryDay
import com.thedearbear.nnov.ui.composables.HorizontalEntrySlider
import com.thedearbear.nnov.ui.viewmodel.JournalViewModel
import java.time.LocalDate
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalTab(
    userId: Int,
    showRings: Boolean = true,
    //showCabinet: Boolean = false,
    viewModel: JournalViewModel,
    onHomework: (DayEntry, Int) -> Unit,
    onReloadRequest: (Int) -> Unit,
    onReloadSuccess: () -> Unit,
    onAuthFailure: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val state by viewModel.state.collectAsState()

    fun updateJournal(period: DateInterval) {
        viewModel.updateShowLoader(true)

        viewModel.fetchJournalInfo(
            id = state.id,
            rings = true,
            days = period,
            onSuccess = { days ->
                viewModel.updateState(state.copy(
                    id = userId,
                    period = period,
                    showLoader = false,
                    days = days.map { day -> Pair(day, true) }
                ))
                onReloadSuccess()
            },
            onAuthFailure = { message ->
                viewModel.updateShowLoader(false)
                onAuthFailure(message)
            },
            onFailure = { e ->
                viewModel.updateShowLoader(false)
                onFailure(e)
            }
        )
    }

    if (state.id != userId) {
        onReloadRequest(userId)

        var date = LocalDate.now()
        date = date.minusDays(date.dayOfWeek.value - 1L)

        updateJournal(DateInterval(
            date.toEpochDay(),
            date.plusDays(6).toEpochDay()
        ))
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        HorizontalEntrySlider(
                            previous = { true },
                            entry = {
                                Text(
                                    text = localizedWeek(state.period.fromDate),
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            },
                            next = { true },
                            onEntryChanged = { offset ->
                                updateJournal(offsetWeek(state.period, offset))
                            }
                        )
                    },
                    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                )

                if (state.showLoader) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(scrollState)) {
            state.days.forEachIndexed { index, pair ->
                DiaryDay(
                    day = pair.first,
                    showRings = showRings,
                    extended = pair.second,
                    onHomework = { hwIndex ->
                        onHomework(pair.first, hwIndex)
                    },
                    onFold = {
                        val mutable = state.days.toMutableList()
                        mutable[index] = Pair(pair.first, pair.second.not())

                        viewModel.updateDays(days = mutable)
                    }
                )
            }
        }
    }
}

private fun offsetWeek(
    interval: DateInterval,
    offset: Int
): DateInterval {
    val delta = interval.toDate - interval.fromDate

    val withOffset = LocalDate.ofEpochDay(interval.fromDate)
        .plusWeeks(offset.toLong())
        .toEpochDay()

    return DateInterval(withOffset, withOffset + delta)
}

@Composable
private fun localizedWeek(
    timestamp: Long,
    offset: Int = 0
) : String {
    if (timestamp == 0L) {
        return stringResource(R.string.journal_schedule_required)
    }

    val calendar = GregorianCalendar.getInstance()
    calendar.time = Date(timestamp * 3600 * 1000 * 24 + (offset * 7 * 24 * 3600))
    val week = calendar.get(GregorianCalendar.WEEK_OF_YEAR)
    val year = calendar.get(GregorianCalendar.YEAR)

    val currentCalendar = GregorianCalendar.getInstance()
    val currentWeek = currentCalendar.get(GregorianCalendar.WEEK_OF_YEAR)
    val currentYear = currentCalendar.get(GregorianCalendar.YEAR)

    return if (week == currentWeek && year == currentYear) {
        stringResource(R.string.journal_current_week)
    } else if (week == currentWeek - 1 && year == currentYear) {
        stringResource(R.string.journal_previous_week)
    } else if (week == currentWeek + 1 && year == currentYear) {
        stringResource(R.string.journal_next_week)
    } else if (year == currentYear) {
        stringResource(R.string.journal_some_week, week)
    } else {
        stringResource(R.string.journal_another_year_week, week, year)
    }
}
