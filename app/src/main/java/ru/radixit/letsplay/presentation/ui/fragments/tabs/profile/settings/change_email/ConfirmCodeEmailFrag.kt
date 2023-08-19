package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings.change_email

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.DialogChangeEmailRedesBinding
import ru.radixit.letsplay.databinding.FragConfirmCodeEmailRedesBinding

@AndroidEntryPoint
class ConfirmCodeEmailFrag : Fragment() {
    private lateinit var binding:FragConfirmCodeEmailRedesBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragConfirmCodeEmailRedesBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showDialog()
    }
    private fun showDialog() {
        val customDialog = Dialog(requireActivity())
        val bindingDialog = DialogChangeEmailRedesBinding.inflate(layoutInflater)
        customDialog.setContentView(bindingDialog.root)
        bindingDialog.okayText.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.show()
    }
}