package com.thedearbear.nnov.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thedearbear.nnov.api.ApiClient
import com.thedearbear.nnov.api.ApiConstants
import com.thedearbear.nnov.api.responses.RulesResponse
import com.thedearbear.nnov.ui.state.LoginState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private var _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private fun currentState(): LoginState {
        return state.value
    }

    private fun updateState(state: LoginState) {
        _state.value = state
    }

    fun switchLoginMethod() {
        val state = currentState()
        updateState(state.copy(useToken = state.useToken.not()))
    }

    fun updateLogin(login: String) {
        val state = currentState()
        updateState(state.copy(login = login))
    }

    fun updatePassword(password: String) {
        val state = currentState()
        updateState(state.copy(password = password))
    }

    fun updateToken(token: String) {
        val state = currentState()
        updateState(state.copy(token = token))
    }

    fun updateServer(server: String) {
        val state = currentState()
        updateState(state.copy(server = server))
    }

    fun updateDevKey(devKey: String) {
        val state = currentState()
        updateState(state.copy(devKey = devKey))
    }

    fun updateVendor(vendor: String) {
        val state = currentState()
        updateState(state.copy(vendor = vendor))
    }

    fun updateState(authState: String) {
        val state = currentState()
        updateState(state.copy(state = authState))
    }

    fun auth(
        onSuccess: (String) -> Unit,
        onAuthFailure: (String) -> Unit,
        onFailure: (Exception) -> Unit,
        apiClient: ApiClient? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val client = apiClient ?: ApiClient(
                devKey = state.value.devKey,
                server = state.value.server,
                authToken = state.value.token,
                vendor = state.value.vendor.ifEmpty {
                    ApiConstants.DEFAULT_VENDOR
                }
            )

            client.runRequest<RulesResponse>(
                call = client.getRules(),
                onSuccess = { rules ->
                    var title = "${rules.lastname} ${rules.firstname}"

                    val school = rules.relations.schools.firstOrNull()
                    if (school != null) {
                        title += " | ${school.title}"
                    }

                    onSuccess(title)
                },
                onAuthFailure = onAuthFailure,
                onFailure = onFailure
            )
        }
    }

    fun login(
        onSuccess: (String, String, String) -> Unit,
        onAuthFailure: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val client = ApiClient(
            devKey = state.value.devKey,
            server = state.value.server,
            vendor = state.value.vendor.ifEmpty {
                ApiConstants.DEFAULT_VENDOR
            }
        )

        client.runRequest<Map<*, *>>(
            call = client.auth(state.value.login, state.value.password),
            onSuccess = { result ->
                val token = result["token"].toString()
                val expires = result["expires"].toString()
                client.authToken = token

                auth(
                    onSuccess = { title ->
                        onSuccess(title, token, expires)
                    },
                    onAuthFailure = onAuthFailure,
                    onFailure = onFailure,
                    apiClient = client
                )
            },
            onAuthFailure = onAuthFailure,
            onFailure = onFailure
        )
    }
}
