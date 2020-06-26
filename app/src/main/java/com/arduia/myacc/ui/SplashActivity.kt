package com.arduia.myacc.ui

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.arduia.core.extension.px
import com.arduia.myacc.R
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity(){

    private val contentView by lazy {  createView() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentView)

        //This execution can be in App Alive.
        MainScope().launch(Dispatchers.Main){
            delay(150)
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

    }

    private fun createView():View{

        //Require View Components
        val frameLayout = FrameLayout(this)
        val imageView = ImageView(this)
        val progressBar = ProgressBar(this)

        //Configure
        with(frameLayout){
            background = ContextCompat.getDrawable(this@SplashActivity, R.color.primaryColor)
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }

        with(imageView){
            layoutParams = FrameLayout.LayoutParams(px(80), px(80)).apply {
                gravity = Gravity.CENTER
            }
            setImageResource(R.drawable.ic_borrow)
            setColorFilter(android.R.color.white)
        }

        with(progressBar){
            layoutParams = FrameLayout.LayoutParams(px(30), px(30)).apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                bottomMargin = resources.getDimension(R.dimen.material_margin).toInt()
            }
            id = View.generateViewId()
        }

        //add to Desire Views
        frameLayout.addView(imageView)

        return frameLayout
    }

}
