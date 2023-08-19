package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.DialogExitProfileRedesBinding
import ru.radixit.letsplay.databinding.FragHistoryActiveBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings.SettingsViewModel

@AndroidEntryPoint
class HistoryActiveFrag : Fragment() {
    private lateinit var binding:FragHistoryActiveBinding
    private val viewModel: SettingsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragHistoryActiveBinding.inflate(layoutInflater)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar2.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.finishActivateTv.setOnClickListener {
            showDialog()
        }
    }
    private fun showDialog() {
        val customDialog = Dialog(requireActivity())
        val bindingDialog = DialogExitProfileRedesBinding.inflate(layoutInflater)
        customDialog.setContentView(bindingDialog.root)
        bindingDialog.cancelText.setOnClickListener {
            customDialog.dismiss()
        }
        bindingDialog.exitText.text = "Завершить"
        bindingDialog.exitText.setOnClickListener {
        }
        customDialog.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.show()
    }
}