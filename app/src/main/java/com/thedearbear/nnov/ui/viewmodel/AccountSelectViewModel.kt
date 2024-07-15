package com.thedearbear.nnov.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thedearbear.nnov.DataStoreManager
import com.thedearbear.nnov.Singleton
import com.thedearbear.nnov.account.LocalAccount
import com.thedearbear.nnov.account.LocalAccountClient
import com.thedearbear.nnov.repositories.LocalAccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountSelectViewModel @Inject constructor(
    val handle: SavedStateHandle,
    val repository: LocalAccountRepository
) : ViewModel() {
    @Inject lateinit var dsManager: DataStoreManager

    val state = mutableStateListOf<LocalAccount>()

    fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            state.addAll(repository.getAll())
            dsManager.cache()
        }
    }

    fun updateLastUsedAccount(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dsManager.setLastUsedAccount(id)
        }
    }

    fun addNew(account: LocalAccount) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.add(account)

            state.clear()
            state.addAll(repository.getAll())
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(id)
            state.removeIf { account -> account.id == id }

            if (id == Singleton.selectedAccount.account.id) {
                Singleton.selectedAccount = LocalAccountClient.default
                updateLastUsedAccount(Singleton.selectedAccount.account.id)
            }
        }
    }

    fun update(account: LocalAccount) {
        viewModelScope.launch(Dispatchers.IO) {
            val new = repository.update(account)

            val index = state.indexOfFirst { stateAccount -> stateAccount.id == account.id }
            if (index != -1 && new != null) {
                state[index] = new
            }
        }
    }

    suspend fun getLastUsedAccount(): Int {
        return dsManager.lastUsedAccount.first()
    }
}