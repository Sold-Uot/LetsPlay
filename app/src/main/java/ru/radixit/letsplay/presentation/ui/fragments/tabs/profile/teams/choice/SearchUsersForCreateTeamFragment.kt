package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams.choice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.model.UserEntity
import ru.radixit.letsplay.databinding.FragmentSearchUsersForCreateTeamBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes.FriendsForEventAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams.CreateTeamViewModel
import ru.radixit.letsplay.utils.SpaceItemDecoration
import ru.radixit.letsplay.utils.hideKeyboardOnScroll

@AndroidEntryPoint

class SearchUsersForCreateTeamFragment : BaseFragment() {
    private var _binding: FragmentSearchUsersForCreateTeamBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<CreateTeamViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentSearchUsersForCreateTeamBinding.inflate(inflater, container, false)

        setupRecyclerview()

        return binding.root
    }
    private fun setupRecyclerview() {
        val recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = FriendsForEventAdapter()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchUsers().collectLatest {
                adapter.submitData(it)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SpaceItemDecoration(40))
        binding.searchPlayers.doAfterTextChanged { text ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.searchUsers(text.toString()).collectLatest {
                    adapter.submitData(it)
                }
            }
        }
        recyclerView.setOnTouchListener { view, motionEvent ->
            requireContext().hideKeyboardOnScroll(view)
        }
        adapter.addLoadStateListener {
            binding.foundNumber.text = "Найдено: ${adapter.itemCount}"
        }
        adapter.selectItem {
            viewModel.add(
                UserEntity(
                id_user = it.id,
                name = it.name,
                photo_url = it.photo?.url,
                photo_id = it.photo?.id,
                surname = it.surname,
                userType = it.userType,
                username = it.username)
            )
        }
        adapter.removeItem {
            viewModel.remove(UserEntity(
                id_user = it.id,
                name = it.name,
                photo_url = it.photo?.url,
                photo_id = it.photo?.id,
                surname = it.surname,
                userType = it.userType,
                username = it.username))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}