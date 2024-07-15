package com.thedearbear.nnov.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.thedearbear.nnov.R
import com.thedearbear.nnov.ui.composables.MailBoxMessage
import com.thedearbear.nnov.ui.viewmodel.MailBoxViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MailBoxMessages(
    userId: Int,
    isInbox: Boolean,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    onAuthFailure: (String) -> Unit,
    onFailure: (Exception) -> Unit,
    viewModel: MailBoxViewModel
) {
    val state by viewModel.state.collectAsState()

    if (
        (isInbox && userId != state.lastInboxId) ||
        (isInbox.not() && userId != state.lastSentId)
    ) {
        viewModel.updateShowLoader(true)

        viewModel.fetchMessages(
            isInbox = isInbox,
            onSuccess = {
                viewModel.updateShowLoader(false)

                if (isInbox) {
                    viewModel.updateLastInboxId(userId)
                } else {
                    viewModel.updateLastSentId(userId)
                }
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

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(
                                if (isInbox) R.string.home_messages_received
                                else R.string.home_messages_sent
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Default.KeyboardArrowLeft, "Go Back")
                        }
                    }
                )

                if (state.showLoader) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    ) { padding ->
        val scrollState = rememberScrollState()

        val messages =
            if (isInbox) state.inbox
            else state.sent

        if (messages.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(
                        if (isInbox) R.string.home_messages_no_received
                        else R.string.home_messages_no_sent
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            val routeMessage = stringResource(R.string.nav_route_home_message)
            messages.forEach { message ->
                MailBoxMessage(
                    modifier = Modifier
                        .padding(
                            start = 12.dp,
                            end = 12.dp,
                            top = 12.dp
                        )
                        .clickable {
                            onNavigate(routeMessage.replace("{id}", message.id.toString()))
                        },
                    message = message
                )
            }
        }
    }
}