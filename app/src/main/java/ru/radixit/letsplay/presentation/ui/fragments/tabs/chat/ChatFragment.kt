package ru.radixit.letsplay.presentation.ui.fragments.tabs.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragmentChatBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.utils.SpaceItemDecoration
import java.util.Timer
import java.util.TimerTask

/**
 * Экран чатов
 */
@AndroidEntryPoint
class ChatFragment : BaseFragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatViewModel by viewModels()
    private val adapter = ChatAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        recyclerView.addItemDecoration(SpaceItemDecoration(10))
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fetchChats()
                .collect {
                    adapter.submitData(it)
                }
        }



        binding.createChat.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.chatFragment) {
                findNavController().navigate(R.id.action_chatFragment_to_createChatFragment)

                }
        }

//        lifecycleScope.launch {
//            adapter.loadStateFlow.collect { loadState ->
//                val isListEmpty =
//                    loadState.source.refresh is LoadState.NotLoading && adapter.itemCount == 0
//                binding.emptyList.isVisible = isListEmpty
//                recyclerView.isVisible =
//                    loadState.source.refresh is LoadState.NotLoading
//                binding.retryButton.isVisible =
//                    loadState.source.refresh is LoadState.Error && adapter.itemCount == 0
//            }
//        }
        adapter.clickItem {
        }



        viewModel.refreshState.observe(viewLifecycleOwner){
            viewModel.viewModelScope.launch {
                adapter.submitData(it)
            }
        }




        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.recyclerView.adapter = adapter
        refreshList()
    }


    override fun onStop() {
        super.onStop()
        refreshList().cancel()

    }
    private fun refreshList():Timer{

        val timer = Timer()
        timer.schedule(object : TimerTask() {

            override fun run() {
               adapter.refresh()
            }
        }, 0, 3500)

        return timer
    }


}