package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings.blackList

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragBlackListRedesBinding
import ru.radixit.letsplay.utils.*

@AndroidEntryPoint
class BlackListRedesFrag : DialogFragment() {

    private var _binding: FragBlackListRedesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BlackListViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.FullScreenDialogStyle
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragBlackListRedesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[BlackListViewModel::class.java]
        onBack()
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = BlackListRedesAdapter()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchUsers().collectLatest {
                adapter.submitData(it)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SpaceItemDecoration(40))
        binding.searchPlayers.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.searchUsers(binding.searchPlayers.query.toString()).collectLatest {
                        adapter.submitData(it)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.source.refresh is LoadState.NotLoading && adapter.itemCount == 0
                binding.emptyList.isVisible = isListEmpty
                recyclerView.isVisible =
                    loadState.source.refresh is LoadState.NotLoading
                binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            }
        }
        adapter.addLoadStateListener {
            binding.foundCountTv.text = "${adapter.itemCount}"
        }
        adapter.removeItem {
            viewModel.unblock(it)
            viewModel.successUnblock.observe(viewLifecycleOwner) { success ->
                if (success) {
                    adapter.refresh()
                    requireContext().showToast("Пользователь удалён из чёрного списка")
                } else {
                    requireContext().showToast("Что-то пошло не так")
                }
            }
        }
        adapter.selectItem {
            if (findNavController().currentDestination?.id == R.id.blackListRedesFrag) {
                findNavController()
                    .navigate(
                        BlackListRedesFragDirections.actionBlackListRedesFragToUserInBlackListDialogFragment(
                            it.toInt()
                        )
                    )
            }
        }
        recyclerView.setOnTouchListener { view, _ ->
            requireContext().hideKeyboardOnScroll(view)
        }
        with(binding){
            searchPlayers.setOnSearchClickListener {
                val params = searchPlayers.layoutParams as ConstraintLayout.LayoutParams
                params.startToStart = ConstraintSet.PARENT_ID
                searchPlayers.layoutParams = params
                titleListFriendsTv.gone()
            }

            searchPlayers.setOnCloseListener(object : SearchView.OnCloseListener{
                override fun onClose(): Boolean {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.searchUsers().collectLatest {
                            adapter.submitData(it)
                        }
                    }
                    val params = searchPlayers.layoutParams as ConstraintLayout.LayoutParams
                    params.startToStart = ConstraintLayout.LayoutParams.UNSET
                    searchPlayers.layoutParams = params
                    titleListFriendsTv.visible()
                    return false
                }
            })
        }
        return binding.root
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