package ru.radixit.letsplay.presentation.ui.fragments.tabs.chat

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragmentCreateChatBinding
import ru.radixit.letsplay.utils.hideKeyboardOnScroll

/**
 * Экран создание чата
 */
@AndroidEntryPoint
class CreateChatFragment : DialogFragment() {

    private var _binding: FragmentCreateChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreateChatViewModel by viewModels()
    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uri: List<Uri> ->
            uploadPhoto(uri)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.FullScreenDialogStyle
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateChatBinding.inflate(inflater, container, false)
        binding.createTeamBtn.isEnabled = false
        binding.editProfileName.doAfterTextChanged {
            viewModel.saveEdit(binding.editProfileName.text.toString())
        }
        viewModel.edit.observe(viewLifecycleOwner) {
            binding.createTeamBtn.isEnabled = it.isNotEmpty()
        }
        binding.createTeamBtn.setOnClickListener {
            if (binding.editProfileName.text.isNotEmpty()) {
                viewModel.create(binding.editProfileName.text.toString())
            }
        }

        binding.addPhoto.setOnClickListener {
            selectImageFromGallery()
        }

        binding.addMembers.setOnClickListener {
//            if (findNavController().currentDestination?.id == R.id.createChatFragment) {
//                findNavController().navigate(R.id.action_createChatFragment_to_choiceUsersForEventFragment2)
//            }
        }
        binding.root.setOnTouchListener { view, _ ->
            requireActivity().hideKeyboardOnScroll(view)
        }
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    private fun uploadPhoto(uri: List<Uri>) {
        viewModel.saveUri(uri)
        binding.photoProfile.visibility = View.VISIBLE
        viewModel.uriList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                Glide.with(binding.root).load(it[it.lastIndex]).into(binding.photoProfile)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

}