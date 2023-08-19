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
import ru.radixit.letsplay.databinding.FragmentAuthForgotNewPasswordBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.utils.hideKeyboardOnScroll
import ru.radixit.letsplay.utils.showSnackBar

/**
 * Экран новый пароль
 */
@AndroidEntryPoint
class ForgotNewPasswordFragment : BaseFragment() {

    private val viewModel: ForgotViewModel by viewModels()
    private var _binding: FragmentAuthForgotNewPasswordBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthForgotNewPasswordBinding.inflate(inflater, container, false)
        binding.confirm?.setOnClickListener {
            if (binding.newPassword.text.toString() == binding.newPassword2.text.toString()) {
                if (binding.newPassword.text.toString().isNotEmpty()
                    ||
                    binding.newPassword2.text.toString().isNotEmpty()
                ) {
                    if (binding.newPassword.text.toString().length > 8 || binding.newPassword2.text.toString().length > 8) {
                        viewModel.password(binding.newPassword.text.toString())
                        findNavController().navigate(R.id.action_forgotNewPasswordFragment_to_rootFragment)
                    } else {
                        binding.parent.showSnackBar("Пароль не может быть короче 8 символов")
                    }
                } else {
                    binding.parent.showSnackBar("Поля не могут быть пустыми")
                }
            } else {
                binding.parent.showSnackBar("Пароли не совпадают")
            }
        }
        binding.parent.setOnTouchListener { view, _ ->
            requireActivity().hideKeyboardOnScroll(view)
        }
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }
}