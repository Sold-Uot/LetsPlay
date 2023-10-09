package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.global.SessionManager
import ru.radixit.letsplay.databinding.FragProfilePlayerRedesBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter.FriendsRedesAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter.ListEventPlayerAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter.ListTeamProfileRedesAdapter
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.setOnSingleClickListener
import ru.radixit.letsplay.utils.showSnackBar
import ru.radixit.letsplay.utils.showToast
import ru.radixit.letsplay.utils.visible
import javax.inject.Inject

@AndroidEntryPoint
class ProfilePlayerFrag : Fragment() {
    private lateinit var binding: FragProfilePlayerRedesBinding
    private lateinit var viewModel: ProfileViewModel
    private val args by navArgs<ProfilePlayerFragArgs>()

    private val id by lazy {
        Log.d("id =", " id = ${args.id}")
        if (args.id != null) {
            args.id.toString()
        } else {
            args.id


        }
    }


    private val friendAdapter: FriendsRedesAdapter =
        FriendsRedesAdapter {
            val user = it
            if ( viewModel.checkThisMyProfile(user.id)){
            findNavController().navigate(
                ProfilePlayerFragDirections.actionFriendProfileInfoFragmentSelf(user.id.toString()
                )
            )}
            else
                context?.showToast("Это ваш профиль")


        }


    @Inject
    lateinit var sessionManager: SessionManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragProfilePlayerRedesBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar2.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            swipeToRefresh.setOnRefreshListener {
                getProfileData()
            }
            allInformProfPlayTv.setOnSingleClickListener() {
                findNavController().navigate(ProfilePlayerFragDirections.actionFriendProfileInfoFragmentToNotifSettingRedesFrag())
            }
        }
        getProfileData()
    }

    private fun getProfileData() {
        with(binding) {
            Log.w("iddi", id.toString())




            viewModel.getProfilePlayerData(id.toInt())
            viewModel.loading.observe(viewLifecycleOwner) {
                infoProgressBar.loadingProgressLayout.isVisible = it
            }
            viewModel.profile_player.observe(viewLifecycleOwner) {
                Log.e("id_it", it.id.toString())
                friendCountTv.text = it.friends.size.toString()
                if (it.name != null && it.surname != null) {
                    profileName.text = "${it.name} ${it.surname}"
                } else if (it.name != null && it.surname == null) {
                    profileName.text = "${it.name}"
                } else if (it.name == null && it.surname != null) {
                    profileName.text = "${it.surname}"
                } else {
                    profileName.text = "Не указано"
                }

                if (it.stateFriend != null) {
                    binding.addFriendMatBtn.gone()

                }

                missedGamesCountTv.text =
                    if (it.wasNotPresentGame != null) it.wasNotPresentGame.toString() else "0"
                /*playedCountTv.text =
                    if (it.matchesPlayed != null) it.matchesPlayed.toString() else "0"
                sport.text = it.userType ?: "Неизв."*/
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
                cityValueTv.text = it.address ?: "Неизв."
                posValueTv.text = when(it.position){
                    "1"-> "Нападающий"
                    "2"-> "Защитник"
                    "3"-> "Вратарь"
                    else -> "Не указано"
                }

                matchesPlayedValueTv.text =
                    if (it.matchesPlayed != null) it.matchesPlayed.toString() else "Неизв."
                clickViews(it.id)


                setupEvents(it.id)
                setupFriends(it.id)
                setupTeams(it.id)
            }


            viewModel.successAddToFriends.observe(viewLifecycleOwner) {
                if (it) {
                    binding.addFriendMatBtn.text = resources.getString(R.string.add_friend_send)
                    binding.addFriendMatBtn.isEnabled = false

                } else {
                    binding.addFriendMatBtn.text = resources.getString(R.string.add_friend)
                    binding.addFriendMatBtn.isEnabled = true
                }
            }
            if (swipeToRefresh.isRefreshing) {
                swipeToRefresh.isRefreshing = false
            }
        }
    }

    private fun clickViews(id: Int) {
        with(binding) {
            friendArrowEndImg.setOnClickListener {
                findNavController().navigate(
                    ProfilePlayerFragDirections.actionFriendProfileInfoFragmentToListFriendsRedesFrag(
                        id,true
                    )
                )
            }
            writeMatBtn.setOnClickListener {
                findNavController().navigate(
                    ProfilePlayerFragDirections.actionFriendProfileInfoFragmentToUserChatFragment2(
                        1,
                        id
                    )
                )
            }
            moreToolbar.setOnClickListener {
                val actions = BottomSheetDialog(requireContext())
                actions.requestWindowFeature(Window.FEATURE_NO_TITLE)
                actions.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                actions.setContentView(R.layout.fragment_bottom_sheet_dialog_actions)
                actions.findViewById<TextView>(R.id.send_message)?.setOnClickListener {
                    findNavController().navigate(
                        ProfilePlayerFragDirections.actionFriendProfileInfoFragmentToUserChatFragment2(
                            1,
                            id
                        )
                    )
                    actions.dismiss()
                }
                actions.findViewById<TextView>(R.id.invite_event)?.setOnClickListener {
                    findNavController().navigate(
                        ProfilePlayerFragDirections.actionFriendProfileInfoFragmentToListEventsProfRedesFrag(
                            id
                        )
                    )
                    actions.dismiss()
                }
                actions.findViewById<TextView>(R.id.invite_in_team)?.setOnClickListener {
                    findNavController().navigate(
                        ProfilePlayerFragDirections.actionFriendProfileInfoFragmentToListTeamsRedesFrag(
                            id
                        )
                    )
                    actions.dismiss()
                }
                actions.findViewById<TextView>(R.id.report)?.setOnClickListener {
                    findNavController().navigate(
                        ProfilePlayerFragDirections.actionFriendProfileInfoFragmentToUserReportFragment(
                            id.toString()
                        )
                    )
                    actions.dismiss()
                }
                actions.findViewById<TextView>(R.id.add_black_list)?.setOnClickListener {
                    viewModel.block(id.toString())
                    viewModel.response.observe(viewLifecycleOwner) { binding.root.showSnackBar(it) }
                    actions.dismiss()
                }
                actions.show()
            }
        }
    }

    private fun setupTeams(id: Int) {
        binding.teamsArrowEndImg.setOnClickListener {
            findNavController().navigate(
                ProfilePlayerFragDirections.actionFriendProfileInfoFragmentToListTeamsRedesFrag(
                    id
                )
            )
        }

        binding.addFriendMatBtn.setOnClickListener {
            viewModel.addToFriends(id.toString())
//            viewModel.profile.observe(viewLifecycleOwner) {
//                if (it.stateFriend == null) {
//                    if (findNavController().currentDestination?.id == R.id.userProfileFragment) {
//                        findNavController().navigate(
//                            UserProfileFragmentDirections.actionUserProfileFragmentToAddToFriendsFragment(
//                                id.toString(),
//                                "${it.name ?: ""} ${it.surname ?: ""}"
//                            )
//                        )
//                    }
//                } else if (it.stateFriend == 1) {
//                    findNavController().navigate(
//                        UserProfileFragmentDirections.actionUserProfileFragmentToRemoveFromFriendsFragment(
//                            id,
//                            "${it.name ?: ""} ${it.surname ?: ""}"
//                        )
//                    )
//                }
//            }
        }
        val teamRecyclerView = binding.teamsRv
        teamRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapter = ListTeamProfileRedesAdapter()
        viewModel.listTeams(id)
        viewModel.teams.observe(viewLifecycleOwner) {
            binding.emptyListTeamsTv.gone()
            adapter.setData(it)
            binding.teamsCountTv.text = "${adapter.itemCount}"
            if (adapter.itemCount == 0) {
                binding.emptyListTeamsTv.visible()
            }
        }
        adapter.onClick {
            findNavController().navigate(ProfilePlayerFragDirections.actionFriendProfileInfoFragmentToListTeamPlayers(
                it.id!!,it.my,sessionManager.fetchToken()))
        }
        teamRecyclerView.adapter = adapter
    }

    private fun setupEvents(id: Int) {
        lifecycleScope.launch {
            Log.e("id_event", id.toString())




            binding.eventsArrowEndImg.setOnClickListener {

                findNavController().navigate(
                    ProfilePlayerFragDirections.actionFriendProfileInfoFragmentToListEventsProfRedesFrag(
                        id
                    )
                )
            }
            viewModel.listEvent(id)

            val recyclerView = binding.eventsRv

            recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            val adapter = ListEventPlayerAdapter()
            recyclerView.setHasFixedSize(true)
            viewModel.eventLiveData.observe(viewLifecycleOwner){
                adapter.setData(it)
                val size = adapter.itemCount
                binding.eventsCountTv.text = "${size}"
                if (size == 0) {
                    binding.emptyListEventsTv.visible()
                }
            }

            adapter.selectItem {
                findNavController().navigate(
                    ProfilePlayerFragDirections.actionFriendProfileInfoFragmentToEventDescRedesignFrag2(
                        it,id.toString()
                    )
                )
            }

            recyclerView.adapter = adapter

            /* val adapter = EventsRedesAdapter()
             recyclerView.adapter = adapter

             recyclerView.setHasFixedSize(true)
             viewLifecycleOwner.lifecycleScope.launch {

                 viewModel.eventsProfilePlayer(id).collect {
                     adapter.submitData(it)
                     val size = adapter.snapshot().size
                     binding.eventsCountTv.text = "${size}"
                     if (size == 0) {
                         binding.emptyListEventsTv.visible()
                     }
                 }
             }*/


        }
    }

    private fun setupFriends(id: Int) {


        val recyclerView = binding.friendsRv
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)
        val adapter = friendAdapter
        recyclerView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchUsers(userId = id.toString()).collect {
                /*val size = adapter.snapshot().size
                binding.friendCountTv.text = "${size}"*/
                /*if (size == 0) {
                    binding.emptyListFriendTv.visible()
                }*/
                adapter.submitData(it)
            }
        }
        /*adapter.selectItem {
            findNavController().navigate(

                ProfilePlayerFragDirections.actionFriendProfileInfoFragmentSelf(
                    it.id.toString()
                )
            )
        }*/
    }


}