package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.FriendEntity
import ru.radixit.letsplay.databinding.FragmentListInviteFriendBinding
import ru.radixit.letsplay.databinding.FragmentListTeamPlayersBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes.ListInviteFriendAdapter


class ListInviteFriendFragment : DialogFragment() {
    private var _binding :FragmentListInviteFriendBinding? = null
    private val binding get() = _binding!!

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

    private fun setupRecycler(){
        val adapter = ListInviteFriendAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        adapter.setData(listOf(FriendEntity(1,"asd",1,"asd","asd","asd","Asd")))

        vm.listSelectFriends.observe(viewLifecycleOwner){
            adapter.setData(it)
        }
        binding.recyclerView.adapter = adapter
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding= null
    }


}