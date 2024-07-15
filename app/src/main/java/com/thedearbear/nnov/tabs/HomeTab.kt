package com.thedearbear.nnov.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thedearbear.nnov.R
import com.thedearbear.nnov.ui.composables.Announcement
import com.thedearbear.nnov.ui.viewmodel.HomeViewModel

@Composable
fun HomeTab(
    userId: Int,
    viewModel: HomeViewModel,
    onNavigate: (String) -> Unit,
    onSuccess: () -> Unit,
    onAuthFailure: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val state by viewModel.state.collectAsState()

    if (userId != state.id) {
        viewModel.updateShowLoader(true)

        viewModel.fetchAnnouncements(
            onSuccess = {
                onSuccess()
                viewModel.updateId(userId)
                viewModel.updateShowLoader(false)
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

    // TODO: Implement writing messages
    Scaffold(
        /*floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(stringResource(R.string.floating_write_message))
                },
                icon = {
                    Icon(Icons.Default.Create, null)
                },
                onClick = { /*TODO*/ }
            )
        }*/
    ) { innerPadding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            val routeMessages = stringResource(R.string.nav_route_home_messages)

            ListItem(
                modifier = Modifier.clickable {
                    onNavigate(routeMessages.replace("{type}", "sent"))
                },
                headlineContent = {
                    Text(stringResource(R.string.home_messages_sent))
                },
                leadingContent = {
                    Icon(Icons.AutoMirrored.Default.Send, null)
                }
            )

            HorizontalDivider(Modifier.padding(horizontal = 4.dp))

            ListItem(
                modifier = Modifier.clickable {
                    onNavigate(routeMessages.replace("{type}", "inbox"))
                },
                headlineContent = {
                    Text(stringResource(R.string.home_messages_received))
                },
                leadingContent = {
                    Icon(Icons.AutoMirrored.Default.Send, null)
                }
            )

            HorizontalDivider(Modifier.padding(horizontal = 4.dp))

            if (state.showLoader) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(6.dp))

            val routeAnnouncement = stringResource(R.string.nav_route_home_announcement)
            state.announcements.forEach { announcement ->
                Announcement(
                    modifier = Modifier
                        .clickable {
                            onNavigate(
                                routeAnnouncement.replace(
                                    "{id}",
                                    announcement.id.toString()
                                )
                            )
                        }
                        .padding(
                            vertical = 6.dp,
                            horizontal = 12.dp
                        )
                        .fillMaxWidth(),
                    announcement = announcement
                )
            }
        }
    }
}