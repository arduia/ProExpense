package com.arduia.expense.ui.about

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
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
        setAppVersionName()
        setupContributeClick()
        setupNavOpenButton()
        setupOpenSourceClick()
        setupPrivacyClick()
        showAboutDeveloper()
    }

    private fun setAppVersionName(){
        viewBinding.tvVersion.text = with(requireActivity()){
            packageManager.getPackageInfo(packageName,0).versionName
        }
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
            val title = getString(R.string.open_source_lib)
            val url = getString(R.string.open_source_url)
            navigateToWeb(title, url)
        }
    }

    private fun setupPrivacyClick() {
        viewBinding.flPrivacy.setOnClickListener {
            val title = getString(R.string.privacy_policy)
            val url = getString(R.string.policy_url)
            navigateToWeb(title, url)
        }
    }

    private fun showAboutDeveloper() {
        val devString = getString(R.string.developer)
        val devMailString = "\n" + getString(R.string.developer_email)
        val devSpanText = SpannableStringBuilder(devString)
        devSpanText.append(devMailString)
        devSpanText.setSpan(
            StyleSpan(Typeface.BOLD), devString.length, devString.length + devMailString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        devSpanText.setSpan(
            URLSpan(getString(R.string.mail_to_developer)),
            devString.length,
            devString.length + devMailString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val smaillSize = RelativeSizeSpan(0.9f)
        devSpanText.setSpan(
            smaillSize,
            devString.length,
            devString.length + devMailString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        viewBinding.tvDeveloper.movementMethod = LinkMovementMethod.getInstance()
        viewBinding.tvDeveloper.text = devSpanText
    }


    private fun openGithubLink() {
        val intent = Intent().apply {
            val url = getString(R.string.github_link_url)
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
