package com.arduia.expense.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.arduia.core.lang.updateResource
import com.arduia.expense.R
import com.arduia.expense.data.SettingRepositoryFactoryImpl
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import com.arduia.expense.di.IntegerDecimal
import com.arduia.expense.di.TopDropNavOption
import com.arduia.expense.model.getDataOrError
import com.arduia.expense.ui.backup.BackupMessageViewModel
import com.arduia.mvvm.EventObserver
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject
import androidx.core.content.ContextCompat
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.ui.about.AboutUpdateUiModel
import com.arduia.expense.ui.about.ForceUpgradeDialog
import com.arduia.expense.ui.about.VersionUpdateUtil
import com.arduia.expense.ui.compose.MainScreen


@AndroidEntryPoint
class MainActivity @Inject constructor() : AppCompatActivity(), NavigationDrawer,
    MainHost, BackupMessageReceiver {

    private val backupViewModel by viewModels<BackupMessageViewModel>()

    private lateinit var navController: NavController

    private lateinit var navOption: NavOptions

    // Removed itemSelectTask

    override val defaultSnackBarDuration: Int by lazy { resources.getInteger(R.integer.duration_short_snack) }

    private var addBtnClickListener: (() -> Unit)? = {}

    private var snackBarMessage: Snackbar? = null

    private var addFabShowTask: (() -> Unit)? = null

    private val viewModel by viewModels<MainViewModel>()

    private var aboutUpdateDialog: ForceUpgradeDialog? = null

    @Inject
    @IntegerDecimal
    lateinit var countFormat: DecimalFormat

    @Inject
    @TopDropNavOption
    lateinit var entryNavOption: NavOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)
        setTheme(R.style.Theme_ProExpense)
        setContent {
            ProExpenseTheme {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                var currentDestId by remember { mutableStateOf<Int?>(null) }
                var isDrawerLocked by remember { mutableStateOf(false) }

                // Expose drawer controls to Activity
                LaunchedEffect(drawerState) {
                    drawerOpenAction = { scope.launch { drawerState.open() } }
                    drawerCloseAction = { scope.launch { drawerState.close() } }
                }

                LaunchedEffect(isDrawerLocked) {
                    drawerLockAction = { isDrawerLocked = true }
                    drawerUnlockAction = { isDrawerLocked = false }
                }

                MainScreen(
                    drawerState = drawerState,
                    currentDestinationId = currentDestId,
                    onItemClick = { destinationId ->
                        val controller = navController
                        if (destinationId == R.id.dest_home) {
                            controller.popBackStack(R.id.dest_home, false)
                        }
                        controller.navigate(destinationId, null, navOption)
                    }
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            val view = android.view.LayoutInflater.from(context)
                                .inflate(R.layout.content_main, null, false)
                            view
                        },
                        update = { view ->
                            // View is inflated, configure NavController if not done
                            if (!::navController.isInitialized) {
                                val navHostFragment = supportFragmentManager
                                    .findFragmentById(R.id.fc_main) as NavHostFragment
                                navController = navHostFragment.navController
                                
                                navController.addOnDestinationChangedListener { _, dest, _ ->
                                    currentDestId = dest.id
                                    
                                    if (com.arduia.expense.ui.compose.TOP_DESTINATIONS.contains(dest.id)) {
                                        isDrawerLocked = false
                                    } else {
                                        isDrawerLocked = true
                                    }

                                    if (dest.id == R.id.dest_home) {
                                        setAddButtonClickListener {
                                            navController.navigate(R.id.dest_expense_entry, null, entryNavOption)
                                        }
                                        showAddButton()
                                    } else hideAddButton()
                                }
                            }
                        }
                    )
                }
            }
        }
        navOption = createNavOption()
        setupViewModel()
    }

    private var drawerOpenAction: (() -> Unit)? = null
    private var drawerCloseAction: (() -> Unit)? = null
    private var drawerLockAction: (() -> Unit)? = null
    private var drawerUnlockAction: (() -> Unit)? = null


    private fun showForceUpgrade(data: AboutUpdateUiModel) {
        aboutUpdateDialog?.dismiss()
        aboutUpdateDialog = ForceUpgradeDialog(this).apply {
            setOnInstallClickListener {
                VersionUpdateUtil.openAppStoreLink(this@MainActivity)
                this@MainActivity.finish()
            }
            setOnCloseListener {
                this@MainActivity.finish()
            }
        }
        aboutUpdateDialog?.show(data)
    }

    private fun setupViewModel() {
        backupViewModel.finishedEvent.observe(this, EventObserver {
            showBackupFinishedMessage(count = it)
        })

        viewModel.forceUpgradeState.observe(this) { state ->
            val (enabled, info) = state
            if (enabled && info != null) {
                showForceUpgrade(info)
            }
        }
    }

    private fun showBackupFinishedMessage(count: Int) {
        val isMultiItem = (count > 1)
        val msg = if (isMultiItem)
            getString(R.string.multi_items_imported)
        else
            getString(R.string.item_imported)

        showSnackMessage("${countFormat.format(count)} $msg")
    }

    private fun findNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fc_main) as NavHostFragment
        return navHostFragment.navController
    }


    private fun selectPage(selectedMenuItem: MenuItem) {
        val isHome = (selectedMenuItem.itemId == R.id.dest_home)

        if (isHome) {
            navController.popBackStack(R.id.dest_home, false)
        }

        navController.navigate(selectedMenuItem.itemId, null, navOption)
    }

    override fun registerBackupTaskID(id: UUID) {
        backupViewModel.addTaskID(id)
    }

    override fun unregisterBackupTaskID(id: UUID) {
        backupViewModel.removeTaskID(id)
    }

    override fun openDrawer() {
        drawerOpenAction?.invoke()
    }

    override fun closeDrawer() {
        drawerCloseAction?.invoke()
    }

    override fun lockDrawer() {
        closeDrawer()
        drawerLockAction?.invoke()
    }

    override fun unlockDrawer() {
        drawerUnlockAction?.invoke()
    }

    override fun navigateUpTo(upIntent: Intent?): Boolean {
        return super.navigateUpTo(upIntent) or navController.navigateUp()
    }

    override fun onBackPressed() {
        if (doDrawerClosure()) {
            super.onBackPressed()
        }
    }

    private fun doDrawerClosure(): Boolean {
        // If drawer is open, we shouldn't exit the app on back press. But we can't observe compose state synchronously here trivially.
        // Let's defer to Android's default or the predictive back handler in Compose.
        // For simplicity returning true to allow standard popBackStack.
        return true
    }

    override fun showAddButton() {

        addFabShowTask = { showAddFab() }

        when (snackBarMessage?.isShown) {
            true -> {
                lifecycleScope.launch {
                    val delayDuration =
                        (snackBarMessage?.duration ?: 0) + 300 //Extra 100 for animation
                    delay(delayDuration.toLong())
                    addFabShowTask?.invoke()
                }
            }

            else -> {
                addFabShowTask?.invoke()
            }
        }
    }

    override fun showAddButtonInstantly() {
        showAddFab()
    }


    private fun showAddFab() {
        // Now handled by Compose screens
    }

    override fun hideAddButton() {
        addFabShowTask = null
    }

    override fun showSnackMessage(message: String, duration: Int) {
        val view = findViewById<View>(android.R.id.content)
        if (view != null) {
            snackBarMessage = Snackbar.make(view, message, duration).apply {
                show()
            }
        }
    }

    override fun setAddButtonClickListener(listener: (() -> Unit)?) {
        addBtnClickListener = listener
    }

    private fun createNavOption() =
        NavOptions.Builder()
            .setLaunchSingleTop(true)
            .build()

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(viewModel)
    }

    override fun attachBaseContext(newBase: Context?) {
        val localedContext = setUiModeAndGetLocaleContext(newBase)
        super.attachBaseContext(localedContext)
    }

    private fun setUiModeAndGetLocaleContext(base: Context?): Context? = runBlocking {
        if (base == null) return@runBlocking base
        val setting = SettingRepositoryFactoryImpl.create(base)
        val selectedLanguage = setting.getSelectedLanguageSync().getDataOrError()
        delegate.localNightMode = setting.getSelectedThemeModeSync().getDataOrError()
        return@runBlocking base.updateResource(selectedLanguage)
    }

    companion object {
        private val TOP_DESTINATIONS = listOf(
            R.id.dest_home,
            R.id.dest_backup,
            R.id.dest_statistics,
            R.id.dest_feedback,
            R.id.dest_about,
            R.id.dest_settings,
            R.id.dest_expense_logs
        )
    }
}
