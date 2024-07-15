package com.thedearbear.nnov.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.thedearbear.nnov.R
import com.thedearbear.nnov.Singleton
import com.thedearbear.nnov.account.LocalAccount
import com.thedearbear.nnov.account.LocalAccountClient
import com.thedearbear.nnov.ui.theme.DigitalJournalTheme
import com.thedearbear.nnov.ui.viewmodel.AccountSelectViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@AndroidEntryPoint
class AccountSelectActivity : ComponentActivity() {
    private val viewModel: AccountSelectViewModel by viewModels()

    private val addNewAccount = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != RESULT_OK || result.data == null) {
            return@registerForActivityResult
        }

        val data = result.data!!

        if (!data.getBooleanExtra("useToken", false)) {
            return@registerForActivityResult
        }

        viewModel.addNew(LocalAccount(
            token = data.getStringExtra("token")!!,
            expires = @Suppress("DEPRECATION")
                data.getSerializableExtra("expire") as? ZonedDateTime?,
            baseAddress = data.getStringExtra("server")!!,
            key = data.getStringExtra("devKey")!!,
            vendor = data.getStringExtra("vendor")!!,
            name = data.getStringExtra("title") ?: ""
        ))
    }

    private val editAccount = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != RESULT_OK || result.data == null) {
            return@registerForActivityResult
        }

        val data = result.data!!

        val id = data.getIntExtra("id", -1)
        if (id == -1) {
            return@registerForActivityResult
        }

        if (data.getBooleanExtra("delete", false)) {
            viewModel.delete(id)

            if (Singleton.selectedAccount.account.id < 0) {
                initAndStartMainActivity(null)
            }
        } else {
            viewModel.update(
                LocalAccount(
                    id = id,
                    token = data.getStringExtra("token")!!,
                    expires = (@Suppress("DEPRECATION")
                        data.getSerializableExtra("expire") as? LocalDateTime?)
                            ?.atZone(ZoneId.of("Europe/Moscow")),
                    baseAddress = data.getStringExtra("server")!!,
                    key = data.getStringExtra("devKey")!!,
                    vendor = data.getStringExtra("vendor")!!,
                    name = ""
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val doReturn = intent.getBooleanExtra("doReturn", false)

        if (savedInstanceState == null) {
            viewModel.load()

            if (!doReturn) {
                val lastUsedAccount = runBlocking {
                    viewModel.getLastUsedAccount()
                }

                if (lastUsedAccount >= 0) {
                    initAndStartMainActivity(lastUsedAccount)
                }
            }
        }

        setContent {
            DigitalJournalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {
                                    addNewAccount.launch(Intent(this, LoginActivity::class.java))
                                }
                            ) {
                                Icon(Icons.Default.Add, stringResource(R.string.floating_add_new_account))
                            }
                        }
                    ) { innerPadding ->
                        if (viewModel.state.isNotEmpty()) {
                            AccountsList(
                                padding = innerPadding,
                                onAccountClick = { account ->
                                    viewModel.updateLastUsedAccount(account.id)

                                    if (!doReturn) {
                                        initAndStartMainActivity(account.id)
                                    }

                                    val result = Intent()
                                        .putExtra("id", account.id)

                                    setResult(RESULT_OK, result)
                                    finish()
                                }
                            )
                        } else {
                            NoAccounts(
                                padding = innerPadding
                            )
                        }
                    }
                }
            }
        }
    }

    private fun initAndStartMainActivity(id: Int?) {
        if (id == null) {
            Singleton.selectedAccount = LocalAccountClient.default
        } else {
            val localAccount = viewModel.state.firstOrNull { account -> account.id == id }

            if (localAccount == null) {
                return
            }

            Singleton.selectedAccount = LocalAccountClient(localAccount)
        }

        val mainIntent = Intent(this@AccountSelectActivity, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(mainIntent)
        finish()
    }

    @Composable
    private fun AccountsList(padding: PaddingValues, onAccountClick: (LocalAccount) -> Unit) {
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            for (account: LocalAccount in viewModel.state) {
                ListItem(
                    modifier = Modifier.clickable {
                        onAccountClick(account)
                    },
                    headlineContent = {
                        Text(
                            text = account.name.ifEmpty { censorToken(account.token) },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    leadingContent = {
                        Icon(Icons.Default.AccountCircle, null)
                    },
                    trailingContent = {
                        IconButton(onClick = {
                            editAccount.launch(
                                Intent(this@AccountSelectActivity, AccountEditActivity::class.java)
                                    .putExtra("id", account.id)
                                    .putExtra("token", account.token)
                                    .putExtra("expire", account.expires)
                                    .putExtra("server", account.baseAddress)
                                    .putExtra("devKey", account.key)
                                    .putExtra("vendor", account.vendor)
                            )
                        }) {
                            Icon(Icons.Default.Edit, "Edit account")
                        }
                    }
                )

                HorizontalDivider(Modifier.padding(horizontal = 4.dp))
            }
        }
    }

    @Composable
    private fun NoAccounts(padding: PaddingValues) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Text(
                text = ":(",
                fontSize = 15.em,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Text(stringResource(R.string.account_select_no_accounts))

            Text(stringResource(R.string.account_select_no_accounts_try_add))
        }
    }

    private fun censorToken(token: String): String {
        val vendorIdPostfixSeparatorIndex = token.lastIndexOf("___")
        var mainPart = token
        var postfix = ""

        if (vendorIdPostfixSeparatorIndex != -1) {
            postfix = token.takeLast(token.length - vendorIdPostfixSeparatorIndex)
            mainPart = token.take(vendorIdPostfixSeparatorIndex)
        }

        val censored =
            if (mainPart.length < 13) mainPart
            else mainPart.take(5) + "***" + mainPart.takeLast(5)

        return censored + postfix
    }
}