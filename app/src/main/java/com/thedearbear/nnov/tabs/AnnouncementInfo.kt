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
import com.thedearbear.nnov.ui.composables.Announcement
import com.thedearbear.nnov.ui.viewmodel.AnnouncementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementInfo(
    announcementId: Int?,
    onBack: () -> Unit,
    onAuthFailure: (String) -> Unit,
    onFailure: (Exception) -> Unit,
    viewModel: AnnouncementViewModel
) {
    val state by viewModel.state.collectAsState()

    if (announcementId != state.announcement?.id) {
        viewModel.updateShowLoader(true)

        viewModel.fetchAnnouncement(
            id = announcementId,
            onSuccess = {
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

    val announcement = state.announcement

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            announcement?.title ?: stringResource(R.string.home_announcements_no_announcement)
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

        if (announcement != null) {
            Announcement(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(padding)
                    .padding(12.dp),
                showTitle = false,
                announcement = announcement
            )
        }
    }
}