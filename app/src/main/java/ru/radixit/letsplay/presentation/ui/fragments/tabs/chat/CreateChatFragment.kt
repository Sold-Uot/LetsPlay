package ru.radixit.letsplay.presentation.ui.fragments.tabs.chat

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragmentCreateChatBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.CreateEventViewModel
import ru.radixit.letsplay.utils.hideKeyboardOnScroll

/**
 * Экран создание чата
 */
@AndroidEntryPoint
class CreateChatFragment : BaseFragment() {

    private var _binding: FragmentCreateChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreateChatViewModel by viewModels()
    private lateinit var viewModelEv: CreateEventViewModel
    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uri: List<Uri> ->
            uploadPhoto(uri)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModelEv = ViewModelProvider(requireActivity())[CreateEventViewModel::class.java]

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
                viewModel.create(title = binding.editProfileName.text.toString())
            }
        }



        binding.addPhoto.setOnClickListener {
            selectImageFromGallery()
        }

        binding.addMembers.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.createChatFragment) {
                findNavController().navigate(R.id.action_createChatFragment_to_choiceUsersForEventFragment2)
            }
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

    override fun onStart() {
        super.onStart()



        viewModelEv.selectedUsers.observe(viewLifecycleOwner) {
            binding.countFriends.text = it.size.toString()
        }

        /*viewModel.selectedUsers.observe(viewLifecycleOwner) {
            Log.e("list_users12 2" , it.size.toString())
            binding.countFriends.text = it.size.toString()
        }
        viewModel.chatUsers.observe(viewLifecycleOwner){
            Log.e("list_users1" , it.size.toString())
            binding.countFriends.text = it.size.toString()
        }*/


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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

}