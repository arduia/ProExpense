package com.arduia.expense.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.arduia.core.lang.updateResource
import com.arduia.expense.R
import com.arduia.expense.data.SettingsRepositoryImpl
import com.arduia.expense.databinding.ActivMainBinding
import com.arduia.expense.databinding.LayoutHeaderBinding
import com.arduia.expense.di.IntegerDecimal
import com.arduia.expense.ui.backup.BackupMessageViewModel
import com.arduia.mvvm.EventObserver
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationDrawer,
    MainHost, BackupMessageReceiver{

    private lateinit var viewBinding: ActivMainBinding

    private lateinit var headerBinding: LayoutHeaderBinding

    private val backupViewModel by viewModels<BackupMessageViewModel>()

    private val navController by lazy {  findNavController() }

    private val navOption by lazy { createNavOption() }

    private var itemSelectTask: (() -> Unit)? = null

    override val defaultSnackBarDuration: Int  by lazy { resources.getInteger(R.integer.duration_short_snack) }

    private var addBtnClickListener: () -> Unit = { }

    private var lastSnackBar: Snackbar? = null

    private var addFabShowTask: (() -> Unit)? = null

    @Inject
    @IntegerDecimal
    lateinit var countFormat: DecimalFormat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ProExpense)

        viewBinding = ActivMainBinding.inflate(layoutInflater)
        headerBinding = LayoutHeaderBinding.bind(viewBinding.nvMain.getHeaderView(0))

        setContentView(viewBinding.root)
        setupView()
        setupViewModel()
    }

    private fun setupViewModel(){
        backupViewModel.finishedEvent.observe(this, EventObserver{
            showBackupFinishedMessage(count = it)
        })
    }

    private fun showBackupFinishedMessage(count: Int){
        val isMultiItem = (count > 1)
        val msg = if(isMultiItem)
            getString(R.string.multi_items_imported)
        else
            getString(R.string.item_imported)

        showSnackMessage("${countFormat.format(count)} $msg")
    }

    private fun findNavController(): NavController{
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fc_main) as NavHostFragment
        return navHostFragment.navController
    }

    private fun setupView(){

        viewBinding.fbMainAdd.setColorFilter(Color.WHITE)
        viewBinding.fbMainAdd.setOnClickListener {
            addBtnClickListener.invoke()
        }

        viewBinding.fbMainAdd.hide()

        viewBinding.nvMain.setupWithNavController(navController)

        viewBinding.nvMain.setNavigationItemSelectedListener listener@{ menuItem ->

            itemSelectTask = { selectPage(selectedMenuItem = menuItem) }
            viewBinding.dlMain.closeDrawer(GravityCompat.START)

            return@listener true
        }

        viewBinding.dlMain.addDrawerListener(object: DrawerLayout.DrawerListener{

            override fun onDrawerStateChanged(newState: Int) { }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {  }

            override fun onDrawerClosed(drawerView: View) {
                itemSelectTask?.invoke()
                itemSelectTask = null
            }

            override fun onDrawerOpened(drawerView: View) { }
        })

        headerBinding.btnClose.setOnClickListener {
           closeDrawer()
        }

    }

    private fun selectPage(selectedMenuItem: MenuItem){

        val isHomePage = (selectedMenuItem.itemId == R.id.dest_home)

        if(isHomePage){
            navController.popBackStack(R.id.dest_home, false)
        }

        navController.navigate(selectedMenuItem.itemId, null, navOption)

    }

    override fun addTaskID(id: UUID) {
        backupViewModel.addTaskID(id)
    }

    override fun removeTaskID(id: UUID) {
       backupViewModel.removeTaskID(id)
    }

    override fun openDrawer() {
        viewBinding.dlMain.openDrawer(GravityCompat.START)
    }

    override fun closeDrawer() {
        viewBinding.dlMain.closeDrawer(GravityCompat.START)
    }

    override fun lockDrawer() {
        with(viewBinding.dlMain){
            closeDrawer(GravityCompat.START)
            setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    override fun unlockDrawer() {
        viewBinding.dlMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun navigateUpTo(upIntent: Intent?): Boolean {
        return super.navigateUpTo(upIntent) or navController.navigateUp()
    }

    override fun onBackPressed() {
        if(drawerClosure()){
            super.onBackPressed()
        }
    }

    private fun drawerClosure():Boolean{
        val isDrawerOpen = viewBinding.dlMain.isDrawerOpen(GravityCompat.START)
        if(isDrawerOpen){
            viewBinding.dlMain.closeDrawer(GravityCompat.START)
            return false
        }
        return true
    }

    override fun showAddButton() {

        addFabShowTask =  { showAddFab() }

        when(lastSnackBar?.isShown){
            true -> {
                MainScope().launch {
                    val delayDuration = (lastSnackBar?.duration ?:0 ) + 300 //Extra 100 for animation
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


    private fun showAddFab(){
        viewBinding.fbMainAdd.show()
        viewBinding.fbMainAdd.isClickable = true
    }

    override fun hideAddButton() {
        //remove task
        addFabShowTask = null

        viewBinding.fbMainAdd.isClickable = false
        viewBinding.fbMainAdd.hide()
    }

    override fun showSnackMessage(message: String, duration: Int) {
           lastSnackBar = Snackbar.make(viewBinding.clMain, message, duration).apply {
               show()
           }
    }

    override fun setAddButtonClickListener(listener: () -> Unit) {
        addBtnClickListener = listener
    }

    private fun createNavOption() =
        NavOptions.Builder()
            .setLaunchSingleTop(true)
            .build()

    override fun onDestroy() {
        super.onDestroy()
        itemSelectTask = {}
    }

    override fun attachBaseContext(newBase: Context?) {
        runBlocking {
            newBase?.let {
                val settings = SettingsRepositoryImpl(it, this)
                val selectedLanguage = settings.getSelectedLanguage().first()

                val localedContext = newBase.updateResource(selectedLanguage)
                super.attachBaseContext(localedContext)
            }
        }

    }
}
