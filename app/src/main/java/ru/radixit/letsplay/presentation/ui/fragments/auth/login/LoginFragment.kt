package ru.radixit.letsplay.presentation.ui.fragments.auth.login

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.global.SessionManager
import ru.radixit.letsplay.data.network.request.LoginRequest
import ru.radixit.letsplay.databinding.FragmentAuthLoginBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.utils.hideKeyboardOnScroll
import ru.radixit.letsplay.utils.showSnackBar
import javax.inject.Inject

/**
 * Экран авторизации
 */
@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private var _binding: FragmentAuthLoginBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthLoginBinding.inflate(inflater, container, false)
        binding.parent.setOnTouchListener { view, _ ->
            requireActivity().hideKeyboardOnScroll(view)
        }
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navigateOnRegistration(binding.registry1)
        fetchToken()
        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_forgotPasswordFragment)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun fetchToken() {
        binding.enterBtn.setOnClickListener {
            if (TextUtils.isEmpty(binding.parol.text) || TextUtils.isEmpty(binding.loginOrNumber.text)) {
                binding.parent.showSnackBar("Логин и пароль обязательны")
            } else {
                loginCheck()
            }
        }
    }

    private fun loginCheck() {
        with(authViewModel) {
            fetchToken(
                LoginRequest(
                    binding.loginOrNumber.text.toString(),
                    binding.parol.text.toString(),
                    sessionManager.fetchFirebaseToken()
                )
            )
            progressBarVisibility.observe(viewLifecycleOwner) { showLoadingProgress(it) }
            error.observe(viewLifecycleOwner) {
                if (it) {
                    binding.parent.showSnackBar("Введен неверный логин или пароль")
                }
            }
            success.observe(viewLifecycleOwner) { checkResponse(it) }
        }
    }

    private fun checkResponse(it: Boolean) {
        if (it && findNavController().currentDestination?.id == R.id.authFragment) {
            findNavController().navigate(R.id.action_authFragment_to_rootFragment)
        }
    }

    private fun navigateOnRegistration(registry: TextView) {
        registry.setOnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_registrationFragment)
        }
    }

    private fun showLoadingProgress(show: Boolean) {
        if (show) {
            binding.buttonProgressBar.visibility = View.VISIBLE
            binding.enterBtn.text = ""
        } else {
            binding.buttonProgressBar.visibility = View.GONE
            binding.enterBtn.text = resources.getString(R.string.enter)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}