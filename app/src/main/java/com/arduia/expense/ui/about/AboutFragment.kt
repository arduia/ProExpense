package com.arduia.expense.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arduia.design.theme.ProExpenseTheme
import com.arduia.expense.R
import com.arduia.expense.di.LefSideNavOption
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.about.molecule.AboutMoleculeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.core.net.toUri

@AndroidEntryPoint
class AboutFragment : NavBaseFragment() {

    private val viewModel by viewModels<AboutMoleculeViewModel>()

    @Inject
    @LefSideNavOption
    lateinit var slideNavOptions: NavOptions

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ProExpenseTheme {
                    val state by viewModel.state.collectAsState()
                    val packageManager = requireActivity().packageManager
                    val packageName = requireActivity().packageName
                    val versionName = try {
                        packageManager.getPackageInfo(packageName, 0).versionName
                    } catch (e: Exception) {
                        "N/A"
                    }

                    AboutScreen(
                        state = state,
                        onEvent = viewModel::take,
                        versionName = versionName ?: "N/A",
                        onNavigationIconClick = { navigationDrawer.openDrawer() },
                        onPrivacyClick = {
                            val title = getString(R.string.privacy_policy)
                            val url = getString(R.string.policy_url)
                            navigateToWeb(title, url)
                        },
                        onOpenSourceClick = {
                            val title = getString(R.string.open_source_lib)
                            val url = getString(R.string.open_source_url)
                            navigateToWeb(title, url)
                        },
                        onContributeClick = { openGithubLink() },
                        onDeveloperClick = { sendEmailToDeveloper() },
                        onInstallUpdateClick = {
                            VersionUpdateUtil.openAppStoreLink(requireContext())
                        }
                    )
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

    private fun sendEmailToDeveloper() {
        val developerEmail = getString(R.string.developer_email)
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:$developerEmail".toUri()
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Handle no email app
        }
    }

    private fun navigateToWeb(title: String, url: String) {
        val action = AboutFragmentDirections
            .actionDestAboutToDestWeb(url = url, title = title)
        findNavController().navigate(action, slideNavOptions)
    }
}
