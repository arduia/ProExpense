package com.arduia.expense.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arduia.expense.R
import com.arduia.expense.ui.MainHost
import com.arduia.expense.databinding.FragAboutBinding
import com.arduia.expense.databinding.FragExpenseBinding
import com.arduia.expense.databinding.FragExpenseEntryBinding
import com.arduia.expense.ui.NavBaseFragment
import java.net.URI

class AboutFragment : NavBaseFragment(){

    private lateinit var viewBinding: FragAboutBinding

    private val mainHost by lazy {
        requireActivity() as MainHost
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragAboutBinding.inflate(layoutInflater)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView(){

        viewBinding.btnDrawerOpen.setOnClickListener {
            openDrawer()
        }

        viewBinding.flPrivacy.setOnClickListener {
            val title = getString(R.string.label_policy)
            val url = getString(R.string.url_policy)
            navigateToWeb(title, url)
        }

        viewBinding.flOpenSources.setOnClickListener {
            val title = getString(R.string.label_open_source)
            val url = getString(R.string.url_open_source)
            navigateToWeb(title, url)
        }

        viewBinding.flContribute.setOnClickListener {
            val intent = Intent().apply {
                val url = getString(R.string.url_github_link)
                action = Intent.ACTION_VIEW
                data = Uri.parse(url)
            }
            startActivity(intent)
        }

    }

    private fun navigateToWeb(title:String, url:String){
        val action = AboutFragmentDirections
            .actionDestAboutToDestWeb(url = url, title = title)
        findNavController().navigate(action, createWebNavOption())
    }

    private fun createWebNavOption() =
        NavOptions.Builder()
            //For Transaction Fragment
            .setEnterAnim(R.anim.expense_enter_left)
            .setPopExitAnim(R.anim.expense_exit_right)
            //For Home Fragment
            .setExitAnim(R.anim.nav_default_exit_anim)
            .setPopEnterAnim(R.anim.nav_default_enter_anim)
            .setLaunchSingleTop(true)
            .build()

}
