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
import ru.radixit.letsplay.databinding.FragmentAuthForgotBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.utils.hideKeyboardOnScroll
import ru.radixit.letsplay.utils.showSnackBar

/**
 * Экран забыли пароль
 */
@AndroidEntryPoint
class ForgotFragment : BaseFragment() {

    private val viewModel: ForgotViewModel by viewModels()
    private var _binding: FragmentAuthForgotBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthForgotBinding.inflate(inflater, container, false)
        binding.forgot.setOnClickListener {
            if (
                Regex("^[+]?[0-9]{8,15}\$")
                    .containsMatchIn(binding.inpurNumberOrMail.text.toString())
                ||
                android.util.Patterns.EMAIL_ADDRESS
                    .matcher(binding.inpurNumberOrMail.text.toString()).matches()
            ) {
                viewModel.verify(binding.inpurNumberOrMail.text.toString().trim())
                viewModel.success.observe(viewLifecycleOwner) {
                    if (it && findNavController().currentDestination?.id == R.id.forgotFragment) {
                        findNavController().navigate(
                            if (android.util.Patterns
                                    .EMAIL_ADDRESS
                                    .matcher(binding.inpurNumberOrMail.text.toString())
                                    .matches()
                            )
                                R.id.action_forgotFragment_to_forgotCodeConfirmationMailFragment
                            else
                                R.id.action_forgotFragment_to_confirmRegistrationFragment
                        )
                    }
                }
            } else binding.parent.showSnackBar("Введены некорректные данные")
        }
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.parent.setOnTouchListener { view, _ ->
            requireActivity().hideKeyboardOnScroll(view)
        }
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

}