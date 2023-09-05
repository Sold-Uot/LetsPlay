package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.friends

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragListFriendsRedesignBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.EventInDetailRedesFragDirections
import ru.radixit.letsplay.utils.*

@AndroidEntryPoint
class ListFriendsRedesFrag : Fragment() {

    private var _binding: FragListFriendsRedesignBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FriendsViewModel by viewModels()
    private val args by navArgs<ListFriendsRedesFragArgs>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragListFriendsRedesignBinding.inflate(inflater, container, false)
        onBack()
        binding.swipeToRefresh.setOnRefreshListener{
            setupRecyclerview()
        }
        binding.openAddFriend.setOnClickListener {
            Log.w("click", "wow its click")
            findNavController().navigate(ListFriendsRedesFragDirections.actionListFriendsRedesFragToFindFriendFragment())
        }
        setupRecyclerview()
//        binding.addFriend.setOnClickListener {
//            if (findNavController().currentDestination?.id == R.id.friendsFragment) {
//                findNavController().navigate(R.id.action_friendsFragment_to_findFriendFragment)
//            }
//        }
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupRecyclerview() {
        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            val adapter = FriendsAdapter()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.searchUsers(userId = args.id.toString()).collect {
                    adapter.submitData(it)
                }
            }
            recyclerView.adapter = adapter
            recyclerView.addItemDecoration(SpaceItemDecoration(40))
            searchFriendSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(text: String?): Boolean {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.searchFriends(text.toString()).collectLatest {
                            adapter.submitData(it)
                        }
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })
            searchFriendSv.setOnSearchClickListener {
                val params = searchFriendSv.layoutParams as ConstraintLayout.LayoutParams
                params.startToStart = ConstraintSet.PARENT_ID
                searchFriendSv.layoutParams = params
                titleListFriendsTv.gone()
                nameFriendTv.gone()
            }

            searchFriendSv.setOnCloseListener(object : SearchView.OnCloseListener {
                override fun onClose(): Boolean {
                    val params = searchFriendSv.layoutParams as ConstraintLayout.LayoutParams
                    params.startToStart = ConstraintLayout.LayoutParams.UNSET
                    searchFriendSv.layoutParams = params
                    titleListFriendsTv.visible()
                    nameFriendTv.visible()
                    return false
                }
            })
            viewModel.fetchProfile(args.id.toString())
            viewModel.profile.observe(viewLifecycleOwner) {
                if (it.name.equals("")) {
                    nameFriendTv.gone()
                    val params = titleListFriendsTv.layoutParams as ConstraintLayout.LayoutParams
                    params.bottomToBottom = ConstraintSet.PARENT_ID
                    titleListFriendsTv.layoutParams = params
                } else {
                    nameFriendTv.text = it.name
                    val params = titleListFriendsTv.layoutParams as ConstraintLayout.LayoutParams
                    params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                    titleListFriendsTv.layoutParams = params
                }
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
            swipeToRefresh.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.searchUsers(
                        searchFriendSv.query.toString(),
                        args.id.toString()
                    )
                        .collectLatest {
                            adapter.submitData(it)
                        }
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

//        searchPlayers.doAfterTextChanged {
//            viewLifecycleOwner.lifecycleScope.launch {
//                viewModel.searchUsers(it.toString(), args.id.toString()).collectLatest {
//                    adapter.submitData(it)
//                }
//            }
//        }


            adapter.selectItem {
//                findNavController().navigate(
//                    FriendsFragmentDirections.actionFriendsFragmentToFriendProfileFragment(
//                        it.id.toString()
//                    )
//                )
            }
            adapter.showActions { id ->
                val actions = BottomSheetDialog(requireContext())
                actions.requestWindowFeature(Window.FEATURE_NO_TITLE)
                actions.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                actions.setContentView(R.layout.fragment_bottom_sheet_dialog_actions)
                actions.findViewById<TextView>(R.id.add_black_list)?.setOnClickListener {
                    actions.dismiss()
                    viewModel.block(id)
                    viewModel.response.observe(viewLifecycleOwner) { requireContext().showToast(it) }
                }
                actions.findViewById<TextView>(R.id.send_message)?.setOnClickListener {
//                    findNavController().navigate(
//                        ListFriendsRedesFrag.actionFriendsFragmentToUserChatFragment2(
//                            1,
//                            id.toInt()
//                        )
//                    )
//                    actions.dismiss()
                }
                actions.show()
            }
            adapter.addLoadStateListener {
                binding.foundCountTv.text = "${adapter.itemCount}"
            }
            recyclerView.setOnTouchListener { view, _ ->
                requireContext().hideKeyboardOnScroll(view)
            }
        }
        if(binding.swipeToRefresh.isRefreshing){
            binding.swipeToRefresh.isRefreshing = false
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