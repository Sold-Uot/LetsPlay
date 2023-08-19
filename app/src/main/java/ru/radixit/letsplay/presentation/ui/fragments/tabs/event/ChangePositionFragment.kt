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
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragmentChangePositionBinding
import ru.radixit.letsplay.utils.showSnackBar

@AndroidEntryPoint
class ChangePositionFragment : DialogFragment() {

    private var _binding: FragmentChangePositionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EventViewModel by viewModels()
    private val args by navArgs<ChangePositionFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePositionBinding.inflate(inflater, container, false)
        binding.editPosition.setOnClickListener {
            viewModel.changePositionOnField()
            setupPosition()
        }
        binding.confirm.setOnClickListener {
            with(viewModel) {
                if (binding.editPosition.text.toString() != "") {
                    position.observe(viewLifecycleOwner) {
                        viewModel.join(id = args.id, it)
                    }
                    success.observe(viewLifecycleOwner) {
                        if (it && findNavController().currentDestination?.id == R.id.changePositionFragment) {
                            findNavController().navigate(R.id.action_changePositionFragment_to_messageRegistratedFragment)
                        }
                    }
                    textMessage.observe(viewLifecycleOwner) {
                        binding.container.showSnackBar(it)
                    }
                }else{
                    binding.container.showSnackBar("Выберите позицию")
                }
            }
        }
        binding.cancel.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    private fun setupPosition() {
        viewModel.textPosition.observe(viewLifecycleOwner) {
            binding.editPosition.text = it
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

}