package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragmentCreatTeamBinding

@AndroidEntryPoint
class CreateTeamFragment : Fragment() {

    private var _binding : FragmentCreatTeamBinding? = null
    private val binding get() = _binding!!

    private val vm  by viewModels<CreateTeamViewModel>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatTeamBinding.inflate(inflater,container,false)





        return binding.root
    }

    override fun onResume() {
        super.onResume()
        settingsView()
    }

    private fun settingsView() {
        clickView()
        vm.fetchSelectUserList()
        vm.selectedUsers.observe(viewLifecycleOwner){
            Log.e("q3", it.size.toString())
            binding.countFriends.text = it.size.toString()
        }

    }

    private fun  clickView() {
        binding.createTeam.setOnClickListener {
            vm.createTeam(title = binding.editProfileName.text.toString(),"12133вфывв1233")
        }
        binding.addMembers.setOnClickListener {
            findNavController().navigate(CreateTeamFragmentDirections.actionCreateTeamFragmentToChoiceUsersForCreateTeamFragment())

        }
    }


}