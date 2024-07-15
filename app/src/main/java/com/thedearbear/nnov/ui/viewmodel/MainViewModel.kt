package com.thedearbear.nnov.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thedearbear.nnov.Singleton
import com.thedearbear.nnov.account.LocalAccountClient
import com.thedearbear.nnov.api.responses.RulesResponse
import com.thedearbear.nnov.repositories.LocalAccountRepository
import com.thedearbear.nnov.ui.state.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val handle: SavedStateHandle,
    val repository: LocalAccountRepository
) : ViewModel() {
    private var _state = MutableStateFlow(MainState())
    private var _rules = MutableStateFlow(RulesResponse())

    val state = _state.asStateFlow()
    val rules = _rules.asStateFlow()

    private fun currentState(): MainState {
        return state.value
    }

    private fun updateState(state: MainState) {
        _state.value = state
    }

    fun updateDialog(dialog: @Composable (() -> Unit)?) {
        val state = currentState()
        updateState(state.copy(dialog = dialog))
    }

    fun fetchAccountInfo(
        id: Int,
        onSuccess: () -> Unit,
        onAuthFailure: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            val localAccount = repository.findById(id) ?: return@launch
            Singleton.selectedAccount = LocalAccountClient(localAccount)

            val client = Singleton.selectedAccount.client

            client.runRequest<RulesResponse>(
                call = client.getRules(),
                onSuccess = { rules ->
                    val state = currentState()

                    _rules.value = rules

                    val school = rules.relations.schools.firstOrNull()
                    val student = rules.relations.students[rules.id]

                    updateState(state.copy(
                        id = Singleton.selectedAccount.account.id,
                        name = rules.title,
                        email = rules.email,
                        school = school?.title ?: "",
                        teacher = if (student != null && student.clazz.isNotEmpty()) {
                            rules.relations.groups[student.clazz]?.homeTeacherName ?: ""
                        } else "",
                        grade = student?.clazz ?: ""
                    ))

                    onSuccess()
                },
                onAuthFailure = onAuthFailure,
                onFailure = onFailure
            )
        }
    }
}