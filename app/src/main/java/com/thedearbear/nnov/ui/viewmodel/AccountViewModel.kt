package com.thedearbear.nnov.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.thedearbear.nnov.ui.state.AccountState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccountViewModel : ViewModel() {
    private val _state = MutableStateFlow(AccountState())
    val state = _state.asStateFlow()

    private fun currentState() : AccountState {
        return state.value
    }

    private fun updateState(state: AccountState) {
        _state.value = state
    }
}