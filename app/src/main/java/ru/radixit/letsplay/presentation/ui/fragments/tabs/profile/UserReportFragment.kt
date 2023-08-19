package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.FragmentReportBinding
import ru.radixit.letsplay.utils.showSnackBar

@AndroidEntryPoint
class UserReportFragment : DialogFragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<UserReportFragmentArgs>()
    private lateinit var viewModel: UserProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[UserProfileViewModel::class.java]
        val radioButtons = arrayListOf(
            binding.optionOne,
            binding.optionTwo,
            binding.optionThree,
            binding.optionFour,
            binding.optionFive
        )
        radioButtons.forEachIndexed { index, radioButton ->
            radioButton.setOnClickListener {
                binding.report.isVisible = index == radioButtons.lastIndex
                binding.buttonReport.setOnClickListener {
                    if (index == radioButtons.lastIndex) {
                        if (radioButton.text.toString().isNotEmpty()) {
                            viewModel.report(
                                args.id,
                                if (index == radioButtons.lastIndex) 0 else index + 1,
                                radioButton.text.toString()
                            )
                        } else {
                            binding.reportContainer
                                .showSnackBar("Поле ввода не может быть пустым")
                        }
                    } else {
                        viewModel.report(
                            args.id,
                            if (index == radioButtons.lastIndex) 0 else index + 1
                        )
                    }
                }
            }
        }
        viewModel.textMessage.observe(viewLifecycleOwner) {
            binding.reportContainer.showSnackBar(it)
        }
        viewModel.success.observe(viewLifecycleOwner) {
            if (it) {
                // dismiss()
            }
        }
        binding.cancel.setOnClickListener { dismiss() }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

}