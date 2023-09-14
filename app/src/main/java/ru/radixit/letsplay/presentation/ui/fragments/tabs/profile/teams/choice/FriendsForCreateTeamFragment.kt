package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams.choice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragmentFriendsForCreateTeamBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes.FriendsForEventAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams.CreateTeamViewModel
import ru.radixit.letsplay.utils.SpaceItemDecoration
import ru.radixit.letsplay.utils.hideKeyboardOnScroll

@AndroidEntryPoint

class FriendsForCreateTeamFragment : BaseFragment() {

    private var _binding: FragmentFriendsForCreateTeamBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<CreateTeamViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFriendsForCreateTeamBinding.inflate(inflater , container,false)

        setupRecyclerview()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupRecyclerview() {
        val recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = FriendsForEventAdapter()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchFriends().collectLatest {
                adapter.submitData(it)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SpaceItemDecoration(40))
        binding.searchPlayers.doAfterTextChanged { text ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.searchFriends(text.toString()).collectLatest {
                    adapter.submitData(it)
                }
            }
        }
        adapter.selectItem {
            viewModel.add(it)
        }
        adapter.removeItem {
            viewModel.remove(it)
        }
        adapter.addLoadStateListener {
            binding.foundNumber.text = "Найдено: ${adapter.itemCount}"
        }
        recyclerView.setOnTouchListener { view, _ ->
            requireContext().hideKeyboardOnScroll(view)
        }
    }


}