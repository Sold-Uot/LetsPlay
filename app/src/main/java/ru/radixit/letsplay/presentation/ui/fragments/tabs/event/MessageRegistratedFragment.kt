package ru.radixit.letsplay.presentation.ui.fragments.tabs.event

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.FragmentMessageRegistratedBinding


@AndroidEntryPoint
class MessageRegistratedFragment : DialogFragment() {

    private var _binding: FragmentMessageRegistratedBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageRegistratedBinding.inflate(inflater, container, false)

        binding.okayTv.setOnClickListener {
            findNavController().navigate(MessageRegistratedFragmentDirections.actionMessageRegistratedFragmentToListEventsRedesFrag())
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

}