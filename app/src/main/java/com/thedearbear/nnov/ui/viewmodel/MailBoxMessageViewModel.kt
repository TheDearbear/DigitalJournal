package com.thedearbear.nnov.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thedearbear.nnov.Singleton
import com.thedearbear.nnov.api.ApiConstants
import com.thedearbear.nnov.api.responses.MessageResponse
import com.thedearbear.nnov.home.MailBoxMessage
import com.thedearbear.nnov.ui.state.MailBoxMessageState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MailBoxMessageViewModel : ViewModel() {
    private val _state = MutableStateFlow(MailBoxMessageState())
    val state = _state.asStateFlow()

    private fun currentState(): MailBoxMessageState {
        return state.value
    }

    private fun updateState(state: MailBoxMessageState) {
        _state.value = state
    }

    fun updateLastMessageId(lastMessageId: Int) {
        val state = currentState()
        updateState(state.copy(lastMessageId = lastMessageId))
    }

    fun updateMessage(message: MailBoxMessage) {
        val state = currentState()
        updateState(state.copy(message = message))
    }

    fun updateShowLoader(showLoader: Boolean) {
        val state = currentState()
        updateState(state.copy(showLoader = showLoader))
    }

    fun fetchMessage(
        id: Int,
        onSuccess: () -> Unit,
        onAuthFailure: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val client = Singleton.selectedAccount.client

            client.runRequest<MessageResponse>(
                call = client.getMessageInfo(id),
                onSuccess = { response ->
                    val message = response.message

                    updateMessage(MailBoxMessage(
                        id = message.id.toInt(),
                        title = message.subject,
                        body = message.text.replace("<br />", ""),
                        author = "${message.userFrom.firstname} ${message.userFrom.lastname}",
                        receivers = message.userTo.map { user -> "${user.firstname} ${user.lastname}" },
                        date = ZonedDateTime.parse(
                            "${message.date} Europe/Moscow",
                            DateTimeFormatter.ofPattern(ApiConstants.DATETIME_PATTERN)
                        ).toLocalDateTime()
                    ))

                    onSuccess()
                },
                onAuthFailure = onAuthFailure,
                onFailure = onFailure
            )
        }
    }
}