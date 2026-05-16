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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arduia.core.view.asGone
import com.arduia.core.view.asVisible
import com.arduia.expense.feature.about.R
import com.arduia.expense.feature.about.databinding.FragmentAboutBinding
import com.arduia.expense.di.LefSideNavOption
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.settings.SettingsViewModel
import com.arduia.expense.ui.settings.SettingsUiEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AboutFragment : NavBaseFragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    @Inject
    @LefSideNavOption
    lateinit var slideNavOptions: NavOptions

    private var aboutUpdateDialog: AboutUpdateDialog? = null

    private val settingViewModel by activityViewModels<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        collectState()
        collectEffects()
    }

    private fun setupView() {
        setAppVersionName()
        setupContributeClick()
        setupNavOpenButton()
        setupOpenSourceClick()
        setupPrivacyClick()
        showAboutDeveloper()
        binding.lnUpdate.setOnClickListener {
            settingViewModel.onOpenNewUpdateInfo()
        }
    }

    private fun setAppVersionName() {
        binding.tvVersion.text = with(requireActivity()) {
            packageManager.getPackageInfo(packageName, 0).versionName
        }
    }

    private fun setupContributeClick() {
        binding.flContribute.setOnClickListener {
            openGithubLink()
        }
    }

    private fun setupNavOpenButton() {
        binding.toolbar.setNavigationOnClickListener {
            navigationDrawer?.openDrawer()
        }
    }

    private fun setupOpenSourceClick() {
        binding.flOpenSources.setOnClickListener {
            val title = getString(R.string.open_source_lib)
            val url = getString(R.string.open_source_url)
            navigateToWeb(title, url)
        }
    }

    private fun setupPrivacyClick() {
        binding.flPrivacy.setOnClickListener {
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
        val smallSize = RelativeSizeSpan(0.9f)
        devSpanText.setSpan(
            smallSize,
            devString.length,
            devString.length + devMailString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvDeveloper.movementMethod = LinkMovementMethod.getInstance()
        binding.tvDeveloper.text = devSpanText
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingViewModel.uiState.collect { state ->
                    Timber.d("isNewVersionAvailable! ${state.isNewVersionAvailable}")
                    if (state.isNewVersionAvailable) {
                        binding.lnUpdate.asVisible()
                    } else {
                        binding.lnUpdate.asGone()
                    }
                }
            }
        }
    }

    private fun collectEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingViewModel.uiEffect.collect { effect ->
                    when (effect) {
                        is SettingsUiEffect.ShowAboutUpdate -> showAboutUpdateDialog(effect.info)
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun openGithubLink() {
        val intent = Intent().apply {
            val url = getString(R.string.github_link_url)
            action = Intent.ACTION_VIEW
            data = Uri.parse(url)
        }
        startActivity(intent)
    }

    private fun showAboutUpdateDialog(data: AboutUpdateUiModel) {
        aboutUpdateDialog?.dismiss()
        aboutUpdateDialog = AboutUpdateDialog(requireContext()).apply {
            setOnInstallClickListener {
                VersionUpdateUtil.openAppStoreLink(requireContext())
            }
        }
        aboutUpdateDialog?.show(data)
    }

    private fun navigateToWeb(title: String, url: String) {
        findNavController().navigate(
            R.id.dest_web,
            android.os.Bundle().apply {
                putString("url", url)
                putString("title", title)
            },
            slideNavOptions
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
