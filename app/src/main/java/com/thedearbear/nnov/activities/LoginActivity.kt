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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.zIndex
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.thedearbear.nnov.R
import com.thedearbear.nnov.api.ApiConstants
import com.thedearbear.nnov.ui.composables.AuthDialog
import com.thedearbear.nnov.ui.composables.VendorDialog
import com.thedearbear.nnov.ui.state.LoginState
import com.thedearbear.nnov.ui.theme.DigitalJournalTheme
import com.thedearbear.nnov.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class LoginActivity : ComponentActivity() {
    private var validateCredentials: Boolean = false
    private val viewModel: LoginViewModel by viewModels()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            validateCredentials = savedInstanceState.getBoolean("validate", false)
        }

        setContent {
            var openLoaderDialog by remember { mutableStateOf(false) }
            var openVendorDialog by remember { mutableStateOf(false) }
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            val state by viewModel.state.collectAsState()
            val formWidth = 350.dp

            val adaptiveInfo = currentWindowAdaptiveInfo()
            val isCompact = adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT ||
                    adaptiveInfo.windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT

            if (isCompact) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
            }

            DigitalJournalTheme {
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier.zIndex(2f)
                        )
                    }
                ) { padding ->
                    when {
                        openVendorDialog -> {
                            VendorDialog { openVendorDialog = false }
                        }

                        openLoaderDialog -> {
                            AuthDialog(state.state) { openLoaderDialog = false }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(padding)
                            .padding(12.dp)
                            .fillMaxSize()
                            .zIndex(1f),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = { viewModel.switchLoginMethod() }) {
                            Text(stringResource(R.string.login_switch_method))
                        }

                        Button(
                            modifier = Modifier.padding(start = 12.dp),
                            onClick = {
                                if (state.useToken) {
                                    performAuth(
                                        state = state,
                                        scope = scope,
                                        snackbarHostState = snackbarHostState,
                                        showDialog = { openLoaderDialog = true },
                                        hideDialog = { openLoaderDialog = false }
                                    )
                                } else {
                                    performLogin(
                                        state = state,
                                        scope = scope,
                                        snackbarHostState = snackbarHostState,
                                        showDialog = { openLoaderDialog = true },
                                        hideDialog = { openLoaderDialog = false }
                                    )
                                }
                            }
                        ) {
                            Text(stringResource(R.string.login_login_button))
                        }
                    }

                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.app_name),
                            fontSize = 8.em,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 1.em,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        if (state.useToken) {
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
                        } else {
                            OutlinedTextField(
                                value = state.login,
                                onValueChange = { viewModel.updateLogin(it) },
                                label = { Text(stringResource(R.string.login_login)) },
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
                                value = state.password,
                                onValueChange = { viewModel.updatePassword(it) },
                                singleLine = true,
                                label = { Text(stringResource(R.string.login_password)) },
                                modifier = Modifier
                                    .padding(bottom = 3.dp)
                                    .requiredWidth(formWidth),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

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
                                    onClick = { openVendorDialog = true },
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

    private fun performAuth(
        state: LoginState,
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        showDialog: () -> Unit,
        hideDialog: () -> Unit
    ) {
        updateState(state)
        showDialog()

        viewModel.auth(
            onSuccess = { title ->
                hideDialog()

                val result = Intent()
                    .putExtra("useToken", state.useToken)
                    .putExtra("title", title)
                    .putExtra("server", state.server)
                    .putExtra("devKey", state.devKey)
                    .putExtra("token", state.token)
                    .putExtra("vendor", state.vendor.ifEmpty {
                        ApiConstants.DEFAULT_VENDOR
                    })

                setResult(RESULT_OK, result)
                finish()
            },
            onAuthFailure = { message ->
                onAuthFailure(message, scope, snackbarHostState, hideDialog)
            },
            onFailure = { e ->
                onFailure(e, scope, snackbarHostState, hideDialog)
            }
        )
    }

    private fun performLogin(
        state: LoginState,
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        showDialog: () -> Unit,
        hideDialog: () -> Unit
    ) {
        updateState(state)
        showDialog()

        viewModel.login(
            onSuccess = { title, token, expires ->
                hideDialog()

                state.useToken = true
                state.token = token

                val result = Intent()
                    .putExtra("useToken", state.useToken)
                    .putExtra("title", title)
                    .putExtra("server", state.server)
                    .putExtra("devKey", state.devKey)
                    .putExtra("token", state.token)
                    .putExtra("expire", ZonedDateTime.parse(
                        "$expires Europe/Moscow",
                        DateTimeFormatter.ofPattern(ApiConstants.DATETIME_PATTERN + " z"))
                    )
                    .putExtra("vendor", state.vendor.ifEmpty {
                        ApiConstants.DEFAULT_VENDOR
                    })

                setResult(RESULT_OK, result)
                finish()
            },
            onAuthFailure = { message ->
                onAuthFailure(message, scope, snackbarHostState, hideDialog)
            },
            onFailure = { e ->
                onFailure(e, scope, snackbarHostState, hideDialog)
            }
        )
    }

    private fun onAuthFailure(
        message: String,
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        hideDialog: () -> Unit
    ) {
        hideDialog()

        scope.launch {
            snackbarHostState.showSnackbar(
                if (message.contains("разраб"))
                    getString(R.string.login_error_devkey)
                else if (message.contains("авторизация"))
                    getString(R.string.login_error_token)
                else if (message.contains("логин"))
                    getString(R.string.login_error_login)
                else if (message.contains("ГосУслуги"))
                    getString(R.string.login_error_gosuslugi)
                else message
            )
        }
    }

    private fun onFailure(
        e: Exception,
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        hideDialog: () -> Unit
    ) {
        hideDialog()

        scope.launch {
            snackbarHostState.showSnackbar(
                if (e is UnknownHostException)
                    getString(R.string.login_error_domain)
                else
                    "${getString(R.string.login_error_network)}: ${e.message}"
            )
        }
    }

    private fun updateState(state: LoginState) {
        viewModel.updateState(getString(
            if (state.useToken) R.string.dialog_authorization
            else R.string.dialog_authentication
        ))
    }
}
