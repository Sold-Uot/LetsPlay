package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams.choice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.FragmentSelectedUsersForCreateTeamBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams.CreateTeamViewModel
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams.choice.adapter.SelectUserFoeCreateTeamAdapter
import ru.radixit.letsplay.utils.SpaceItemDecoration
import ru.radixit.letsplay.utils.hideKeyboardOnScroll

@AndroidEntryPoint

class SelectedUsersForCreateTeamFragment : BaseFragment() {

    private var _binding: FragmentSelectedUsersForCreateTeamBinding? = null
    private val binding get() = _binding!!

    private val adapter = SelectUserFoeCreateTeamAdapter()
    private var firstStart = true

    private lateinit var viewModel: CreateTeamViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentSelectedUsersForCreateTeamBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[CreateTeamViewModel::class.java]







        return binding.root
    }

    override fun onResume() {
        super.onResume()
        recyclerSetup()
    }

    fun recyclerSetup() {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.fetchSelectUserList()
        viewModel.selectedUsers.observe(viewLifecycleOwner) {
            adapter.setData(it)
            binding.foundNumber.text = "Найдено: ${it.size}"
        }

        adapter.selectItem {
            viewModel.remove(it)
        }
        recyclerView.adapter = adapter
        if(firstStart){
            recyclerView.addItemDecoration(SpaceItemDecoration(40))

            firstStart = false
        }

        recyclerView.setOnTouchListener { view, _ ->
            requireContext().hideKeyboardOnScroll(view)
        }
    }


}