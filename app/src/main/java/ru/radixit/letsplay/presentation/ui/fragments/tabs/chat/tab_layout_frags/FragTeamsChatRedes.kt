package ru.radixit.letsplay.presentation.ui.fragments.tabs.chat.tab_layout_frags

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.radixit.letsplay.databinding.FragChatPlayersRedesBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.chat.ChatAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.chat.ChatViewModel
import ru.radixit.letsplay.utils.SpaceItemDecoration

@AndroidEntryPoint
class FragTeamsChatRedes : BaseFragment() {
    private lateinit var binding: FragChatPlayersRedesBinding
    private val viewModel: ChatViewModel by viewModels()
    private val adapter by lazy { ChatAdapter()}
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragChatPlayersRedesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingRV()
        binding.swipeToRefresh.setOnRefreshListener {
            settingRV()
        }
    }
    fun updateSearchResults(query: String?) {
        // Обновить список и отобразить результаты поиска
        // на основе введенного запроса (query)
        Log.d("query","query = ${query}")
    }
    private fun isRefreshing(value:Boolean) {
        if(binding.swipeToRefresh.isRefreshing){
            binding.swipeToRefresh.isRefreshing = value
        }
    }
    private fun settingRV() {
        with(binding) {
            val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            adapter.setFragment(this@FragTeamsChatRedes)
            recyclerView.setHasFixedSize(true)
            recyclerView.addItemDecoration(SpaceItemDecoration(4))
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.fetchChats().cachedIn(viewLifecycleOwner.lifecycleScope)
                    .collect {
                        isRefreshing(false)
                        adapter.submitData(it)
                    }
            }
        }
        binding.retryButton.setOnClickListener {
            adapter.refresh()
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.source.refresh is LoadState.NotLoading && adapter.itemCount == 0
                binding.emptyList.isVisible = isListEmpty
                binding.recyclerView.isVisible =
                    loadState.source.refresh is LoadState.NotLoading
                binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                binding.retryButton.isVisible =
                    loadState.source.refresh is LoadState.Error && adapter.itemCount == 0
            }
        }
    }
}