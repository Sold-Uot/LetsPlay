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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.FragmentRemoveFromFriendsBinding
import ru.radixit.letsplay.utils.showToast

@AndroidEntryPoint
class RemoveFromFriendsFragment : DialogFragment() {

    private var _binding: FragmentRemoveFromFriendsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RemoveFromFriendsViewModel by viewModels()
    private val args by navArgs<RemoveFromFriendsFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRemoveFromFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.cancelText.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.cancel.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.textView38.text = "Вы точно хотите удалить ${args.name} из списка друзей?"
        binding.buttonReport.setOnClickListener {
            viewModel.removeFromFriends(args.id)
        }
        viewModel.success.observe(viewLifecycleOwner) {
            if (it) {
                requireContext().showToast("Пользователь ${args.name} удалён из списка друзей")
                dismiss()
            } else {
                requireContext().showToast("Что-то пошло не так")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}