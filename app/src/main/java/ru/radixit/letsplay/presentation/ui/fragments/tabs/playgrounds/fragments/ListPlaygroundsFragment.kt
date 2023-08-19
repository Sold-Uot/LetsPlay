package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.radixit.letsplay.databinding.FragmentListPlaygroundsBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.utils.SpaceItemDecoration
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.hideKeyboardOnScroll
import ru.radixit.letsplay.utils.visible


@AndroidEntryPoint
class ListPlaygroundsFragment : BaseFragment() {


    private var _binding: FragmentListPlaygroundsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ListPlaygroundsViewModel by viewModels()

    @SuppressLint("ClickableViewAccessibility", "NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListPlaygroundsBinding.inflate(inflater, container, false)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        val adapter = PlaygroundsAdapter()
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SpaceItemDecoration(50))

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playgrounds.cachedIn(viewLifecycleOwner.lifecycleScope)
                .collectLatest{
                    adapter.submitData(it)
                }
        }
        viewModel.newNotifCount.observe(viewLifecycleOwner){
            if(it > 0){
                binding.constTopBar.notifCountTvPlayg.visible()
                binding.constTopBar.notifCountTvPlayg.text = it.toString()
            }else{
                binding.constTopBar.notifCountTvPlayg.gone()
            }
        }
        viewModel.newNotifCount()
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

        adapter.selectItem {
            findNavController().navigate(
                ListPlaygroundsFragmentDirections.actionListPlaygroundsFragmentToPlaygInfoRedesignFrag(
                    null,
                    it.id.toString()
                )
            )
        }
        with(binding) {

            swipeToRefresh.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.searchPlaygrounds(constTopBar.searchFieldsPlayg.query.toString())
                        .collectLatest(adapter::submitData)
                }
                viewLifecycleOwner.lifecycleScope.launch {
                    adapter.loadStateFlow.collect { loadState ->
                        emptyList.isVisible =
                            loadState.source.refresh is LoadState.NotLoading && adapter.itemCount == 0
                        recyclerView.isVisible =
                            loadState.source.refresh is LoadState.NotLoading
                        swipeToRefresh.isRefreshing =
                            loadState.source.refresh is LoadState.Loading
                        progressBar.isVisible = false
                        retryButton.isVisible =
                            loadState.source.refresh is LoadState.Error && adapter.itemCount == 0
                    }
                }
            }

            retryButton.setOnClickListener {
                adapter.refresh()
            }
            constTopBar.notifBellConstPlayg.setOnClickListener {
                findNavController().navigate(ListPlaygroundsFragmentDirections.actionListPlaygroundsFragmentToNotificationsFragment3())
            }
            listMapsBtn.setOnClickListener {
                findNavController().navigate(ListPlaygroundsFragmentDirections.actionListPlaygroundsFragmentToMapsFragment())
            }
            constTopBar.addPlaygroundsPlayg.setOnClickListener {
                findNavController().navigate(ListPlaygroundsFragmentDirections.actionListPlaygroundsFragmentToCreatePlaygRedesFrag())
            }
            constTopBar.searchFieldsPlayg.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.searchPlaygrounds(query.toString())
                            .collectLatest(adapter::submitData)
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })

            recyclerView.setOnTouchListener { view, _ ->
                requireActivity().hideKeyboardOnScroll(view)
            }
        }
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }
}
