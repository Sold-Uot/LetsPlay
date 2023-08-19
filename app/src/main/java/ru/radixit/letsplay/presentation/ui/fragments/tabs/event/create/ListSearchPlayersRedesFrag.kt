package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.radixit.letsplay.databinding.FragSearchListPlayersTeamsRedesBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.EventsAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.ListPlayearsRedesFrag
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.UserProfileViewModel
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.tabs.teams.TeamAdapter
import ru.radixit.letsplay.utils.SpaceItemDecoration
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.visible

@AndroidEntryPoint
class ListSearchPlayersRedesFrag : Fragment() {
    private lateinit var binding: FragSearchListPlayersTeamsRedesBinding
    private lateinit var viewModel: UserProfileViewModel
    private val arrayListFragments = arrayListOf(
        ListPlayearsRedesFrag(),
        ListTeamsRedesFrag()
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragSearchListPlayersTeamsRedesBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity())[UserProfileViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBack()
        settingToolbar()
        val viewPager = binding.viewPager
        val adapter = EventsAdapter(this)
        adapter.setData(arrayListFragments)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false
        TabLayoutMediator(
            binding.tabLayout, viewPager
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Игроки"
                }
                1 -> {
                    tab.text = "КОманды"
                }
            }
        }.attach()
    }
    private fun settingToolbar() {
        with(binding) {
            searchPlayers.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewLifecycleOwner.lifecycleScope.launch {
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
            }

            searchPlayers.setOnCloseListener(object : SearchView.OnCloseListener {
                override fun onClose(): Boolean {
                    val params = searchPlayers.layoutParams as ConstraintLayout.LayoutParams
                    params.startToStart = ConstraintLayout.LayoutParams.UNSET
                    searchPlayers.layoutParams = params
                    titleListEventsTv.visible()
                    nameEventsTv.visible()
                    playersMatCard.visible()
                    return false
                }
            })
        }
    }
    private fun onBack() {
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

}