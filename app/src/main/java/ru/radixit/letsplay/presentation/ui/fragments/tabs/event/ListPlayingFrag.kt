package ru.radixit.letsplay.presentation.ui.fragments.tabs.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.network.response.EventMembersResp
import ru.radixit.letsplay.databinding.FragListPlayingBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter.ListPlayerRedesAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.friends.FriendsAdapter

@AndroidEntryPoint
class ListPlayingFrag : DialogFragment() {
    private lateinit var binding:FragListPlayingBinding
    private val viewModel: EventDescriptionViewModel by viewModels()

    private val args by navArgs<ListPlayingFragArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.FullScreenDialogStyle
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragListPlayingBinding.inflate(layoutInflater)

        setupRecyclerView()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    private fun setupRecyclerView(){
        with(binding){
            listPlayingRv.layoutManager = LinearLayoutManager(requireContext())
            val adapter = ListPlayerRedesAdapter{}
            viewModel.listEventsMembers(args.id)

            viewModel.eventMemberList.observe(viewLifecycleOwner){
                adapter.setData(it)
                binding.foundCountTv.text = it.size.toString()
            }
            adapter.selectItem {  }

            listPlayingRv.adapter = adapter

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}