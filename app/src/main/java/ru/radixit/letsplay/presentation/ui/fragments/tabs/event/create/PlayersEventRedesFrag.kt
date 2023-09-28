package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create

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
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bootom_sheet_dialog_friend_actions.view.close
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragListPlayersRedesBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes.PlayersForEventRedesAdapter
import ru.radixit.letsplay.utils.SpaceItemDecoration
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.hideKeyboardOnScroll
import ru.radixit.letsplay.utils.setOnSingleClickListener
import ru.radixit.letsplay.utils.showToast
import ru.radixit.letsplay.utils.visible

@AndroidEntryPoint
class PlayersEventRedesFrag : DialogFragment() {

    private var _binding: FragListPlayersRedesBinding? = null
    private var selectCount = 0
    private val binding get() = _binding!!
    private val adapter by lazy { PlayersForEventRedesAdapter() }
    private lateinit var viewModel: CreateEventViewModel

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

        _binding = FragListPlayersRedesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[CreateEventViewModel::class.java]
        onBack()
        setupRecyclerview()
        settingToolbar()
        binding.menuImg.setOnClickListener {
            binding.menuLinLay.isVisible = !binding.menuLinLay.isVisible

        }
        return binding.root
    }

    private fun settingToolbar() {
        with(binding) {

            selectedAllLinLay.setOnSingleClickListener{
                adapter.selectAll(true)
                context?.showToast("Все выбраны")


            }
            cancelSelection.setOnSingleClickListener{
                adapter.selectAll(false)
                context?.showToast("Все удалены")
                cancelSelection
            }

            searchPlayers.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.searchFriends(query.toString()).collectLatest {
                            adapter.submitData(it)
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })
            searchPlayers.setOnSearchClickListener {
                val params = searchPlayers.layoutParams as ConstraintLayout.LayoutParams
                params.startToStart = ConstraintSet.PARENT_ID
                searchPlayers.layoutParams = params
                titleListEventsTv.gone()
                playersMatCard.gone()
                nameEventsTv.gone()
                binding.menuImg.gone()
            }

            searchPlayers.setOnCloseListener(object : SearchView.OnCloseListener {
                override fun onClose(): Boolean {
                    val params = searchPlayers.layoutParams as ConstraintLayout.LayoutParams
                    params.startToStart = ConstraintLayout.LayoutParams.UNSET
                    searchPlayers.layoutParams = params
                    titleListEventsTv.visible()
                    nameEventsTv.visible()
                    binding.menuImg.visible()
                    playersMatCard.visible()
                    return false
                }
            })
        }
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
            viewModel.addFriendToInviteList(it)
            selectCount+=1
//            binding.selectedCountTv.text = selectCount.toString()
            viewModel.add(it)


        }


        adapter.removeItem {
            viewModel.removeFriendToInviteList(it)
            viewModel.remove(it)
            selectCount-=1
//            binding.selectedCountTv.text = (selectCount).toString()

        }
        adapter.addLoadStateListener {
            binding.foundCountTv.text = "${adapter.itemCount}"
        }
        viewModel.selectedUsers.observe(viewLifecycleOwner) {
            binding.selectedCountTv.text = it.size.toString()
        }
        recyclerView.setOnTouchListener { view, _ ->
            requireContext().hideKeyboardOnScroll(view)
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