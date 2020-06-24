package com.arduia.myacc.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.*
import androidx.navigation.ui.setupWithNavController
import com.arduia.core.performance.printDurationMilli
import com.arduia.core.performance.printDurationNano
import com.arduia.myacc.NavigationDrawer
import com.arduia.myacc.R
import com.arduia.myacc.databinding.ActivMainBinding
import com.arduia.myacc.databinding.LayoutHeaderBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), NavigationDrawer{

    private val viewBinding by lazy {
        printDurationMilli("MainActivity", "Activity Binding"){
            ActivMainBinding.inflate(layoutInflater)
        }
    }
    private val headerBinding by lazy { LayoutHeaderBinding.bind(viewBinding.nvMain.getHeaderView(0)) }
    private val navController by lazy { findNavController(R.id.fc_main) }
    private val navOption by lazy { createNavOption() }
    private var itemSelectionTask: (()->Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        setupView()
    }

    private fun setupView(){
        setupNavigation()
    }

    private fun setupNavigation(){

        viewBinding.nvMain.setupWithNavController(navController)

        viewBinding.nvMain.setNavigationItemSelectedListener listener@{

            //for smooth drawer motions
            //register new task
            itemSelectionTask = { selectItem(it) }

            //close drawer first
            viewBinding.dlMain.closeDrawer(GravityCompat.START)

            return@listener true
        }

        viewBinding.dlMain.addDrawerListener(object:DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {

            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerClosed(drawerView: View) {
                //execute selected item
                itemSelectionTask?.invoke()
                //remove old task
                itemSelectionTask = null
            }

            override fun onDrawerOpened(drawerView: View) {

            }

        })

        headerBinding.btnBack.setOnClickListener {
            viewBinding.dlMain.closeDrawer(GravityCompat.START)
        }

    }

    private fun selectItem(item:MenuItem){
        if(item.itemId == R.id.dest_home ){
            navController.popBackStack(R.id.dest_home,false)
        }
        navController.navigate(item.itemId,null,navOption)
    }

    override fun openDrawer() {
        viewBinding.dlMain.openDrawer(GravityCompat.START)
    }

    override fun navigateUpTo(upIntent: Intent?): Boolean {
        return super.navigateUpTo(upIntent) or navController.navigateUp()
    }

    //separated function to improve readability
    private fun createNavOption() =
        NavOptions.Builder()
            .setLaunchSingleTop(true)
            .build()

    override fun onDestroy() {
        super.onDestroy()
        itemSelectionTask = {}
    }

}
