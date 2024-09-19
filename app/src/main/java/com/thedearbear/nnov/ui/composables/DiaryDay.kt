package com.thedearbear.nnov.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thedearbear.nnov.R
import com.thedearbear.nnov.journal.DayEntry
import com.thedearbear.nnov.journal.DayType
import com.thedearbear.nnov.utils.toDisplayString

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiaryDay(
    day: DayEntry,
    showRings: Boolean = false,
    showLessonOnVacation: Boolean = true,
    extended: Boolean = false,
    onHomework: (Int) -> Unit,
    onFold: () -> Unit
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = day.date.toDisplayString(
                            hideYear = true
                        ),
                        onTextLayout = null,
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (day.type != DayType.NORMAL) {
                        Box(
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(percent = 16)
                                )
                        ) {
                            Text(
                                text =
                                if (day.type == DayType.VACATION)
                                    stringResource(R.string.journal_vacation)
                                else
                                    day.typeSpecific ?: stringResource(R.string.journal_holiday)
                                ,
                                modifier = Modifier.padding(6.dp),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                if (day.type == DayType.NORMAL || showLessonOnVacation) {
                    IconButton(onClick = onFold) {
                        if (extended) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = stringResource(R.string.journal_fold)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = stringResource(R.string.journal_unfold)
                            )
                        }
                    }
                }
            }

            if (extended && (day.type == DayType.NORMAL || showLessonOnVacation)) {
                HorizontalDivider()

                day.lessons.forEachIndexed { index, lesson ->
                    ListItem(
                        headlineContent = {
                            FlowRow(
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = lesson.name,
                                    style = MaterialTheme.typography.labelLarge
                                )

                                if (showRings && lesson.time != null) {
                                    Spacer(Modifier.padding(start = 6.dp))

                                    Text(
                                        text = "${lesson.time.first} - ${lesson.time.second}",
                                        lineHeight = MaterialTheme.typography.labelLarge.lineHeight,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        },
                        supportingContent = {
                            if (lesson.homework.isNotEmpty()) {
                                Text(
                                    text = stringResource(R.string.journal_show_homework),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.clickable {
                                        onHomework(index)
                                    }
                                )
                            }
                        },
                        leadingContent = {
                            Text(
                                text = lesson.number.toString(),
                                modifier = Modifier.padding(5.dp)
                            )
                        },
                        trailingContent = {
                            Row {
                                lesson.marks.forEachIndexed { index, mark ->
                                    Text(
                                        text = mark,
                                        modifier = Modifier
                                            .requiredSize(25.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                                shape = RoundedCornerShape(percent = 16)
                                            )
                                            .wrapContentSize()
                                    )

                                    if (index != lesson.marks.size - 1) {
                                        Spacer(Modifier.width(10.dp))
                                    }
                                }
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )

                    if (index != day.lessons.size - 1) {
                        HorizontalDivider()
                    }
                }

                if (day.lessons.isEmpty()) {
                    Text(
                        text = stringResource(R.string.journal_no_lessons),
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
