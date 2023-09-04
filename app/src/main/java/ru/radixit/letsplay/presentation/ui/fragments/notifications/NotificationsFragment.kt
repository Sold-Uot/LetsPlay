package ru.radixit.letsplay.presentation.ui.fragments.notifications

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragmentNotificationsBinding


/**
 * Экран уведомление
 */
@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext())
//        val notfList = mutableListOf<Notification>()
//        for(i in 0..20){
//            notfList.add(Notification("time","title",0, Type("label","value")))
//        }
//        val pagingData: PagingData<Notification> = PagingData.from(notfList)

        val adapter = NotificationsAdapter()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchNotifications().cachedIn(viewLifecycleOwner.lifecycleScope)
                .collectLatest {
                    adapter.submitData(it)
                }
        }
        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                Log.w("Notification", loadState.source.refresh.toString())
                val isListEmpty =
                    loadState.source.refresh is LoadState.NotLoading && adapter.itemCount == 0
                binding.emptyList.isVisible = isListEmpty
                recyclerView.isVisible =
                    loadState.source.refresh is LoadState.NotLoading
                binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                binding.retryButton.isVisible =
                    loadState.source.refresh is LoadState.Error && adapter.itemCount == 0
            }
        }
        binding.swipeToRefresh.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.searchNotifications().collectLatest {
                    adapter.submitData(it)
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                adapter.loadStateFlow.collect { loadState ->
                    binding.emptyList.isVisible =
                        loadState.source.refresh is LoadState.NotLoading && adapter.itemCount == 0
                    recyclerView.isVisible =
                        loadState.source.refresh is LoadState.NotLoading
                    binding.swipeToRefresh.isRefreshing =
                        loadState.source.refresh is LoadState.Loading
                    binding.progressBar.isVisible = false
                    binding.retryButton.isVisible =
                        loadState.source.refresh is LoadState.Error && adapter.itemCount == 0
                }
            }
        }
        binding.retryButton.setOnClickListener {
            adapter.refresh()
        }
        fun desc(type: String) = when (type) {
            "2" -> "Вам было отправлено приглашение на участие в событии"
            "7" -> "Вам было отправлено заявка в друзья"
            "13" -> "Принял(-а) заявку в друзья"
            else -> "Неизвестно"
        }
        adapter.selectItem { _it ->
            val customDialog = Dialog(requireActivity())
            customDialog.setContentView(R.layout.dialog_accept_or_rejec)
            val title = customDialog.findViewById(R.id.title) as TextView
            val typeLabel = customDialog.findViewById(R.id.typeLabel) as TextView
            val cancelBtn = customDialog.findViewById(R.id.cancel) as ImageView
            val acceptBtn = customDialog.findViewById(R.id.acceptBtn) as ConstraintLayout
            val rejectBtn = customDialog.findViewById(R.id.rejectBtn) as TextView
            cancelBtn.setOnClickListener {
                customDialog.dismiss()
            }
            title.text = _it.title
            typeLabel.text = desc(_it.type.value.toString())
            acceptBtn.setOnClickListener {

                if (_it.type.value == 7) {
                    viewModel.acceptFriend(_it.id.toString())
                }
                customDialog.dismiss()
            }
            rejectBtn.setOnClickListener {
                if (_it.type.value == 7) {
                    viewModel.rejectFriend(_it.id.toString())
                }
                customDialog.dismiss()
            }
            customDialog.window?.setLayout(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            customDialog.show()
        }
        recyclerView.adapter = adapter
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }
}