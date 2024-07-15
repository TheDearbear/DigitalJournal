package com.thedearbear.nnov.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.thedearbear.nnov.R
import com.thedearbear.nnov.ui.viewmodel.AppSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationSettings(
    onBack: () -> Unit,
    //viewModel: AppSettingsViewModel
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.account_app_settings))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Default.KeyboardArrowLeft, "Go Back")
                    }
                }
            )
        }
    ) { padding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(scrollState)
                .fillMaxWidth()
        ) {
            var option1 by remember { mutableStateOf(false) }
            Row {
                Text("Show Lessons on Vacation")

                Spacer(Modifier.weight(1f))

                Switch(
                    checked = option1,
                    onCheckedChange = { option1 = option1.not() }
                )
            }

            var option2 by remember { mutableStateOf(true) }
            Row {
                Text("Show Start/End of Lesson")

                Spacer(Modifier.weight(1f))

                Switch(
                    checked = option2,
                    onCheckedChange = { option2 = option2.not() }
                )
            }

            var option3 by remember { mutableStateOf(true) }
            Row {
                Text("Show Preview Text")

                Spacer(Modifier.weight(1f))

                Switch(
                    checked = option3,
                    onCheckedChange = { option3 = option3.not() }
                )
            }
        }
    }
}