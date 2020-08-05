package com.arduia.expense.ui.web

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arduia.expense.R
import com.arduia.expense.databinding.FragWebBinding
import com.arduia.expense.ui.MainHost
import com.arduia.expense.ui.NavigationDrawer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WebFragment : Fragment(){

    private lateinit var viewBinding: FragWebBinding

    private val args: WebFragmentArgs by navArgs()

    private val navDrawer by lazy {
        requireActivity() as NavigationDrawer
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragWebBinding.inflate(layoutInflater)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    private fun setupView(){

        if(args.url.isEmpty()){
            findNavController().popBackStack()
            val mainHost = (requireActivity() as MainHost)
            mainHost.showSnackMessage("No URL found!")
        }

        viewBinding.tvWebTitle.text = args.title
        viewBinding.wvMain.loadUrl(args.url)
        viewBinding.wvMain.webViewClient = object : WebViewClient(){

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                viewBinding.pbLoading.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                viewBinding.pbLoading.visibility = View.INVISIBLE
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                request?.url?.let {
                    if(it.path != args.url){
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = it
                        }
                        startActivity(intent)
                        return true
                    }
                }
                return false
            }
        }

        viewBinding.btnPopBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navDrawer.lockNavDrawer()
    }

    override fun onDestroy() {
        super.onDestroy()
        navDrawer.unlockNavDrawer()
    }

}
