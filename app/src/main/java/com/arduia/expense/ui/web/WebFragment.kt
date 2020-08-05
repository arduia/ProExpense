package com.arduia.expense.ui.web

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arduia.expense.databinding.FragWebBinding
import com.arduia.expense.ui.MainHost
import com.arduia.expense.ui.NavigationDrawer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WebFragment : Fragment(){

    private lateinit var viewBinding: FragWebBinding

    private val args: WebFragmentArgs by navArgs()

    private val navDrawer by lazy {
        requireActivity() as NavigationDrawer
    }

    @Inject
    lateinit var mainHost: MainHost


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

    private fun setupView(){

        val currentWebUrl = args.url

        if(currentWebUrl.isEmpty()){
            mainHost.showSnackMessage("No URL found!")
            popBackToPreviousFragment()
            return
        }

        val pageTitleText = args.title

        viewBinding.tvWebTitle.text = pageTitleText
        viewBinding.wvMain.loadUrl(currentWebUrl)
        viewBinding.wvMain.webViewClient = getWebClient()

        viewBinding.btnPopBack.setOnClickListener {
            popBackToPreviousFragment()
        }
    }

    private fun getWebClient() = object : WebViewClient(){

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            showLoading()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            hideLoading()
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {

            val shouldOverload = false
            val shouldNotOverload = true

            val requestUrl = request?.url ?: return shouldNotOverload

            val isCurrentUrl = (requestUrl.path == args.url)

            if(isCurrentUrl.not()){
                openUrlToExternalBrowser(url = requestUrl)
                return shouldNotOverload
            }

            return shouldOverload
        }

        private fun openUrlToExternalBrowser(url: Uri){
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = url
            }
            startActivity(intent)
        }
    }

    private fun showLoading(){
        viewBinding.pbLoading.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        viewBinding.pbLoading.visibility = View.INVISIBLE
    }
    private fun popBackToPreviousFragment(){
        findNavController().popBackStack()
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
