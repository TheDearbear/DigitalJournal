package com.thedearbear.nnov.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thedearbear.nnov.Singleton
import com.thedearbear.nnov.api.responses.BoardNoticesResponse
import com.thedearbear.nnov.home.Announcement
import com.thedearbear.nnov.ui.state.HomeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private fun currentState(): HomeState {
        return state.value
    }

    private fun updateState(state: HomeState) {
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

    fun updateAnnouncements(announcements: List<Announcement>) {
        val state = currentState()
        updateState(state.copy(announcements = announcements))
    }

    fun fetchAnnouncements(
        onSuccess: () -> Unit,
        onAuthFailure: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val client = Singleton.selectedAccount.client

            client.runRequest<BoardNoticesResponse>(
                call = client.getBoardNotices(),
                onSuccess = { notices ->
                    updateAnnouncements(
                        if (notices.notices == null) listOf()
                        else notices.notices.map { notice ->
                            Announcement(
                                id = notice.id.toInt(),
                                title = notice.subject,
                                body = notice.shortText,
                                author = notice.userFromString,
                                date = LocalDate.parse(
                                    notice.date,
                                    DateTimeFormatter.ISO_LOCAL_DATE
                                )
                            )
                        }
                    )

                    onSuccess()
                },
                onAuthFailure = onAuthFailure,
                onFailure = onFailure
            )
        }
    }
}