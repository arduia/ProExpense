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
import com.arduia.expense.databinding.FragAboutBinding
import com.arduia.expense.di.LefSideNavOption
import com.arduia.expense.ui.NavBaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AboutFragment : NavBaseFragment() {

    private lateinit var viewBinding: FragAboutBinding

    @Inject
    @LefSideNavOption
    lateinit var slideNavOptions: NavOptions

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewBinding(parent = container)
        return viewBinding.root
    }

    private fun initViewBinding(parent: ViewGroup?) {
        viewBinding = FragAboutBinding.inflate(layoutInflater, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        setupContributeClick()
        setupNavOpenButton()
        setupOpenSourceClick()
        setupPrivacyClick()
    }

    private fun setupContributeClick() {
        viewBinding.flContribute.setOnClickListener {
            openGithubLink()
        }
    }

    private fun setupNavOpenButton() {
        viewBinding.btnDrawerOpen.setOnClickListener {
            navigationDrawer?.openDrawer()
        }
    }

    private fun setupOpenSourceClick() {
        viewBinding.flOpenSources.setOnClickListener {
            val title = getString(R.string.label_open_source)
            val url = getString(R.string.url_open_source)
            navigateToWeb(title, url)
        }
    }

    private fun setupPrivacyClick() {
        viewBinding.flPrivacy.setOnClickListener {
            val title = getString(R.string.label_policy)
            val url = getString(R.string.url_policy)
            navigateToWeb(title, url)
        }
    }


    private fun openGithubLink() {
        val intent = Intent().apply {
            val url = getString(R.string.url_github_link)
            action = Intent.ACTION_VIEW
            data = Uri.parse(url)
        }
        startActivity(intent)
    }

    private fun navigateToWeb(title: String, url: String) {
        val action = AboutFragmentDirections
            .actionDestAboutToDestWeb(url = url, title = title)
        findNavController().navigate(action, slideNavOptions)
    }
}
