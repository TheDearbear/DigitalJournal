package com.thedearbear.nnov.tabs

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thedearbear.nnov.R
import com.thedearbear.nnov.ui.composables.MailBoxMessage
import com.thedearbear.nnov.ui.viewmodel.MailBoxMessageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MailBoxMessageInfo(
    messageId: Int,
    onBack: () -> Unit,
    onAuthFailure: (String) -> Unit,
    onFailure: (Exception) -> Unit,
    viewModel: MailBoxMessageViewModel
) {
    val state by viewModel.state.collectAsState()

    if (messageId != state.lastMessageId) {
        viewModel.updateShowLoader(true)

        viewModel.fetchMessage(
            id = messageId,
            onSuccess = {
                viewModel.updateShowLoader(false)
                viewModel.updateLastMessageId(messageId)
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

    val message = state.message

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            message?.title ?: stringResource(R.string.home_messages_no_message)
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

        if (message != null) {
            MailBoxMessage(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(padding)
                    .padding(12.dp),
                showTitle = false,
                message = message
            )
        }
    }
}