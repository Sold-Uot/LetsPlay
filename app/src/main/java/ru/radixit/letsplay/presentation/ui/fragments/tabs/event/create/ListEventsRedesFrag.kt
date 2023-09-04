package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.global.SessionManager
import ru.radixit.letsplay.databinding.FragListEventsRedesBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.EventViewModel
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter.ListEventsRedesAdapter
import ru.radixit.letsplay.utils.SpaceItemDecoration
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.hideKeyboardOnScroll
import ru.radixit.letsplay.utils.visible
import javax.inject.Inject

@AndroidEntryPoint
class ListEventsRedesFrag : BaseFragment() {

    private val adapter by lazy {
        ListEventsRedesAdapter { event ->
            findNavController().navigate(
                ListEventsRedesFragDirections.actionListEventsFragmentToEventDescRedesignFrag(
                    event, null
                )
            )
        }
    }

    @Inject
    lateinit var sessionManager: SessionManager
    private var _binding: FragListEventsRedesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EventViewModel by viewModels()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragListEventsRedesBinding.inflate(inflater, container, false)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeToRefresh.setOnRefreshListener {
            settingView()
        }
        settingView()
    }

    private fun settingView() {

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(SpaceItemDecoration(30))
        recyclerView.adapter = adapter
        searchEvents()
        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.source.refresh is LoadState.NotLoading && adapter.itemCount == 0
                binding.emptyEventsLinLay.isVisible = isListEmpty
                recyclerView.isVisible =
                    loadState.source.refresh is LoadState.NotLoading
                binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                binding.retryButton.isVisible =
                    loadState.source.refresh is LoadState.Error && adapter.itemCount == 0
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
//                viewModel.searchEvents(binding.constTopBar.searchEvents.text.toString()).collectLatest {
//                    adapter.submitData(it)
//                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                binding.emptyEventsLinLay.isVisible =
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

        switchBtn()
        with(binding.constTopBar) {
            searchFieldsEvent.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchEvents(query)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })
            searchFieldsEvent.setOnSearchClickListener {
                val params = searchFieldsEvent.layoutParams as ConstraintLayout.LayoutParams
                params.startToStart = ConstraintSet.PARENT_ID
                searchFieldsEvent.layoutParams = params
                addPlaygroundsEvent.gone()
                allPeopleEvent.gone()
                onlyOnePersonEvent.gone()
            }

            searchFieldsEvent.setOnCloseListener(object : SearchView.OnCloseListener {
                override fun onClose(): Boolean {
                    val params = searchFieldsEvent.layoutParams as ConstraintLayout.LayoutParams
                    params.startToStart = ConstraintLayout.LayoutParams.UNSET
                    searchFieldsEvent.layoutParams = params
                    addPlaygroundsEvent.visible()
                    allPeopleEvent.visible()
                    onlyOnePersonEvent.visible()
                    return false
                }
            })
            addPlaygroundsEvent.setOnClickListener {
                findNavController().navigate(
                    ListEventsRedesFragDirections.actionListEventsRedesFragToEventInDetailRedesFrag2(
                        null,
                        null,
                        null
                    )
                )
            }
        }
        binding.createEventMatBtn.setOnClickListener {
            findNavController().navigate(
                ListEventsRedesFragDirections.actionListEventsRedesFragToEventInDetailRedesFrag2(
                    null,
                    null,
                    null
                )
            )
        }
        binding.listMapsBtn.setOnClickListener {
            findNavController().navigate(ListEventsRedesFragDirections.actionListEventsFragmentToMapEventsFragment())
        }
        binding.retryButton.setOnClickListener {
            adapter.refresh()
        }

//        binding.searchEvents.doAfterTextChanged {
//            viewLifecycleOwner.lifecycleScope.launch {
//                viewModel.searchEvents(it.toString()).collectLatest {
//                    adapter.submitData(it)
//                }
//            }
//        }

        binding.recyclerView.setOnTouchListener { view, _ ->
            requireActivity().hideKeyboardOnScroll(view)
        }

    }

    private fun searchEvents(text: String? = null) {
        viewLifecycleOwner.lifecycleScope.launch {
            text?.let {
                viewModel.searchEvents(it).cachedIn(viewLifecycleOwner.lifecycleScope)
                    .collectLatest { eventList ->
                        adapter.submitData(eventList)
                    }
            } ?: run {
                viewModel.searchEvents().cachedIn(viewLifecycleOwner.lifecycleScope).collectLatest {
                    adapter.submitData(it)
                }
            }
        }
    }

    private fun switchBtn() {
        with(binding.constTopBar) {
            allPeopleEvent.setOnClickListener {
                searchFieldsEvent.visible()
                allPeopleEvent.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.back_color
                    )
                )
                allPeopleEvent.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )

                onlyOnePersonEvent.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                onlyOnePersonEvent.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background_for_edittext
                    )
                )
                searchEvents()
            }
            onlyOnePersonEvent.setOnClickListener {
                searchFieldsEvent.gone()
                allPeopleEvent.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                allPeopleEvent.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background_for_edittext
                    )
                )

                onlyOnePersonEvent.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                onlyOnePersonEvent.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.back_color
                    )
                )
                viewModel.fetchProfile(sessionManager.fetchToken())
                viewModel.profile.observe(viewLifecycleOwner) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.events(it.id).collectLatest {
                            adapter.submitData(it)
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        with(binding.constTopBar) {
            val params = searchFieldsEvent.layoutParams as ConstraintLayout.LayoutParams
            params.startToStart = ConstraintLayout.LayoutParams.UNSET
            searchFieldsEvent.layoutParams = params
            addPlaygroundsEvent.visible()
            allPeopleEvent.visible()
            searchFieldsEvent.isIconified = true
            onlyOnePersonEvent.visible()
        }
    }
}