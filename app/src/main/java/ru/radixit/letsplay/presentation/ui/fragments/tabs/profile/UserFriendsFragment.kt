package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.radixit.letsplay.databinding.FragmentUserFriendsBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.friends.FriendsAdapter
import ru.radixit.letsplay.utils.SpaceItemDecoration

@AndroidEntryPoint
class UserFriendsFragment : BaseFragment() {

    private var _binding: FragmentUserFriendsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UserProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserFriendsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[UserProfileViewModel::class.java]
        setupRecyclerview()
        return binding.root
    }

    private fun setupRecyclerview() {
        val friendRecyclerView = binding.recyclerView
        friendRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val adapter = FriendsAdapter()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.listFriends("", viewModel.vUserId!!).collectLatest {
               adapter.submitData(it)
            }
        }
        friendRecyclerView.adapter = adapter
        friendRecyclerView.addItemDecoration(SpaceItemDecoration(30))
        adapter.selectItem {

        }
    }

}