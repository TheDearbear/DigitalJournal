package com.thedearbear.nnov.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.thedearbear.nnov.R
import com.thedearbear.nnov.home.MailBoxMessage
import com.thedearbear.nnov.utils.toDisplayString

@Composable
fun MailBoxMessage(
    modifier: Modifier = Modifier,
    showTitle: Boolean = true,
    showBody: Boolean = true,
    message: MailBoxMessage
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.AccountCircle, null)

                Spacer(Modifier.width(6.dp))

                Column {
                    Row {
                        val receivers = message.receivers.joinToString(", ")

                        if (message.author.isNotEmpty() && message.receivers.isNotEmpty()) {
                            Text(
                                text = message.author,
                                style = MaterialTheme.typography.labelSmall
                            )

                            Icon(painterResource(R.drawable.arrow_forward), "Sent to")

                            Text(
                                text = receivers,
                                style = MaterialTheme.typography.labelSmall
                            )
                        } else {
                            Text(
                                text = message.author.ifEmpty { receivers },
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    Text(
                        text = message.date.toDisplayString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            if (showTitle && message.title.isNotEmpty()) {
                Text(
                    text = message.title,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (showBody && message.body.isNotEmpty()) {
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}