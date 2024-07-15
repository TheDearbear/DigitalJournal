package com.thedearbear.nnov.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thedearbear.nnov.Singleton
import com.thedearbear.nnov.api.ApiConstants
import com.thedearbear.nnov.api.responses.ReceivedMessagesResponse
import com.thedearbear.nnov.api.responses.SentMessagesResponse
import com.thedearbear.nnov.home.MailBoxMessage
import com.thedearbear.nnov.ui.state.MailBoxState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MailBoxViewModel : ViewModel() {
    private val _state = MutableStateFlow(MailBoxState())
    val state = _state.asStateFlow()

    private fun currentState(): MailBoxState {
        return state.value
    }

    private fun updateState(state: MailBoxState) {
        _state.value = state
    }

    fun updateLastInboxId(id: Int) {
        val state = currentState()
        updateState(state.copy(lastInboxId = id))
    }

    fun updateInbox(inbox: List<MailBoxMessage>) {
        val state = currentState()
        updateState(state.copy(inbox = inbox))
    }

    fun updateLastSentId(id: Int) {
        val state = currentState()
        updateState(state.copy(lastSentId = id))
    }

    fun updateSent(sent: List<MailBoxMessage>) {
        val state = currentState()
        updateState(state.copy(sent = sent))
    }

    fun updateShowLoader(showLoader: Boolean) {
        val state = currentState()
        updateState(state.copy(showLoader = showLoader))
    }

    fun fetchMessages(
        isInbox: Boolean,
        onSuccess: () -> Unit,
        onAuthFailure: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val client = Singleton.selectedAccount.client

            if (isInbox) {
                client.runRequest<ReceivedMessagesResponse>(
                    call = client.getMessages("inbox"),
                    onSuccess = { messages ->
                        val parsedMessages =
                            if (messages.messages == null) listOf()
                            else messages.messages.map { message ->
                                MailBoxMessage(
                                    id = message.id.toInt(),
                                    title = message.subject,
                                    body = message.shortText.replace("<br />", ""),
                                    author = "${message.userFrom.firstname} ${message.userFrom.lastname}",
                                    receivers = listOf(),
                                    date = ZonedDateTime.parse(
                                        "${message.date} Europe/Moscow",
                                        DateTimeFormatter.ofPattern(ApiConstants.DATETIME_PATTERN)
                                    ).toLocalDateTime()
                                )
                            }

                        updateInbox(parsedMessages)

                        onSuccess()
                    },
                    onAuthFailure = onAuthFailure,
                    onFailure = onFailure
                )
            } else {
                client.runRequest<SentMessagesResponse>(
                    call = client.getMessages("sent"),
                    onSuccess = { messages ->
                        val parsedMessages =
                            if (messages.messages == null) listOf()
                            else messages.messages.map { message ->
                                MailBoxMessage(
                                    id = message.id.toInt(),
                                    title = message.subject,
                                    body = message.shortText,
                                    author = "",
                                    receivers = message.usersTo.map { user -> "${user.firstname} ${user.lastname}" },
                                    date = ZonedDateTime.parse(
                                        "${message.date} Europe/Moscow",
                                        DateTimeFormatter.ofPattern(ApiConstants.DATETIME_PATTERN)
                                    ).toLocalDateTime()
                                )
                            }

                        updateSent(parsedMessages)

                        onSuccess()
                    },
                    onAuthFailure = onAuthFailure,
                    onFailure = onFailure
                )
            }
        }
    }
}