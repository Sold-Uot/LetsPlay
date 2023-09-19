package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.FragSearchListMapRedesBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.UserProfileViewModel
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams.TeamAdapter
import ru.radixit.letsplay.utils.SpaceItemDecoration

@AndroidEntryPoint
class ListTeamsRedesFrag : BaseFragment() {

    private var _binding: FragSearchListMapRedesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UserProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragSearchListMapRedesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[UserProfileViewModel::class.java]
        setupRecyclerview()
        binding.swipeToRefresh.setOnRefreshListener {
            setupRecyclerview()
        }
        return binding.root
    }


    private fun setupRecyclerview() {
        val teamRecyclerView = binding.recyclerView
        teamRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val adapter = TeamAdapter()
        viewModel.listTeams(viewModel.vUserId!!.toInt())
        viewModel.teams.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }
        teamRecyclerView.adapter = adapter
        teamRecyclerView.addItemDecoration(SpaceItemDecoration(30))
        if(binding.swipeToRefresh.isRefreshing){
            binding.swipeToRefresh.isRefreshing = false
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}