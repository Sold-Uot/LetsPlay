package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.friends

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.FragmentUserInBlackListDialogBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings.blackList.BlackListViewModel

@AndroidEntryPoint
class UserInBlackListDialogFragment : DialogFragment() {

    private var _binding: FragmentUserInBlackListDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BlackListViewModel
    private val args by navArgs<UserInBlackListDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserInBlackListDialogBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[BlackListViewModel::class.java]

        binding.cancel.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonReport.setOnClickListener {
            viewModel.unblock(args.id.toString())
            dismiss()
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}