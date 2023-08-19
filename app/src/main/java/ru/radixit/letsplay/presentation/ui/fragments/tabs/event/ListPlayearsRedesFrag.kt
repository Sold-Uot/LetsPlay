package ru.radixit.letsplay.presentation.ui.fragments.tabs.event

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
import ru.radixit.letsplay.databinding.FragSearchListPlayersRedesBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.CreateEventViewModel
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes.PlayersForEventRedesAdapter
import ru.radixit.letsplay.utils.SpaceItemDecoration
import ru.radixit.letsplay.utils.hideKeyboardOnScroll

@AndroidEntryPoint
class ListPlayearsRedesFrag : BaseFragment() {

    private var _binding: FragSearchListPlayersRedesBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy { PlayersForEventRedesAdapter() }
    private lateinit var viewModel: CreateEventViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragSearchListPlayersRedesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[CreateEventViewModel::class.java]
        setupRecyclerview()
        return binding.root
    }

    private fun setupRecyclerview() {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchFriends().collectLatest {
                adapter.submitData(it)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SpaceItemDecoration(40))
        adapter.selectItem {
            viewModel.add(it)
        }
        adapter.removeItem {
            viewModel.remove(it)
        }
        adapter.addLoadStateListener {
            binding.selectedCountTv.text = "${adapter.itemCount}"
        }
        viewModel.selectedUsers.observe(viewLifecycleOwner) {
            binding.selectedCountTv.text = "${adapter.itemCount}"
        }
        recyclerView.setOnTouchListener { view, _ ->
            requireContext().hideKeyboardOnScroll(view)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}