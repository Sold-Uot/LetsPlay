package ru.radixit.letsplay.presentation.ui.fragments.auth.registration

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.network.request.RegistrationRequest
import ru.radixit.letsplay.databinding.FragmentAuthRegistrationConfirmBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.utils.hideKeyboardOnScroll
import ru.radixit.letsplay.utils.maskForNumberPhone

/**
 * Экран подтверждение регистрации
 */
@AndroidEntryPoint
class ConfirmRegistrationFragment : BaseFragment() {

    private var _binding: FragmentAuthRegistrationConfirmBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RegistrationViewModel
    private val args by navArgs<ConfirmRegistrationFragmentArgs>()

    @SuppressLint("ClickableViewAccessibility", "LongLogTag")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthRegistrationConfirmBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[RegistrationViewModel::class.java]
        binding.phoneNumber.text = args.numberPhone
        val pinEntry = binding.pinEntryEditText
        pinEntry.setOnPinEnteredListener { str ->
                viewModel.registrationRequest.observe(viewLifecycleOwner) {
                    viewModel.register(
                        RegistrationRequest(
                            birthDate = it.birthDate,
                            name = it.name,
                            password = it.password,
                            username = it.username,
                            code = pinEntry.text.toString()
                        )
                    )
                }
        }
        binding.backStepFrag.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.changeNumber.setOnClickListener {
            showDialog()
        }
        viewModel.successRegistry.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.rootFragment)
            } else {
                findNavController().popBackStack()
            }
        }
        pinEntry.doAfterTextChanged {
            if (it!!.length == 4) {
                binding.confirmButton.text = "ПРОДОЛЖИТЬ"
            } else {
                binding.confirmButton.text = "ОТПРАВИТЬ КОД ЗАНОВО"
            }
        }
        binding.parent.setOnTouchListener { view, _ ->
            requireActivity().hideKeyboardOnScroll(view)
        }
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    private fun showDialog() {
        val customDialog = Dialog(requireActivity())
        customDialog.setContentView(R.layout.dialog_fragment_auth_forgot_change_number)
        val cancelBtn = customDialog.findViewById(R.id.cancel) as ImageView
        val confirmBtn = customDialog.findViewById(R.id.confirm_btn) as MaterialButton
        val numberPhone = customDialog.findViewById(R.id.phone_number) as EditText
        cancelBtn.setOnClickListener {
            customDialog.dismiss()
        }
        numberPhone.maskForNumberPhone()
        confirmBtn.setOnClickListener {
            val phoneNumber = numberPhone.text
            viewModel.successVerify.observe(viewLifecycleOwner){
                binding.phoneNumber.text = phoneNumber
                customDialog.dismiss()
            }
            viewModel.verify(
                numberPhone
                    .text
                    .toString()
                    .filterNot {
                        it == '-' || it == ' ' || it == '(' || it == ')'
                    }
            )
        }
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.show()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}