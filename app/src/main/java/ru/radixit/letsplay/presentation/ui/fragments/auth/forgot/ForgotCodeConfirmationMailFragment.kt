package ru.radixit.letsplay.presentation.ui.fragments.auth.forgot

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragmentAuthForgotCodeConfirmationMailBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.utils.hideKeyboardOnScroll

/**
 * Экран письма с подтверждение забытого кода
 */
@AndroidEntryPoint
class ForgotCodeConfirmationMailFragment : BaseFragment() {

    private val viewModel: ForgotViewModel by viewModels()
    private var _binding: FragmentAuthForgotCodeConfirmationMailBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthForgotCodeConfirmationMailBinding.inflate(inflater, container, false)
        viewModel.sessionManagerEmailOrPhone()
        viewModel.data.observe(viewLifecycleOwner) {
            binding.textView6.text = getString(R.string.we_send_code) + " " + it
        }
        binding.codeFromMailButton.setOnClickListener {
            if (binding.inputCodeFromMail.text?.length == 4) {
                viewModel.reset(binding.inputCodeFromMail.text.toString())
                viewModel.success.observe(viewLifecycleOwner) {
                    if (it) {
                        findNavController().navigate(R.id.action_forgotCodeConfirmationMailFragment_to_forgotNewPasswordFragment)
                    }
                }
            }
        }
        binding.parent.setOnTouchListener { view, _ ->
            requireActivity().hideKeyboardOnScroll(view)
        }
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }
}