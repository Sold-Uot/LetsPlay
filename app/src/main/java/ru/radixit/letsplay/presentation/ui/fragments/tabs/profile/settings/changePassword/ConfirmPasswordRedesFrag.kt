package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings.changePassword

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.DialogChangeEmailRedesBinding
import ru.radixit.letsplay.databinding.FragConfirmPasswordRedesBinding
import ru.radixit.letsplay.presentation.ui.fragments.auth.forgot.ForgotViewModel
import ru.radixit.letsplay.utils.showSnackBar

@AndroidEntryPoint
class ConfirmPasswordRedesFrag : Fragment() {
    private val viewModel: ForgotViewModel by viewModels()
    private lateinit var binding:FragConfirmPasswordRedesBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragConfirmPasswordRedesBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.confirmButton.setOnClickListener {
            if (binding.newPasswordEd.text.toString() == binding.confirmNewPasswordEd.text.toString()) {
                if (binding.newPasswordEd.text.toString().isNotEmpty()
                    ||
                    binding.confirmNewPasswordEd.text.toString().isNotEmpty()
                ) {
                    if (binding.newPasswordEd.text.toString().length > 8 || binding.confirmNewPasswordEd.text.toString().length > 8) {
                        viewModel.password(binding.newPasswordEd.text.toString())
                        showDialog()
                    } else {
                        binding.root.showSnackBar("Пароль не может быть короче 8 символов")
                    }
                } else {
                    binding.root.showSnackBar("Поля не могут быть пустыми")
                }
            } else {
                binding.root.showSnackBar("Пароли не совпадают")
            }
        }
    }
    private fun showDialog() {
        val customDialog = Dialog(requireActivity())
        val bindingDialog = DialogChangeEmailRedesBinding.inflate(layoutInflater)
        customDialog.setContentView(bindingDialog.root)
        bindingDialog.okayText.setOnClickListener {
            customDialog.dismiss()
        }
        bindingDialog.titleTv.text = "Пароль был изменен"
        bindingDialog.descTv.text = "Старый пароль был успешно изменен на\nновый."
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.show()
    }
}