package com.thedearbear.nnov.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thedearbear.nnov.R
import com.thedearbear.nnov.journal.HomeworkFile
import com.thedearbear.nnov.journal.Lesson

@Composable
fun HomeworkSheet(
    lesson: Lesson,
    onSelect: (Int) -> Unit,
    onHomeworkOpen: (HomeworkFile) -> Unit,
    onHomeworkDownload: (HomeworkFile) -> Unit
) {
    Column(
        Modifier.padding(4.dp)
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = lesson.name,
            style = MaterialTheme.typography.headlineMedium
        )

        if (lesson.homework.isNotEmpty()) {
            HorizontalDivider()
        }

        lesson.homework.forEachIndexed { index, homework ->
            val anyFiles = homework.files.isNotEmpty()
            var hwCardClickable: (() -> Unit)? = null

            if (!anyFiles) {
                hwCardClickable = {
                    onSelect(index)
                }
            }

            HomeworkCard(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                enableOnClick = !anyFiles,
                onClick = hwCardClickable
            ) {
                Column(
                    Modifier.padding(12.dp)
                ) {
                    var hwTextModifier = Modifier.fillMaxWidth()

                    if (anyFiles) {
                        hwTextModifier = hwTextModifier.clickable {
                            onSelect(index)
                        }
                    }

                    Text(
                        text = homework.message,
                        modifier = hwTextModifier,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    homework.files.forEach { file ->
                        HorizontalDivider(Modifier.padding(4.dp))

                        ListItem(
                            headlineContent = {
                                Text(file.name)
                            },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(R.drawable.description),
                                    contentDescription = null
                                )
                            },
                            trailingContent = {
                                Row {
                                    IconButton(onClick = { onHomeworkOpen(file) }) {
                                        Icon(
                                            painter = painterResource(R.drawable.open_in_new),
                                            contentDescription = stringResource(R.string.journal_open)
                                        )
                                    }

                                    IconButton(onClick = { onHomeworkDownload(file) }) {
                                        Icon(
                                            painter = painterResource(R.drawable.download),
                                            contentDescription = stringResource(R.string.journal_download)
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeworkCard(
    modifier: Modifier = Modifier,
    enableOnClick: Boolean,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if (enableOnClick) {
        OutlinedCard(
            modifier = modifier,
            onClick = {
                if (onClick != null) {
                    onClick()
                }
            }
        ) {
            content()
        }
    } else {
        OutlinedCard(
            modifier = modifier
        ) {
            content()
        }
    }
}
