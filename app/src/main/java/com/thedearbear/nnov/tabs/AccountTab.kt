package com.thedearbear.nnov.tabs

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thedearbear.nnov.R
import com.thedearbear.nnov.activities.AccountSelectActivity
import com.thedearbear.nnov.ui.viewmodel.MainViewModel

@Composable
fun AccountTab(
    viewModel: MainViewModel,
    onNavigate: (String) -> Unit,
    onDialogRequest: (@Composable () -> Unit) -> Unit,
    onDialogCancel: () -> Unit,
    getNewAccountId: ActivityResultLauncher<Intent>
) {
    val state by viewModel.state.collectAsState()

    @Composable
    fun CardEntry(key: String, value: String) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$key: ",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    Column {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(Modifier.padding(8.dp)) {
                CardEntry(
                    key = stringResource(R.string.account_card_name),
                    value = state.name
                )
                CardEntry(
                    key = stringResource(R.string.ext_info_email),
                    value = state.email
                )
                CardEntry(
                    key = stringResource(R.string.account_card_school),
                    value = state.school
                )
                if (state.teacher.isNotEmpty()) {
                    CardEntry(
                        key = stringResource(R.string.account_card_home_teacher),
                        value = state.teacher
                    )
                }
                if (state.grade.isNotEmpty()) {
                    CardEntry(
                        key = stringResource(R.string.account_card_grade),
                        value = state.grade
                    )
                }
            }
        }

        val mContext = LocalContext.current

        ListItem(
            modifier = Modifier.clickable {
                val intent = Intent(mContext, AccountSelectActivity::class.java)
                    .putExtra("doReturn", true)

                getNewAccountId.launch(intent)
            },
            headlineContent = { Text(stringResource(R.string.account_switch_account)) },
            leadingContent = { Icon(Icons.Default.AccountBox, null) }
        )

        // TODO: Settings
        /*HorizontalDivider()

        ListItem(
            modifier = Modifier.clickable { /* TODO */ },
            headlineContent = { Text("Account Settings") },
            leadingContent = { Icon(Icons.Default.Settings, null) }
        )

        HorizontalDivider()

        val routeAppSettings = stringResource(R.string.nav_route_account_app_settings)
        ListItem(
            modifier = Modifier.clickable { onNavigate(routeAppSettings) },
            headlineContent = { Text(stringResource(R.string.account_app_settings)) },
            leadingContent = { Icon(Icons.Default.Settings, null) }
        )*/

        HorizontalDivider()

        val routeExtendedInformation = stringResource(R.string.nav_route_account_extended_infomation)
        ListItem(
            modifier = Modifier.clickable { onNavigate(routeExtendedInformation) },
            headlineContent = { Text(stringResource(R.string.account_extended_information)) },
            leadingContent = { Icon(Icons.Default.Warning, null) }
        )

        HorizontalDivider()

        ListItem(
            modifier = Modifier.clickable {
                onDialogRequest {
                    AlertDialog(
                        onDismissRequest = onDialogCancel,
                        confirmButton = {
                            TextButton(onClick = onDialogCancel) {
                                Text(stringResource(R.string.button_close))
                            }
                        },
                        icon = {
                            Icon(Icons.Default.Info, null)
                        },
                        title = {
                            Text(stringResource(R.string.app_name))
                        },
                        text = {
                            Text(stringResource(R.string.account_about_body))
                        }
                    )
                }
            },
            headlineContent = { Text(stringResource(R.string.account_about)) },
            leadingContent = { Icon(Icons.Default.Info, null) }
        )
    }
}
