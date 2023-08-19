package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.radixit.letsplay.databinding.FragmentFriendsForEventBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes.FriendsForEventAdapter
import ru.radixit.letsplay.utils.SpaceItemDecoration
import ru.radixit.letsplay.utils.hideKeyboardOnScroll

@AndroidEntryPoint
class FriendsForEventFragment : BaseFragment() {

    private var _binding: FragmentFriendsForEventBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreateEventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsForEventBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[CreateEventViewModel::class.java]
        setupRecyclerview()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
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