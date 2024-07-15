package com.thedearbear.nnov.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.zIndex
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.thedearbear.nnov.R
import com.thedearbear.nnov.api.ApiConstants
import com.thedearbear.nnov.ui.composables.VendorDialog
import com.thedearbear.nnov.ui.theme.DigitalJournalTheme
import com.thedearbear.nnov.ui.viewmodel.AccountEditViewModel
import java.time.LocalDateTime

class AccountEditActivity : ComponentActivity() {
    private val viewModel: AccountEditViewModel by viewModels()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val vendor = intent.getStringExtra("vendor")

            viewModel.updateToken(intent.getStringExtra("token") ?: "")
            viewModel.updateExpire(
                @Suppress("DEPRECATION")
                intent.getSerializableExtra("expire") as? LocalDateTime?
            )

            viewModel.updateServer(intent.getStringExtra("server") ?: "")
            viewModel.updateDevKey(intent.getStringExtra("devKey") ?: "")
            viewModel.updateVendor(
                if (vendor != null && vendor != ApiConstants.DEFAULT_VENDOR) vendor
                else ""
            )
        }

        setContent {
            val openVendorDialog = remember { mutableStateOf(false) }
            val openDeleteDialog = remember { mutableStateOf(false) }
            val state by viewModel.state.collectAsState()
            val formWidth = 350.dp

            val adaptiveInfo = currentWindowAdaptiveInfo()
            val isCompact = adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT ||
                    adaptiveInfo.windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT

            if (isCompact) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
            }

            DigitalJournalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when {
                        openDeleteDialog.value -> {
                            DeleteDialog(
                                onConfirm = {
                                    val intent = Intent()
                                        .putExtra("id", intent?.getIntExtra("id", -1))
                                        .putExtra("delete", true)

                                    setResult(RESULT_OK, intent)
                                    finish()
                                },
                                onDismiss = {
                                    openDeleteDialog.value = false
                                }
                            )
                        }

                        openVendorDialog.value -> {
                            VendorDialog(onDismiss = { openVendorDialog.value = false })
                        }
                    }

                    Row(
                        modifier = Modifier.padding(12.dp).zIndex(1f),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = { openDeleteDialog.value = true }) {
                            Text(stringResource(R.string.account_edit_delete))
                        }

                        Button(
                            modifier = Modifier.padding(start = 12.dp),
                            onClick = {
                                val result = Intent()
                                    .putExtra("id", intent?.getIntExtra("id", -1))
                                    .putExtra("token", state.token)
                                    .putExtra("expire", state.expire)
                                    .putExtra("server", state.server)
                                    .putExtra("devKey", state.devKey)
                                    .putExtra("vendor", state.vendor.ifEmpty {
                                        ApiConstants.DEFAULT_VENDOR
                                    })

                                setResult(RESULT_OK, result)
                                finish()
                            }
                        ) {
                            Text(stringResource(R.string.account_edit_save))
                        }
                    }

                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier.verticalScroll(scrollState),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.account_edit_editing),
                            fontSize = 8.em,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 1.em,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        OutlinedTextField(
                            value = state.token,
                            onValueChange = { viewModel.updateToken(it) },
                            label = { Text(stringResource(R.string.login_token)) },
                            singleLine = true,
                            modifier = Modifier
                                .padding(bottom = 3.dp)
                                .requiredWidth(formWidth),
                            keyboardOptions = KeyboardOptions(
                                autoCorrectEnabled = false,
                                keyboardType = KeyboardType.Ascii,
                                imeAction = ImeAction.Next
                            )
                        )

                        HorizontalDivider(
                            Modifier
                                .padding(top = 5.dp)
                                .requiredWidth(formWidth)
                        )

                        OutlinedTextField(
                            value = state.server,
                            onValueChange = { viewModel.updateServer(it) },
                            label = { Text(stringResource(R.string.login_domain_or_ip)) },
                            singleLine = true,
                            modifier = Modifier
                                .padding(bottom = 3.dp)
                                .requiredWidth(formWidth),
                            keyboardOptions = KeyboardOptions(
                                autoCorrectEnabled = false,
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Next
                            )
                        )

                        OutlinedTextField(
                            value = state.devKey,
                            onValueChange = { viewModel.updateDevKey(it) },
                            label = { Text(stringResource(R.string.login_dev_key)) },
                            singleLine = true,
                            modifier = Modifier
                                .padding(bottom = 3.dp)
                                .requiredWidth(formWidth),
                            keyboardOptions = KeyboardOptions(
                                autoCorrectEnabled = false,
                                keyboardType = KeyboardType.Ascii,
                                imeAction = ImeAction.Next
                            )
                        )

                        OutlinedTextField(
                            value = state.vendor,
                            onValueChange = { viewModel.updateVendor(it) },
                            label = { Text(stringResource(R.string.login_vendor)) },
                            singleLine = true,
                            modifier = Modifier.requiredWidth(formWidth),
                            keyboardOptions = KeyboardOptions(
                                autoCorrectEnabled = false,
                                keyboardType = KeyboardType.Ascii,
                                imeAction = ImeAction.Done
                            ),
                            trailingIcon = {
                                IconButton(
                                    onClick = { openVendorDialog.value = true },
                                    modifier = Modifier.padding(start = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Info,
                                        contentDescription = "Vendor Field Description"
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun DeleteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            shape = RoundedCornerShape(16),
            icon = {
                Icon(Icons.Filled.Delete, null)
            },
            title = {
                Text(stringResource(R.string.account_edit_account_deletion))
            },
            text = {
                Text(stringResource(R.string.dialog_account_delete_sure))
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(stringResource(R.string.account_edit_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.button_cancel))
                }
            }
        )
    }
}
