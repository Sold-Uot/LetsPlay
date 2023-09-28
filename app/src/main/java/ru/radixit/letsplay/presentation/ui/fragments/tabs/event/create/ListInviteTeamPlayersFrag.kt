package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.global.SessionManager
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.data.network.response.UserForTeamPlayers
import ru.radixit.letsplay.databinding.FragmentListInviteTeamPlayersBinding
import ru.radixit.letsplay.databinding.FragmentListTeamPlayersBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes.ListInviteTeamPlayersAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams.ListTeamPlayersAdapter
import javax.inject.Inject

@AndroidEntryPoint
class ListInviteTeamPlayersFrag : DialogFragment() {
    private var _binding:FragmentListInviteTeamPlayersBinding? = null
    private val binding get() = _binding!!

    private  var adapter  = ListInviteTeamPlayersAdapter()
    private val viewModel by viewModels<CreateEventViewModel>()
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
    ): View? {
        _binding = FragmentListInviteTeamPlayersBinding.inflate(inflater, container,false)
        viewModel.fetchTeamPlayers(arguments?.getInt("team_id")!!)
        setupRecyclerView()
        setupView()
        return binding.root
    }

    fun setupView(){
        with(binding){
            swipeToRefresh.setOnRefreshListener {
                adapter.notifyDataSetChanged()
                swipeToRefresh.isRefreshing = false
            }
        }
        onBack()
    }
    private fun onBack(){
        binding.toolbar2.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    fun setupRecyclerView(){



        binding.recyclerView.layoutManager = LinearLayoutManager(context)



        adapter?.removePlayerInInviteList {
            viewModel.removeFriendToInviteList(User(id =it.id,name =it.name ,photo =it.photo,surname = it.surname, username = it.username , userType = "Неизвестно"))

        }
        adapter?.addPlayerInInviteList {

            viewModel.addFriendToInviteList(User(id =it.id,name =it.name ,photo =it.photo,surname = it.surname, username = it.username, userType = "Неизвестно"))
        }

        viewModel.listTeamPlayers.observe(viewLifecycleOwner){
            adapter?.setData(it)
        }


        binding.recyclerView.adapter = adapter



    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }





}