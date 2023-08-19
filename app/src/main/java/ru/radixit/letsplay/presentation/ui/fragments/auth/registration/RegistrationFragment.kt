package ru.radixit.letsplay.presentation.ui.fragments.auth.registration

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_auth_registration.checkBox
import kotlinx.android.synthetic.main.fragment_auth_registration.login
import kotlinx.android.synthetic.main.fragment_auth_registration.password
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.network.request.RegistrationRequest
import ru.radixit.letsplay.databinding.FragmentAuthRegistrationBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.maskForNumberPhone
import ru.radixit.letsplay.utils.showSnackBar
import ru.radixit.letsplay.utils.visible
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Экран регистрации
 */
@AndroidEntryPoint
class RegistrationFragment : BaseFragment() {

    private lateinit var registrationViewModel: RegistrationViewModel
    private var _binding: FragmentAuthRegistrationBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthRegistrationBinding.inflate(inflater, container, false)
        registrationViewModel =
            ViewModelProvider(requireActivity())[RegistrationViewModel::class.java]

        val numberPhone = binding.numberPhone
        numberPhone.maskForNumberPhone()
        maskForBirthDateLocal()
        checkPassword()
        checkLogin()
        binding.enter.setOnClickListener {
            findNavController().popBackStack()
        }
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return binding.root
    }

    private fun maskForBirthDateLocal() {
        val slots = UnderscoreDigitSlotsParser().parseSlots("__-__-____")
        val mask = MaskImpl.createTerminated(slots)
        mask.isHideHardcodedHead = true
        val formatWatcher: FormatWatcher = MaskFormatWatcher(
            mask
        )
        formatWatcher.installOn(binding.birthYear)
        binding.birthYear.doAfterTextChanged {
            if (it.toString().length != 10) {
                binding.materialCardView4.endIconMode = TextInputLayout.END_ICON_NONE
                binding.materialCardView4.error = "Дата не действительна"
            } else {
                binding.materialCardView4.endIconMode = TextInputLayout.END_ICON_CUSTOM
                binding.materialCardView4.setEndIconTintList(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.main_gray
                    )
                )
                binding.materialCardView4.endIconDrawable =
                    ResourcesCompat.getDrawable(resources, R.drawable.baseline_calendar_month, null)
                binding.materialCardView4.error = null
                try {
                    val sdf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    LocalDate.parse(it.toString(), sdf)
                } catch (e: DateTimeParseException) {
                    binding.materialCardView4.error = "Дата не действительна"
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickContinueButton()
        registrationViewModel.progressBarVisibility.observe(viewLifecycleOwner) {
            showLoadingProgress(
                it
            )
        }
    }

    private fun checkLogin() {
        binding.login.doAfterTextChanged {
            if (it?.length == 0) {
                binding.errorLoginEd.gone()
                binding.materialCardView.endIconMode = TextInputLayout.END_ICON_NONE
            } else if (!it!!.contains(Regex("^[[a-z]][[a-z0-9]|\\\\d|[_]]{3,15}\$"))) {
                binding.errorLoginEd.visible()
                binding.errorLoginEd.text =
                    "Логин может содержать только буквы латиницы нижнего регистра, цифры и символ нижнего подчёркивания. Длина логина должна быть от 4 до 15 символов"
                binding.errorLoginEd.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red_error
                    )
                )
                binding.materialCardView.boxStrokeColor = ContextCompat.getColor(requireContext(),R.color.red_error)
                binding.materialCardView.endIconMode = TextInputLayout.END_ICON_CUSTOM
                binding.materialCardView.setEndIconTintList(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.red_error
                    )
                )
                binding.materialCardView.endIconDrawable =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_error_ed, null)
            } else {
                binding.materialCardView.boxStrokeColor = ContextCompat.getColor(requireContext(),R.color.green_check)
                binding.errorLoginEd.visible()
                binding.errorLoginEd.text = "Логин свободен"
                binding.errorLoginEd.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green_check
                    )
                )
                binding.materialCardView.endIconMode = TextInputLayout.END_ICON_CUSTOM
                binding.materialCardView.setEndIconTintList(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.green_check
                    )
                )
                binding.materialCardView.endIconDrawable =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_check_green, null)
            }
        }
    }

    private fun checkPassword() {
        binding.password.doAfterTextChanged {
            if (it?.length == 0) {
                binding.errorPassEd.gone()
                binding.materialCardView3.endIconMode = TextInputLayout.END_ICON_NONE
            } else if (it!!.length < 8) {
                binding.errorPassEd.visible()
                binding.errorPassEd.text = "Пароль не может быть короче 8 символов"
                binding.errorPassEd.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red_error
                    )
                )
                binding.materialCardView3.endIconMode = TextInputLayout.END_ICON_CUSTOM
                binding.materialCardView3.setEndIconTintList(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.red_error
                    )
                )
                binding.materialCardView3.boxStrokeColor = ContextCompat.getColor(requireContext(),R.color.red_error)
                binding.materialCardView3.endIconDrawable =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_error_ed, null)
            } else {
                binding.errorPassEd.visible()
                binding.errorPassEd.text = "Надежный пароль"
                binding.errorPassEd.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green_check
                    )
                )
                binding.materialCardView3.boxStrokeColor = ContextCompat.getColor(requireContext(),R.color.green_check)
                binding.materialCardView3.endIconMode = TextInputLayout.END_ICON_CUSTOM
                binding.materialCardView3.setEndIconTintList(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.green_check
                    )
                )
                binding.materialCardView3.endIconDrawable =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_check_green, null)
            }
        }
    }

    @SuppressLint("NewApi")
    private fun clickContinueButton() {
        binding.continueButton.setOnClickListener {
            with(binding) {
                registrationViewModel.successVerify.observe(
                    viewLifecycleOwner
                ) {
                    if (it) {
                        findNavController().navigate(
                            RegistrationFragmentDirections.actionRegistrationFragmentToConfirmRegistrationFragment(
                                numberPhone.text.toString()
                            )
                        )
                    } else {
                        registrationLayout.showSnackBar("Этот номер уже используется")
                    }
                }
                if (isInvalidate()) {
                    if (checkBox.isChecked) {
                        registrationViewModel.checkUsername(binding.login.text.toString()).apply {
                            registrationViewModel.checkUsernameVerify.observe(viewLifecycleOwner) { checkUsername ->
                                if (checkUsername) {
                                    registrationViewModel.verify(
                                        numberPhone
                                            .text
                                            .toString()
                                            .filterNot {
                                                it == '-' || it == ' ' || it == '(' || it == ')'
                                            }
                                    ).apply {
                                    registrationViewModel.saveRegisterData(
                                        RegistrationRequest(
                                            birthDate = birthYear.text.toString(),
                                            name = name.text.toString(),
                                            password = password.text.toString(),
                                            username = login.text.toString()
                                        )
                                    )
                                    }
                                } else {
                                    registrationLayout.showSnackBar("Имя пользователя уже занято")
                                }
                            }
                        }
                    } else {
                        registrationLayout.showSnackBar("Примите условия пользовательского соглашения")
                    }
                } else {
                    registrationLayout.showSnackBar("Поля не могут быть пустыми")
                }
            }
        }
    }

    private fun isInvalidate(): Boolean {
        with(binding) {
            return !TextUtils.isEmpty(name.text.toString()) || !TextUtils.isEmpty(birthYear.text.toString()) || !TextUtils.isEmpty(
                login.text.toString()
            ) || !TextUtils.isEmpty(numberPhone.text.toString()) || !TextUtils.isEmpty(
                password.text.toString()
            )
        }
    }

    private fun showLoadingProgress(show: Boolean) {
        if (show) {
            binding.progressBar2.visibility = View.VISIBLE
            binding.continueButton.text = ""
        } else {
            binding.progressBar2.visibility = View.GONE
            binding.continueButton.text = resources.getString(R.string.continue_registry)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}