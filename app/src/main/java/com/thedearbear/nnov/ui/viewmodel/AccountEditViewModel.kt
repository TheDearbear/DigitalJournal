package com.thedearbear.nnov.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.thedearbear.nnov.ui.state.AccountEditState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

class AccountEditViewModel : ViewModel() {
    private var _state = MutableStateFlow(AccountEditState())
    val state: StateFlow<AccountEditState> = _state.asStateFlow()

    private fun currentState(): AccountEditState {
        return state.value
    }

    private fun updateState(state: AccountEditState) {
        _state.value = state
    }

    fun updateToken(token: String) {
        val state = currentState()
        updateState(state.copy(token = token))
    }

    fun updateExpire(expire: LocalDateTime?) {
        val state = currentState()
        updateState(state.copy(expire = expire))
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
}