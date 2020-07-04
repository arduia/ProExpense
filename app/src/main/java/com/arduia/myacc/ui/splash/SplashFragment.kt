package com.arduia.myacc.ui.splash

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arduia.core.extension.px
import com.arduia.myacc.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment: Fragment(){

    private val contentView by lazy {  createView() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =  contentView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainScope().launch(Dispatchers.Main){
            delay(1000)

            findNavController().popBackStack()
            findNavController().navigate(R.id.dest_home)
        }
    }

    private fun createView(): View {

        //Require View Components
        val frameLayout = FrameLayout(requireContext())
        val imageView = ImageView(requireContext())
        val progressBar = ProgressBar(requireContext())

        //Configure
        with(frameLayout){
            background = ContextCompat.getDrawable(requireContext(), R.color.primaryColor)
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