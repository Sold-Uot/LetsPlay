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
import ru.radixit.letsplay.data.model.FriendEntity
import ru.radixit.letsplay.data.model.Photo
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.databinding.FragmentListInviteFriendBinding
import ru.radixit.letsplay.databinding.FragmentListTeamPlayersBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes.ListInviteFriendAdapter
import ru.radixit.letsplay.utils.SpaceItemDecoration

@AndroidEntryPoint
class ListInviteFriendFragment : DialogFragment() {
    private var _binding :FragmentListInviteFriendBinding? = null
    private val binding get() = _binding!!
    private val adapter = ListInviteFriendAdapter()
    private val vm by viewModels<CreateEventViewModel>()
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
       _binding = FragmentListInviteFriendBinding.inflate(inflater,container,false)



        setupRecycler()

        return binding.root
    }

    private fun onBack(){

        binding.toolbar2.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    private fun setDataRecycler(){
        vm.fetchFriendList()
        vm.listSelectFriends.observe(viewLifecycleOwner){
            Log.e("31" , it.toString())
            adapter.setData(it)
        }
    }

    private fun setupRecycler(){

        binding.recyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)


        setDataRecycler()

        binding.swipeToRefresh.setOnRefreshListener {

            binding.swipeToRefresh.isRefreshing =false
        }

        adapter.deleteClick {
            vm.removeFriendToInviteList(User(id = it.id_user, name = it.name, Photo(it.photo_id,it.photo_url),it.surname, userType = it.userType,username = it.username))
            setDataRecycler()

        }
        binding.recyclerView.adapter = adapter

    }

    override fun onStart() {
        super.onStart()

    }



    override fun onDestroy() {
        super.onDestroy()
        _binding= null
    }


}