package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.global.SessionManager
import ru.radixit.letsplay.databinding.FragProfileRedesignBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter.EventsRedesAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter.FriendsRedesAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter.ListTeamProfileRedesAdapter
import ru.radixit.letsplay.utils.SpaceItemDecoration
import ru.radixit.letsplay.utils.visible
import javax.inject.Inject


@AndroidEntryPoint
class ProfileRedesignFrag : BaseFragment() {

    private var _binding: FragProfileRedesignBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragProfileRedesignBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            swipeToRefresh.setOnRefreshListener {
                getProfileData()
            }
            editMatBtn.setOnClickListener {
                findNavController().navigate(ProfileRedesignFragDirections.actionProfileRedesignFragToEditProfileRedesFrag())
            }
            allInformProfileTv.setOnClickListener {
                if (findNavController().currentDestination?.id == R.id.profileRedesignFrag) {
                    findNavController().navigate(R.id.action_profileRedesignFrag_to_settingsRedesignFrag)
                }
            }
            getProfileData()
        }
    }

    private fun getProfileData() {
        with(binding) {
            viewModel.fetchProfile(sessionManager.fetchToken())
            viewModel.loading.observe(viewLifecycleOwner) {
                infoProgressBar.loadingProgressLayout.isVisible = it
            }
            viewModel.profile.observe(viewLifecycleOwner) {
                if(it.name !=  null && it.surname != null){
                    profileName.text = "${it.name} ${it.surname}"
                }else if(it.name !=  null && it.surname == null){
                    profileName.text = "${it.name}"
                }else if(it.name ==  null && it.surname != null){
                    profileName.text = "${it.surname}"
                }else{
                    profileName.text = "Не указано"
                }

                missedGamesCountTv.text =
                    if (it.wasNotPresentGame != null) it.wasNotPresentGame.toString() else "0"
                playedCountTv.text =
                    if (it.matchesPlayed != null) it.matchesPlayed.toString() else "0"
                sport.text = it.userType ?: "Неизв."
                if (it.photo != null) {
                    nameOnAvatar.visibility = View.GONE
                    profileImage.isVisible = true
                    Glide.with(root).load(it.photo.url).into(profileImage)
                } else {
                    nameOnAvatar.visibility = View.VISIBLE
                    profileImage.isVisible = false
                    nameOnAvatar.text = "${it.name.toString()[0]}"
                    val cardColor =
                        ContextCompat.getColor(requireContext(), R.color.violet)
                    profileMatCard.setCardBackgroundColor(cardColor)
                }
                profileInfoAddressTv.text = it.address ?: "Неизв."
                profileInfoPositionTv.text = if (it.position != null) {
                    "${it.position}"
                } else {
                    "не указана"
                }
                matchesPlayedTv.text =
                    if (it.matchesPlayed != null) it.matchesPlayed.toString() else "Неизв."
                setupRecyclerview(it.id)
                setupTeams(it.id)
                setupEvents(it.id)
            }
            if (swipeToRefresh.isRefreshing) {
                swipeToRefresh.isRefreshing = false
            }
        }
    }

    private fun setupTeams(id: Int) {
        binding.teamsArrowEndImg.setOnClickListener {
            findNavController().navigate(
                ProfileRedesignFragDirections.actionProfileRedesignFragToListTeamsRedesFrag(
                    id
                )
            )
        }
        val teamRecyclerView = binding.teamsRv
        teamRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapter = ListTeamProfileRedesAdapter()
        viewModel.listTeams(id)
        viewModel.teams.observe(viewLifecycleOwner) {
            adapter.setData(it)
            binding.teamsCountTv.text = "${it.size}"
            if (it.isEmpty()) {
                binding.emptyListTeamsTv.visible()
            }
        }
        teamRecyclerView.adapter = adapter
        teamRecyclerView.addItemDecoration(SpaceItemDecoration(30))
    }

    private fun setupEvents(id: Int) {
        binding.eventsArrowEndImg.setOnClickListener {
            findNavController().navigate(
                ProfileRedesignFragDirections.actionProfileRedesignFragToListEventsProfRedesFrag(
                    id
                )
            )
        }
        val recyclerView = binding.eventsRv

        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        val adapter = EventsRedesAdapter()
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collectLatest {
                adapter.submitData(it)
            }
        }
        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                binding.eventsCountTv.text = adapter.itemCount.toString()
                val isListEmpty =
                    loadState.source.refresh is LoadState.NotLoading && adapter.itemCount == 0
                binding.emptyListEventsTv.isVisible = isListEmpty
                recyclerView.isVisible =
                    loadState.source.refresh is LoadState.NotLoading
            }
        }
    }

    private fun setupRecyclerview(id: Int) {
        val recyclerView = binding.friendsRv

        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        recyclerView.setHasFixedSize(true)
        val adapter = FriendsRedesAdapter()
        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchUsers(userId = id.toString()).collect {
                adapter.submitData(it)
            }
        }
        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                binding.friendCountTv.text = adapter.itemCount.toString()
                val isListEmpty =
                    loadState.source.refresh is LoadState.NotLoading && adapter.itemCount == 0
                binding.emptyListFriendTv.isVisible = isListEmpty
                recyclerView.isVisible =
                    loadState.source.refresh is LoadState.NotLoading
            }
        }
        adapter.selectItem {
            findNavController().navigate(
                ProfileRedesignFragDirections.actionProfileRedesignFragToFriendProfileInfoFragment(
                    it.id.toString()
                )
            )
        }
        binding.friendArrowEndImg.setOnClickListener {
            viewModel.profile.observe(viewLifecycleOwner) {
                findNavController().navigate(
                    ProfileRedesignFragDirections.actionProfileRedesignFragToListFriendsRedesFrag(
                        it.id
                    )
                )
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}