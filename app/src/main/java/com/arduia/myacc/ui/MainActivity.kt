package com.arduia.myacc.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.*
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.arduia.myacc.NavigationDrawer
import com.arduia.myacc.R
import com.arduia.myacc.databinding.ActivMainBinding
import com.arduia.myacc.databinding.LayoutHeaderBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationDrawer{

    private val viewBinding by lazy { ActivMainBinding.inflate(layoutInflater) }
    private val headerBinding by lazy { LayoutHeaderBinding.bind(viewBinding.nvMain.getHeaderView(0)) }
    private val navController by lazy { findNavController(R.id.fc_main) }
    private val navOption by lazy {  createNavOption() }

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
            MainScope().launch {
                viewBinding.dlMain.closeDrawer(GravityCompat.START)
                //wait for drawer closure
                delay(300)
                //popBack last
                if(it.itemId == R.id.dest_home ){
                    navController.popBackStack(R.id.dest_home,false)
                }
                navController.navigate(it.itemId,null,navOption)
            }
            return@listener true
        }

        headerBinding.btnBack.setOnClickListener {
            viewBinding.dlMain.closeDrawer(GravityCompat.START)
        }
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

}
