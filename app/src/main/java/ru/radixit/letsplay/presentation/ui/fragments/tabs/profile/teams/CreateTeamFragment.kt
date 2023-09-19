package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.FragmentCreatTeamBinding
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.showToast
import ru.radixit.letsplay.utils.visible

@AndroidEntryPoint
class CreateTeamFragment : Fragment() {

    private var _binding: FragmentCreatTeamBinding? = null
    private val binding get() = _binding!!

    private val vm by viewModels<CreateTeamViewModel>()
    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uri: List<Uri> ->
            uploadPhoto(uri)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatTeamBinding.inflate(inflater, container, false)





        return binding.root
    }

    override fun onResume() {
        super.onResume()
        settingsView()
    }

    private fun settingsView() {
        clickView()
        vm.fetchSelectUserList()
        vm.selectedUsers.observe(viewLifecycleOwner) {
            binding.countFriends.text = it.size.toString()
        }

    }

    private fun uploadPhoto(uri: List<Uri>) {
        vm.saveUri(uri)
        binding.photoProfile.visibility = View.VISIBLE
        vm.uriList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                Glide.with(binding.root).load(it[it.lastIndex]).into(binding.photoProfile)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun onBack(){
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun clickView() {
        onBack()

        vm.success.observe(viewLifecycleOwner) {

            if (it)
                findNavController().popBackStack()


        }
        vm.loading.observe(viewLifecycleOwner) {
            if (it) {
                binding.buttonProgressBar.visible()
                binding.createTeam.gone()
            }

        }
        binding.createTeamBtn.setOnClickListener {

            if (binding.editProfileName.text.isEmpty())
                context?.showToast("Название не должно быть пустым")
            else
                vm.createTeam(
                    title = binding.editProfileName.text.toString(),
                    nickname = binding.editNickName.text.toString()
                )
        }

        binding.addPhoto.setOnClickListener {
            selectImageFromGallery()
        }
        binding.addMembers.setOnClickListener {
            findNavController().navigate(CreateTeamFragmentDirections.actionCreateTeamFragmentToChoiceUsersForCreateTeamFragment())

        }

    }
}


