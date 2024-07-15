package com.thedearbear.nnov.ui.viewmodel

import android.icu.util.DateInterval
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thedearbear.nnov.Singleton
import com.thedearbear.nnov.api.responses.DiaryResponse
import com.thedearbear.nnov.journal.DayEntry
import com.thedearbear.nnov.journal.DayType
import com.thedearbear.nnov.journal.Lesson
import com.thedearbear.nnov.ui.state.JournalState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URL
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class JournalViewModel : ViewModel() {
    private val _state = MutableStateFlow(JournalState())
    val state = _state.asStateFlow()

    private fun currentState(): JournalState {
        return state.value
    }

    fun updateState(state: JournalState) {
        _state.value = state
    }

    fun updateId(id: Int) {
        val state = currentState()
        updateState(state.copy(id = id))
    }

    fun updateShowLoader(showLoader: Boolean) {
        val state = currentState()
        updateState(state.copy(showLoader = showLoader))
    }

    fun updatePeriod(period: DateInterval) {
        val state = currentState()
        updateState(state.copy(period = period))
    }

    fun updateNewDays(days: List<DayEntry>) {
        val state = currentState()
        updateState(state.copy(days = days.map { day -> Pair(day, true) }))
    }

    fun updateDays(days: List<Pair<DayEntry, Boolean>>) {
        val state = currentState()
        updateState(state.copy(days = days))
    }

    fun fetchJournalInfo(
        id: Int,
        days: DateInterval? = null,
        rings: Boolean = false,
        onSuccess: (List<DayEntry>) -> Unit,
        onAuthFailure: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            val client = Singleton.selectedAccount.client

            client.runRequest<DiaryResponse>(
                call = client.getDiary(
                    id = id.toUInt(),
                    days = days,
                    rings = rings
                ),
                onSuccess = { diary ->
                    onSuccess(
                        if (diary.students.isEmpty()) listOf()
                        else {
                            val student = diary.students.values.first()

                            student.days.map { day ->
                                DayEntry(
                                    date = LocalDate.parse(day.key, DateTimeFormatter.BASIC_ISO_DATE),
                                    type = when (day.value.alert) {
                                        "vacation" -> DayType.VACATION
                                        "holiday" -> DayType.HOLIDAY
                                        else -> DayType.NORMAL
                                    },
                                    typeSpecific = day.value.holidayName,
                                    lessons = day.value.items.map { lesson ->
                                        Lesson(
                                            number = lesson.key.toInt(),
                                            name = lesson.value.name,
                                            homework = lesson.value.homework.map { homework ->
                                                homework.value.value
                                            },
                                            files = lesson.value.files.map { file ->
                                                URL(file.link)
                                            },
                                            marks = lesson.value.assessments?.map { mark ->
                                                mark.value
                                            } ?: listOf(),
                                            time = if (lesson.value.startTime == null || lesson.value.endTime == null) null
                                            else {
                                                Pair(
                                                    LocalTime.parse(lesson.value.startTime),
                                                    LocalTime.parse(lesson.value.endTime)
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    )
                },
                onAuthFailure = onAuthFailure,
                onFailure = onFailure
            )
        }
    }
}