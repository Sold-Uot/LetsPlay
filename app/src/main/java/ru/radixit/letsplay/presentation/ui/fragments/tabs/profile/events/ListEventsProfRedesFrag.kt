package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.events

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.frag_list_events_redesign.recyclerViewCurrent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragListEventsRedesignBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.ProfileViewModel
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter.ListEventsRedesAdapter
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.launchWhenStartedTest
import ru.radixit.letsplay.utils.visible

@AndroidEntryPoint
class ListEventsProfRedesFrag : Fragment() {
    private val builder = SpannableStringBuilder()

    private var _binding: FragListEventsRedesignBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel
    private val adapterCurrent by lazy {
        ListEventsRedesAdapter { event ->
            findNavController().navigate(
                ListEventsProfRedesFragDirections.actionListEventsProfRedesFragToEventDescRedesignFrag(
                    event, null
                )
            )
        }
    }
    private val args by navArgs<ListEventsProfRedesFragArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragListEventsRedesignBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingView()
    }

    private fun settingView() {
        recyclerViewCurrent.layoutManager =
            LinearLayoutManager(requireContext())
        recyclerViewCurrent.adapter = adapterCurrent
        recyclerViewCurrent.setHasFixedSize(true)
        currentAdapter()
        completedAdapter()
        binding.emptyEventsLinLay.gone()
        switchBtn()
        binding.swipeToRefresh.setOnRefreshListener {
            currentAdapter()
            completedAdapter()
            binding.emptyEventsLinLay.gone()
        }

        viewModel.getProfileData(args.id)
        viewModel.profile.observe(viewLifecycleOwner) {
            if (it.name.equals("")) {
                binding.nameEventsTv.gone()
                val params = binding.titleListEventsTv.layoutParams as ConstraintLayout.LayoutParams
                params.bottomToBottom = ConstraintSet.PARENT_ID
                binding.titleListEventsTv.layoutParams = params
            } else {
                binding.nameEventsTv.text = it.name
                val params = binding.titleListEventsTv.layoutParams as ConstraintLayout.LayoutParams
                params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                binding.titleListEventsTv.layoutParams = params
            }
        }
        binding.createEventMatBtn.setOnClickListener {

        }
    }

    private fun switchBtn() {
        with(binding) {
//            searchPlayers.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//                override fun onQueryTextSubmit(query: String?): Boolean {
//                    viewLifecycleOwner.lifecycleScope.launch {
//
//                    }
//                    return true
//                }
//
//                override fun onQueryTextChange(newText: String?): Boolean {
//                    return true
//                }
//            })
//            searchPlayers.setOnSearchClickListener {
//                val params = searchPlayers.layoutParams as ConstraintLayout.LayoutParams
//                params.startToStart = ConstraintSet.PARENT_ID
//                searchPlayers.layoutParams = params
//                titleListEventsTv.gone()
//                nameEventsTv.gone()
//            }
//
//            searchPlayers.setOnCloseListener(object : SearchView.OnCloseListener {
//                override fun onClose(): Boolean {
//                    val params = searchPlayers.layoutParams as ConstraintLayout.LayoutParams
//                    params.startToStart = ConstraintLayout.LayoutParams.UNSET
//                    searchPlayers.layoutParams = params
//                    titleListEventsTv.visible()
//                    nameEventsTv.visible()
//                    return false
//                }
//            })
            toolbar2.setOnClickListener {
                findNavController().popBackStack()
            }
            currentEvent.setOnClickListener {
                currentEvent.tag = "1"
                completeEvent.tag = "0"
                currentEvent.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.back_color
                    )
                )
                currentEvent.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )

                completeEvent.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                completeEvent.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background_for_edittext
                    )
                )
                currentAdapter()
            }
            completeEvent.setOnClickListener {
                currentEvent.tag = "0"
                completeEvent.tag = "1"
                currentEvent.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                currentEvent.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background_for_edittext
                    )
                )

                completeEvent.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                completeEvent.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.back_color
                    )
                )
                completedAdapter()
            }
        }
    }

    private fun completedAdapter() {
        with(binding) {
            if (swipeToRefresh.isRefreshing) {
                swipeToRefresh.isRefreshing = false
            }
            emptyEventsLinLay.gone()
            lifecycleScope.launch {
                viewModel.archiveEvents(args.id).collectLatest { list ->
                    adapterCurrent.submitData(list)
                }
            }
            lifecycleScope.launch {
                adapterCurrent.loadStateFlow.collect { loadState ->
                    val str = "Завершенные ${adapterCurrent.itemCount}"
                    val str1 = SpannableString(str)
                    str1.setSpan(
                        StyleSpan(Typeface.BOLD),
                        str.length - "${adapterCurrent.itemCount}".length,
                        str.length,
                        0
                    )
                    builder.append(str1)
                    completeEvent.text = str1
                    val isListEmpty =
                        loadState.source.refresh is LoadState.NotLoading && adapterCurrent.itemCount == 0
                    if (isListEmpty) {
                        if (currentEvent.tag.equals("0") && binding.completeEvent.tag.equals("1")) {
                            emptyEventsCompleteLinLay.visible()
                            emptyEventsLinLay.gone()
                        }
                    }
                }
            }
        }
    }

    private fun currentAdapter() {
        with(binding) {
            if (swipeToRefresh.isRefreshing) {
                swipeToRefresh.isRefreshing = false
            }
            emptyEventsCompleteLinLay.gone()
            lifecycleScope.launch {
                viewModel.events(args.id).collectLatest { list ->
                    adapterCurrent.submitData(list)
                }
            }
            lifecycleScope.launch {
                adapterCurrent.loadStateFlow.onEach { loadState ->
                    val str = "Текущие ${adapterCurrent.itemCount}"
                    val str1 = SpannableString(str)
                    str1.setSpan(
                        StyleSpan(Typeface.BOLD),
                        str.length - "${adapterCurrent.itemCount}".length,
                        str.length,
                        0
                    )
                    builder.append(str1)
                    currentEvent.text = str1
                    val isListEmpty =
                        loadState.source.refresh is LoadState.NotLoading && adapterCurrent.itemCount == 0
                    if (isListEmpty) {
                        if (binding.currentEvent.tag.equals("1") && binding.completeEvent.tag.equals(
                                "0"
                            )
                        ) {
                            emptyEventsLinLay.visible()
                            emptyEventsCompleteLinLay.gone()
                        }
                    }
                    recyclerViewCurrent.isVisible =
                        loadState.source.refresh is LoadState.NotLoading
                }.launchWhenStartedTest(lifecycleScope)
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}