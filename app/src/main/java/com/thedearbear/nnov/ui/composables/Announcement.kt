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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import com.thedearbear.nnov.home.Announcement
import com.thedearbear.nnov.utils.materialTextLinkStyles
import com.thedearbear.nnov.utils.toDisplayString

@Composable
fun Announcement(
    modifier: Modifier = Modifier,
    showTitle: Boolean = true,
    showBody: Boolean = true,
    announcement: Announcement
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
                    Text(
                        text = announcement.author,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = announcement.date.toDisplayString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            if (showTitle && announcement.title.isNotEmpty()) {
                Text(
                    text = announcement.title,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (showBody && announcement.body.isNotEmpty()) {
                Text(
                    text = AnnotatedString.fromHtml(
                        htmlString = announcement.body,
                        linkStyles = materialTextLinkStyles()
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}