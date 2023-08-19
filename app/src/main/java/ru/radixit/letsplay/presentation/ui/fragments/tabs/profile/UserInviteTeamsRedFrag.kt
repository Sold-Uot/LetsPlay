package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragListEventsRedesignBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.tabs.teams.TeamAdapter
import ru.radixit.letsplay.utils.SpaceItemDecoration

@AndroidEntryPoint
class UserInviteTeamsRedFrag : BaseFragment() {

    private var _binding: FragListEventsRedesignBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UserProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragListEventsRedesignBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[UserProfileViewModel::class.java]
        onBack()
        setupRecyclerview()
        switchBtn()
        return binding.root
    }

    private fun switchBtn() {
        with(binding){
            completeEvent.setOnClickListener {
                completeEvent.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white))
                currentEvent.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.background_for_edittext))
            }
            currentEvent.setOnClickListener {
                currentEvent.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white))
                completeEvent.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.background_for_edittext))
            }
        }
    }

    private fun setupRecyclerview() {
        val teamRecyclerView = binding.recyclerViewCurrent
        teamRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val adapter = TeamAdapter()
        viewModel.listTeams(viewModel.vUserId!!.toInt())
        viewModel.teams.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }
        teamRecyclerView.adapter = adapter
        teamRecyclerView.addItemDecoration(SpaceItemDecoration(30))
    }

    private fun onBack() {
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}