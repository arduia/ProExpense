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
import com.arduia.expense.databinding.ActivMainBinding
import com.arduia.expense.databinding.LayoutHeaderBinding
import com.arduia.expense.di.IntegerDecimal
import com.arduia.expense.model.getDataOrError
import com.arduia.expense.ui.backup.BackupMessageViewModel
import com.arduia.expense.ui.common.themeColor
import com.arduia.mvvm.EventObserver
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationDrawer,
    MainHost, BackupMessageReceiver {

    private lateinit var binding: ActivMainBinding

    private lateinit var headerBinding: LayoutHeaderBinding

    private val backupViewModel by viewModels<BackupMessageViewModel>()

    private lateinit var navController: NavController

    private lateinit var navOption: NavOptions

    private var itemSelectTask: (() -> Unit)? = null

    override val defaultSnackBarDuration: Int by lazy { resources.getInteger(R.integer.duration_short_snack) }

    private var addBtnClickListener: (() -> Unit)? = {}

    private var snackBarMessage: Snackbar? = null

    private var addFabShowTask: (() -> Unit)? = null

    private val viewModel by viewModels<MainViewModel>()

    @Inject
    @IntegerDecimal
    lateinit var countFormat: DecimalFormat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)
        setTheme(R.style.Theme_ProExpense)
        binding = ActivMainBinding.inflate(layoutInflater)
        headerBinding = LayoutHeaderBinding.bind(binding.nvMain.getHeaderView(0))
        setContentView(binding.root)
        navController = findNavController()
        navOption = createNavOption()
        setupView()
        setupViewModel()
    }

    private fun setupViewModel() {
        backupViewModel.finishedEvent.observe(this, EventObserver {
            showBackupFinishedMessage(count = it)
        })
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

    private fun setupView() {

        binding.fbMainAdd.setColorFilter(this.themeColor(R.attr.colorOnPrimary))
        binding.fbMainAdd.setOnClickListener {
            addBtnClickListener?.invoke()
        }

        binding.fbMainAdd.hide()

        binding.nvMain.setupWithNavController(navController)

        binding.nvMain.setNavigationItemSelectedListener listener@{ menuItem ->

            itemSelectTask = { selectPage(selectedMenuItem = menuItem) }

            binding.dlMain.closeDrawer(GravityCompat.START)

            return@listener true
        }

        binding.dlMain.addDrawerListener(object : DrawerLayout.DrawerListener {

            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerClosed(drawerView: View) {
                itemSelectTask?.invoke()
                itemSelectTask = null
            }

            override fun onDrawerOpened(drawerView: View) {}
        })

        navController.addOnDestinationChangedListener { _, dest, _ ->

            if (TOP_DESTINATIONS.contains(dest.id)) {
                binding.dlMain.setDrawerLockMode(
                    DrawerLayout.LOCK_MODE_UNLOCKED
                )
            } else binding.dlMain.setDrawerLockMode(
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            )
        }

        headerBinding.btnClose.setOnClickListener {
            closeDrawer()
        }

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
        binding.dlMain.openDrawer(GravityCompat.START)
    }

    override fun closeDrawer() {
        binding.dlMain.closeDrawer(GravityCompat.START)
    }

    override fun lockDrawer() {
        with(binding.dlMain) {
            closeDrawer(GravityCompat.START)
            setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    override fun unlockDrawer() {
        binding.dlMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
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
        val isDrawerOpen = binding.dlMain.isDrawerOpen(GravityCompat.START)
        if (isDrawerOpen) {
            binding.dlMain.closeDrawer(GravityCompat.START)
            return false
        }
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
        binding.fbMainAdd.show()
        binding.fbMainAdd.isClickable = true
    }

    override fun hideAddButton() {
        addFabShowTask = null
        binding.fbMainAdd.isClickable = false
        binding.fbMainAdd.hide()
    }

    override fun showSnackMessage(message: String, duration: Int) {
        snackBarMessage = Snackbar.make(binding.clMain, message, duration).apply {
            show()
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
        itemSelectTask = null
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
