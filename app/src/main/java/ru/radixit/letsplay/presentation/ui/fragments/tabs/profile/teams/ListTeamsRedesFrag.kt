package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.radixit.letsplay.databinding.FragListTeamsRedesBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.UserProfileViewModel
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter.ListTeamRedesAdapter
import ru.radixit.letsplay.utils.SpaceItemDecoration
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.visible

@AndroidEntryPoint
class ListTeamsRedesFrag : BaseFragment() {

    private var _binding: FragListTeamsRedesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UserProfileViewModel
    private val args by navArgs<ListTeamsRedesFragArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragListTeamsRedesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[UserProfileViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBack()
        setupRecyclerview()

        binding.swipeToRefresh.setOnRefreshListener {
            setupRecyclerview()
        }
    }

    private fun setupRecyclerview() {
        val teamRecyclerView = binding.recyclerView
        teamRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val adapter = ListTeamRedesAdapter()
        viewModel.listTeams(args.id)
        viewModel.teams.observe(viewLifecycleOwner) {
            adapter.setData(it)
            binding.foundCountTv.text = "${it.size}"
        }
        viewModel.fetchProfile(args.id.toString())
        viewModel.profile.observe(viewLifecycleOwner) {
            if (it.name.equals("")) {
                binding.nameTeamsTv.gone()
                val params = binding.titleListTeamsTv.layoutParams as ConstraintLayout.LayoutParams
                params.bottomToBottom = ConstraintSet.PARENT_ID
                binding.titleListTeamsTv.layoutParams = params
            } else {
                binding.nameTeamsTv.text = it.name
                val params = binding.titleListTeamsTv.layoutParams as ConstraintLayout.LayoutParams
                params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                binding.titleListTeamsTv.layoutParams = params
            }
        }
        teamRecyclerView.adapter = adapter
        teamRecyclerView.addItemDecoration(SpaceItemDecoration(30))
        with(binding) {
//            searchPlayers.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//                override fun onQueryTextSubmit(query: String?): Boolean {
//                    viewLifecycleOwner.lifecycleScope.launch {
//
//                    }
//                    return true
//                }
//
//                override fun onQueryTextChange(newText: String?): Boolean {
//                    return true
//                }
//            })
//            searchPlayers.setOnSearchClickListener {
//                val params = searchPlayers.layoutParams as ConstraintLayout.LayoutParams
//                params.startToStart = ConstraintSet.PARENT_ID
//                searchPlayers.layoutParams = params
//                titleListTeamsTv.gone()
//                nameTeamsTv.gone()
//            }
//
//            searchPlayers.setOnCloseListener(object : SearchView.OnCloseListener {
//                override fun onClose(): Boolean {
//                    val params = searchPlayers.layoutParams as ConstraintLayout.LayoutParams
//                    params.startToStart = ConstraintLayout.LayoutParams.UNSET
//                    searchPlayers.layoutParams = params
//                    titleListTeamsTv.visible()
//                    nameTeamsTv.visible()
//                    return false
//                }
//            })
        }
        if (binding.swipeToRefresh.isRefreshing) {
            binding.swipeToRefresh.isRefreshing = false
        }
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