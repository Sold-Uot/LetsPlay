package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.play_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragListPlaygsRedesBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.CreateEventViewModel
import ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.fragments.ListPlaygroundsFragmentDirections
import ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.fragments.ListPlaygroundsViewModel
import ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.fragments.PlaygroundsAdapter
import ru.radixit.letsplay.utils.SpaceItemDecoration
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.hideKeyboardOnScroll
import ru.radixit.letsplay.utils.visible

@AndroidEntryPoint
class ListPlaygRedesFrag : DialogFragment() {

    private lateinit var binding: FragListPlaygsRedesBinding
    private val viewModel: ListPlaygroundsViewModel by viewModels()
    private lateinit var viewModelEvent: CreateEventViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.FullScreenDialogStyle
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragListPlaygsRedesBinding.inflate(layoutInflater)
        viewModelEvent = ViewModelProvider(requireActivity())[CreateEventViewModel::class.java]
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingView()
        onBack()
    }

    private fun settingView() {

        with(binding) {

            pointMapImg.setOnClickListener {
                findNavController().navigate(
                    ListPlaygRedesFragDirections.actionListPlaygRedesFragToListMapsRedesFrag(
                    )
                )
            }

            cancelBtn.setOnClickListener {
                constraintLayout7.gone()
            }
            val recyclerView = recyclerView
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext())
            val adapter = PlaygroundsAdapter()
            recyclerView.adapter = adapter
            recyclerView.addItemDecoration(SpaceItemDecoration(50))
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.playgrounds.cachedIn(viewLifecycleOwner.lifecycleScope)
                    .collectLatest(adapter::submitData)
            }
            lifecycleScope.launch {
                adapter.loadStateFlow.collect { loadState ->
                    val isListEmpty =
                        loadState.source.refresh is LoadState.NotLoading && adapter.itemCount == 0
                    emptyList.isVisible = isListEmpty
                    recyclerView.isVisible =
                        loadState.source.refresh is LoadState.NotLoading
                    progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                    retryButton.isVisible =
                        loadState.source.refresh is LoadState.Error && adapter.itemCount == 0
                }
            }

            adapter.selectItem { playground ->
                constraintLayout7.visible()
                saveDataBtn.setOnClickListener {
                    playground.id?.let { id ->
                        viewModelEvent.savePlaygId(id)
                    }
                    viewModelEvent.setTitle(playground.title ?: "Неизвестный адрес")
                    findNavController().popBackStack()
                }
            }
            swipeToRefresh.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.searchPlaygrounds(searchPlayers.query.toString())
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
            searchPlayers.setOnSearchClickListener {
                val params = searchPlayers.layoutParams as ConstraintLayout.LayoutParams
                params.startToStart = ConstraintSet.PARENT_ID
                searchPlayers.layoutParams = params
                pointMapImg.gone()
                titleToolbar.gone()
            }

            searchPlayers.setOnCloseListener(object : SearchView.OnCloseListener {
                override fun onClose(): Boolean {
                    val params = searchPlayers.layoutParams as ConstraintLayout.LayoutParams
                    params.startToStart = ConstraintLayout.LayoutParams.UNSET
                    searchPlayers.layoutParams = params
                    pointMapImg.visible()
                    titleToolbar.visible()
                    return false
                }
            })
            searchPlayers.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
    }

    private fun onBack() {
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}