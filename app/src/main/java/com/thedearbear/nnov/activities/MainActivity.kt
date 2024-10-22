package com.thedearbear.nnov.activities

import android.app.DownloadManager
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.thedearbear.nnov.R
import com.thedearbear.nnov.Singleton
import com.thedearbear.nnov.tabs.AccountTab
import com.thedearbear.nnov.tabs.AnnouncementInfo
import com.thedearbear.nnov.tabs.ApplicationSettings
import com.thedearbear.nnov.tabs.ExtendedInformation
import com.thedearbear.nnov.tabs.HomeTab
import com.thedearbear.nnov.tabs.JournalTab
import com.thedearbear.nnov.tabs.MailBoxMessageInfo
import com.thedearbear.nnov.ui.composables.AccountLoadingDialog
import com.thedearbear.nnov.tabs.MailBoxMessages
import com.thedearbear.nnov.ui.composables.HomeworkSheet
import com.thedearbear.nnov.ui.state.MainState
import com.thedearbear.nnov.ui.theme.DigitalJournalTheme
import com.thedearbear.nnov.ui.viewmodel.AnnouncementViewModel
import com.thedearbear.nnov.ui.viewmodel.HomeViewModel
import com.thedearbear.nnov.ui.viewmodel.JournalViewModel
import com.thedearbear.nnov.ui.viewmodel.MailBoxMessageViewModel
import com.thedearbear.nnov.ui.viewmodel.MailBoxViewModel
import com.thedearbear.nnov.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.UnknownHostException

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    private var sheetContent: (@Composable () -> Unit)? = null

    private lateinit var newAccountSuccess: (Int) -> Unit
    private var accountLoading: Job? = null

    private val getNewAccountId = registerForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode != RESULT_OK) {
            return@registerForActivityResult
        }

        val data = result.data ?: return@registerForActivityResult
        val id = data.getIntExtra("id", -1)

        if (id == -1) {
            return@registerForActivityResult
        }

        newAccountSuccess(id)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Singleton.selectedAccount.isValid().not()) {
            startActivity(Intent(this, AccountSelectActivity::class.java))
            finish()
        }

        setContent {
            val state by viewModel.state.collectAsState()
            var openAccountLoadingDialog by remember { mutableStateOf(false) }
            var showSheet by remember { mutableStateOf(false) }
            val snackbarHostState = remember { SnackbarHostState() }
            val sheetState = rememberModalBottomSheetState()

            val scope = rememberCoroutineScope()
            val navController = rememberNavController(ComposeNavigator())
            val adaptiveInfo = currentWindowAdaptiveInfo()

            val isCompact = adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT ||
                    adaptiveInfo.windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT

            newAccountSuccess = { id ->
                accountLoading?.cancel()

                openAccountLoadingDialog = true

                accountLoading = viewModel.fetchAccountInfo(
                    id = id,
                    onSuccess = { openAccountLoadingDialog = false },
                    onAuthFailure = { message ->
                        openAccountLoadingDialog = false

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (message.contains("разраб"))
                                    getString(R.string.login_error_devkey)
                                else if (message.contains("авторизация"))
                                    getString(R.string.login_error_invalid)
                                else message
                            )
                        }
                    },
                    onFailure = { e ->
                        openAccountLoadingDialog = false

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (e is UnknownHostException)
                                    getString(R.string.login_error_domain)
                                else
                                    "${getString(R.string.login_error_network)}: ${e.message}"
                            )
                        }
                    }
                )
            }

            if (savedInstanceState == null) {
                newAccountSuccess(Singleton.selectedAccount.account.id)
            }

            DigitalJournalTheme {
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(snackbarHostState)
                    },
                    bottomBar = {
                        if (isCompact) {
                            NavBar(navController)
                        }
                    }
                ) { padding ->
                    when {
                        openAccountLoadingDialog -> {
                            AccountLoadingDialog {
                                accountLoading?.cancel()
                                accountLoading = null

                                openAccountLoadingDialog = false
                            }
                        }

                        state.dialog != null -> {
                            state.dialog?.let { it() }
                        }

                        showSheet -> {
                            ModalBottomSheet(
                                onDismissRequest = {
                                    sheetContent = null
                                    showSheet = false
                                },
                                sheetState = sheetState
                            ) {
                                sheetContent?.let { it() }
                            }
                        }
                    }

                    Row(
                        Modifier
                            .padding(padding)
                            .fillMaxSize()) {
                        if (isCompact.not()) {
                            NavRail(navController)
                        }

                        NavigationHost(
                            navController = navController,
                            state = state,
                            getNewAccountId = getNewAccountId,
                            requestSnackbar = { message, args ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = if (args.isNotEmpty()) {
                                            getString(message) + ": ${args[0]}"
                                        } else {
                                            getString(message)
                                        }
                                    )
                                }
                            },
                            requestSheet = { content ->
                                sheetContent = content
                                showSheet = content != null
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun NavigationHost(
        modifier: Modifier = Modifier,
        navController: NavHostController,
        state: MainState,
        getNewAccountId: ActivityResultLauncher<Intent>,
        requestSnackbar: (Int, Array<out Any>) -> Unit,
        requestSheet: ((@Composable () -> Unit)?) -> Unit
    ) {
        val navHome = stringResource(R.string.nav_route_home)
        val navHomeMessage = stringResource(R.string.nav_route_home_message)
        val navHomeMessages = stringResource(R.string.nav_route_home_messages)
        val navHomeAnnouncement = stringResource(R.string.nav_route_home_announcement)

        val navJournal = stringResource(R.string.nav_route_journal)

        val navAccount = stringResource(R.string.nav_route_account)
        val navAccountAppSettings = stringResource(R.string.nav_route_account_app_settings)
        val navAccountExtendedInformation = stringResource(R.string.nav_route_account_extended_infomation)

        NavHost(
            navController = navController,
            startDestination = navHome,
            modifier = modifier
        ) {
            composable(navHome) {
                val homeViewModel: HomeViewModel by viewModels()

                HomeTab(
                    userId = state.id,
                    viewModel = homeViewModel,
                    onNavigate = { route ->
                        navController.navigate(
                            route = route,
                            navOptions = NavOptions.Builder()
                                .setRestoreState(true)
                                .build()
                        )
                    },
                    onSuccess = { },
                    onAuthFailure = { message ->
                        requestSnackbar(R.string.login_error_invalid, arrayOf(message))
                    },
                    onFailure = { e ->
                        requestSnackbar(R.string.login_error_network, arrayOf(e))
                    }
                )
            }

            composable(navJournal) {
                val journalViewModel: JournalViewModel by viewModels()
                val clipboard = LocalClipboardManager.current
                val hwLocalized = stringResource(R.string.journal_homework)

                JournalTab(
                    userId = state.id,
                    viewModel = journalViewModel,
                    onHomework = { day, index ->
                        if (day.lessons.size > index) {
                            val lesson = day.lessons[index]

                            requestSheet {
                                HomeworkSheet(
                                    lesson = lesson,
                                    onSelect = { hwIndex ->
                                        val hwMessage = lesson.homework[hwIndex].message
                                        val clipData = ClipData.newPlainText(hwLocalized, hwMessage)

                                        clipboard.setClip(ClipEntry(clipData))
                                        requestSheet(null)
                                        requestSnackbar(R.string.journal_homework_copied, emptyArray())
                                    },
                                    onHomeworkOpen = { file ->
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(file.file.toString()))
                                        startActivity(intent)
                                    },
                                    onHomeworkDownload = { file ->
                                        val manager = getSystemService(DownloadManager::class.java)

                                        manager?.enqueue(DownloadManager.Request(
                                            Uri.parse(file.file.toString())
                                        ).setDestinationInExternalPublicDir(
                                            Environment.DIRECTORY_DOWNLOADS,
                                            file.name
                                        ).setTitle(
                                            file.name
                                        ).setNotificationVisibility(
                                            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                                        ).addRequestHeader(
                                            "User-Agent",
                                            // Using custom user-agent due to AndroidDownloadManager
                                            // being detected as crawler server-side
                                            Singleton.selectedAccount.client.userAgent
                                        ))
                                    }
                                )
                            }
                        }
                    },
                    onReloadRequest = { },
                    onReloadSuccess = { },
                    onAuthFailure = { message ->
                        requestSnackbar(R.string.login_error_invalid, arrayOf(message))
                    },
                    onFailure = { e ->
                        requestSnackbar(R.string.login_error_network, arrayOf(e))
                    }
                )
            }

            composable(navAccount) {
                val scrollState = rememberScrollState()

                Box(modifier.verticalScroll(scrollState)) {
                    AccountTab(
                        viewModel = viewModel,
                        onNavigate = { route ->
                            navController.navigate(
                                route = route,
                                navOptions = NavOptions.Builder()
                                    .setRestoreState(true)
                                    .build()
                            )
                        },
                        onDialogRequest = { dialog ->
                            viewModel.updateDialog(dialog)
                        },
                        onDialogCancel = {
                            viewModel.updateDialog(null)
                        },
                        getNewAccountId = getNewAccountId
                    )
                }
            }

            composable(navHomeMessage) {
                val id = (it.arguments?.getString("id") ?: "-1").toInt()
                val viewModel: MailBoxMessageViewModel by viewModels()

                MailBoxMessageInfo(
                    messageId = id,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onAuthFailure = { message ->
                        if (message.contains("доступ")) {
                            requestSnackbar(R.string.error_forbidden, arrayOf())
                        } else {
                            requestSnackbar(R.string.login_error_invalid, arrayOf(message))
                        }
                    },
                    onFailure = { e ->
                        requestSnackbar(R.string.login_error_network, arrayOf(e))
                    }
                )
            }

            composable(navHomeMessages) {
                val type = it.arguments?.getString("type") ?: "inbox"
                val viewModel: MailBoxViewModel by viewModels()

                MailBoxMessages(
                    userId = state.id,
                    isInbox = type == "inbox",
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigate = { route ->
                        navController.navigate(
                            route = route,
                            navOptions = NavOptions.Builder()
                                .setRestoreState(true)
                                .build()
                        )
                    },
                    onAuthFailure = { message ->
                        requestSnackbar(R.string.login_error_invalid, arrayOf(message))
                    },
                    onFailure = { e ->
                        requestSnackbar(R.string.login_error_network, arrayOf(e))
                    }
                )
            }

            composable(navHomeAnnouncement) {
                val id = it.arguments?.getString("id")?.toInt()
                val viewModel: AnnouncementViewModel by viewModels()

                AnnouncementInfo(
                    announcementId = id,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onAuthFailure = { message ->
                        if (message.contains("доступ")) {
                            requestSnackbar(R.string.error_forbidden, arrayOf())
                        } else {
                            requestSnackbar(R.string.login_error_invalid, arrayOf(message))
                        }
                    },
                    onFailure = { e ->
                        requestSnackbar(R.string.login_error_network, arrayOf(e))
                    }
                )
            }

            // TODO: Implement Settings
            composable(navAccountAppSettings) {
                //val appSettingsViewModel: AppSettingsViewModel by viewModels()

                ApplicationSettings(
                    onBack = { navController.popBackStack() },
                    //viewModel = appSettingsViewModel
                )
            }

            composable(navAccountExtendedInformation) {
                ExtendedInformation(
                    onBack = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
        }
    }

    @Composable
    fun NavBar(navController: NavController) {

        val home = stringResource(R.string.bottom_bar_home)
        val navHome = stringResource(R.string.nav_route_home)

        val journal = stringResource(R.string.bottom_bar_journal)
        val navJournal = stringResource(R.string.nav_route_journal)

        val account = stringResource(R.string.bottom_bar_account)
        val navAccount = stringResource(R.string.nav_route_account)

        NavigationBar {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route

            val isHome = currentRoute?.startsWith(navHome) ?: false
            NavigationBarItem(
                selected = isHome,
                onClick = {
                    navController.navigate(navHome) {
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        if (isHome) Icons.Filled.Home
                        else Icons.Outlined.Home,
                        home)
                },
                label = { Text(home) }
            )

            val isJournal = currentRoute?.startsWith(navJournal) ?: false
            NavigationBarItem(
                selected = isJournal,
                onClick = {
                    navController.navigate(navJournal) {
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        if (isJournal) Icons.Filled.DateRange
                        else Icons.Outlined.DateRange,
                        journal)
                },
                label = { Text(journal) }
            )

            val isAccount = currentRoute?.startsWith(navAccount) ?: false
            NavigationBarItem(
                selected = isAccount,
                onClick = {
                    navController.navigate(navAccount) {
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        if (isAccount) Icons.Filled.AccountCircle
                        else Icons.Outlined.AccountCircle,
                        account)
                },
                label = { Text(account) }
            )
        }
    }

    @Composable
    fun NavRail(navController: NavController) {

        val home = stringResource(R.string.bottom_bar_home)
        val navHome = stringResource(R.string.nav_route_home)

        val journal = stringResource(R.string.bottom_bar_journal)
        val navJournal = stringResource(R.string.nav_route_journal)

        val account = stringResource(R.string.bottom_bar_account)
        val navAccount = stringResource(R.string.nav_route_account)

        NavigationRail(
            windowInsets = WindowInsets(top = 12.dp)
        ) {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route

            Spacer(Modifier.height(8.dp))

            val isHome = currentRoute?.startsWith(navHome) ?: false
            NavigationRailItem(
                selected = isHome,
                onClick = {
                    navController.navigate(navHome) {
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        if (isHome) Icons.Filled.Home
                        else Icons.Outlined.Home,
                        home)
                },
                label = { Text(home) }
            )

            Spacer(Modifier.height(24.dp))

            val isJournal = currentRoute?.startsWith(navJournal) ?: false
            NavigationRailItem(
                selected = isJournal,
                onClick = {
                    navController.navigate(navJournal) {
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        if (isJournal) Icons.Filled.DateRange
                        else Icons.Outlined.DateRange,
                        journal)
                },
                label = { Text(journal) }
            )

            Spacer(Modifier.height(24.dp))

            val isAccount = currentRoute?.startsWith(navAccount) ?: false
            NavigationRailItem(
                selected = isAccount,
                onClick = {
                    navController.navigate(navAccount) {
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        if (isAccount) Icons.Filled.AccountCircle
                        else Icons.Outlined.AccountCircle,
                        account)
                },
                label = { Text(account) }
            )

            Spacer(Modifier.weight(1f))
        }
    }
}
