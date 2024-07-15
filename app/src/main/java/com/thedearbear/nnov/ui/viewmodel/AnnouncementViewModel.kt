package com.thedearbear.nnov.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thedearbear.nnov.Singleton
import com.thedearbear.nnov.api.responses.BoardNoticeResponse
import com.thedearbear.nnov.home.Announcement
import com.thedearbear.nnov.ui.state.AnnouncementState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AnnouncementViewModel : ViewModel() {
    private val _state = MutableStateFlow(AnnouncementState())
    val state = _state.asStateFlow()

    private fun currentState(): AnnouncementState {
        return state.value
    }

    private fun updateState(state: AnnouncementState) {
        _state.value = state
    }

    fun updateAnnouncement(announcement: Announcement?) {
        val state = currentState()
        updateState(state.copy(announcement = announcement))
    }

    fun updateShowLoader(showLoader: Boolean) {
        val state = currentState()
        updateState(state.copy(showLoader = showLoader))
    }

    fun fetchAnnouncement(
        id: Int?,
        onSuccess: () -> Unit,
        onAuthFailure: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (id == null) {
            updateAnnouncement(null)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val client = Singleton.selectedAccount.client

            client.runRequest<BoardNoticeResponse>(
                call = client.getBoardNoticeInfo(id),
                onSuccess = { response ->
                    val notice = response.notice

                    updateAnnouncement(
                        Announcement(
                        id = notice.id.toInt(),
                        title = notice.subject,
                        body = notice.text,
                        author = notice.userFromString,
                        date = LocalDate.parse(
                            notice.date,
                            DateTimeFormatter.ISO_LOCAL_DATE
                        )
                    ))

                    onSuccess()
                },
                onAuthFailure = onAuthFailure,
                onFailure = onFailure
            )
        }
    }
}