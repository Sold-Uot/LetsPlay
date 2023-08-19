package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.friends.findFriend

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragmentFindFriendBinding
import ru.radixit.letsplay.utils.SpaceItemDecoration
import ru.radixit.letsplay.utils.hideKeyboardOnScroll

@AndroidEntryPoint
class FindFriendFragment : DialogFragment() {

    private var _binding: FragmentFindFriendBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FindFriendViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.FullScreenDialogStyle
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindFriendBinding.inflate(inflater, container, false)
        onBack()
        setupRecyclerview()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupRecyclerview() {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = FindFriendAdapter()
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SpaceItemDecoration(40))
        binding.searchPlayers.doAfterTextChanged { text ->
            viewLifecycleOwner.lifecycleScope.launch {
                if (text?.isNotEmpty() == true) {
                    viewModel.searchUsers(text.toString()).collect {
                        adapter.submitData(it)
                    }
                } else {
                    adapter.submitData(PagingData.empty())
                }
            }
        }
        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.source.refresh is LoadState.NotLoading && adapter.itemCount == 0
                binding.emptyList.isVisible = isListEmpty
                recyclerView.isVisible =
                    loadState.source.refresh is LoadState.NotLoading
                binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                binding.retryButton.isVisible =
                    loadState.source.refresh is LoadState.Error && adapter.itemCount == 0
            }
        }
        binding.swipeToRefresh.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                if (binding.searchPlayers.text.isNotEmpty()) {
                    viewModel.searchUsers(binding.searchPlayers.text.toString()).collect {
                        adapter.submitData(it)
                    }
                } else {
                    adapter.submitData(PagingData.empty())
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                adapter.loadStateFlow.collect { loadState ->
                    binding.emptyList.isVisible =
                        loadState.source.refresh is LoadState.NotLoading && adapter.itemCount == 0
                    recyclerView.isVisible =
                        loadState.source.refresh is LoadState.NotLoading
                    binding.swipeToRefresh.isRefreshing =
                        loadState.source.refresh is LoadState.Loading
                    binding.progressBar.isVisible = false
                    binding.retryButton.isVisible =
                        loadState.source.refresh is LoadState.Error && adapter.itemCount == 0
                }
            }
        }

        binding.retryButton.setOnClickListener {
            adapter.refresh()
        }


        adapter.selectItem {
            if (findNavController().currentDestination?.id == R.id.findFriendFragment) {
//                findNavController().navigate(
//                    FindFriendFragmentDirections.actionFindFriendFragmentToUserProfileFragment(
//                        it.id.toString()
//                    )
//                )
            }
        }
        recyclerView.setOnTouchListener { view, _ ->
            requireContext().hideKeyboardOnScroll(view)
        }
        adapter.addLoadStateListener {
            binding.foundNumber.text = "Найдено: ${adapter.itemCount}"
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