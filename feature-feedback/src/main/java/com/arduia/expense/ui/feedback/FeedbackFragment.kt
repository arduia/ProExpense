package com.arduia.expense.ui.feedback

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.arduia.expense.R
import com.arduia.expense.databinding.FragmentFeedbackBinding
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.NavigationDrawer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedbackFragment : NavBaseFragment() {

    private var _binding: FragmentFeedbackBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<FeedbackViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        collectEffects()
    }

    private fun setupView() {
        binding.toolbar.setNavigationOnClickListener {
            (requireActivity() as? NavigationDrawer)?.openDrawer()
        }

        binding.btnSend.setOnClickListener {
            sendFeedback()
        }
    }

    private fun collectEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEffect.collect { effect ->
                    when (effect) {
                        is FeedbackUiEffect.FeedbackSubmitted -> {
                            showFeedbackStatusDialog()
                            clearInputField()
                        }
                    }
                }
            }
        }
    }

    private fun clearInputField() {
        binding.edtComment.setText("")
        binding.edtName.setText("")
        binding.edtEmail.setText("")
    }

    private fun showFeedbackStatusDialog() {
        FeedbackStatusDialog().show(parentFragmentManager, "Status")
    }

    private fun sendFeedback() {
        val name = getName()
        val email = getEmail()
        val comment = getComment()

        var isValidComment = true

        if (email.isNotEmpty()) {
            val valid = isValidEmail(email)
            if (valid.not()) {
                showEmailError()
                isValidComment = false
            }
        }

        if (comment.isEmpty()) {
            showCommentEmptyError()
            isValidComment = false
        }

        if (isValidComment) {
            viewModel.sendFeedback(name, email, comment)
        }
    }

    private fun showEmailError() {
        binding.edtEmail.error = getString(R.string.invalid_email)
    }

    private fun isValidEmail(email: String): Boolean {
        if (email.isEmpty()) return false
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showCommentEmptyError() {
        binding.edtComment.error = getString(R.string.empty_comment)
    }

    private fun getName() = binding.edtName.text.toString()

    private fun getEmail() = binding.edtEmail.text.toString()

    private fun getComment() = binding.edtComment.text.toString()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
