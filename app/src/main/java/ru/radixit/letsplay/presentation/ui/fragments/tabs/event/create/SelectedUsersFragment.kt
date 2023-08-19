package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.FragmentSelectedUsersBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes.SelectedUsersAdapter
import ru.radixit.letsplay.utils.SpaceItemDecoration
import ru.radixit.letsplay.utils.hideKeyboardOnScroll

@AndroidEntryPoint
class SelectedUsersFragment : BaseFragment() {

    private var _binding: FragmentSelectedUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreateEventViewModel


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectedUsersBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[CreateEventViewModel::class.java]

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SelectedUsersAdapter()

        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SpaceItemDecoration(40))
        viewModel.selectedUsers.observe(viewLifecycleOwner) {
            adapter.setData(it!!)
            binding.foundNumber.text = "Найдено: ${adapter.itemCount}"
        }
        adapter.selectItem {
            viewModel.remove(it)
        }
        recyclerView.setOnTouchListener { view, _ ->
            requireContext().hideKeyboardOnScroll(view)
        }

        return binding.root
    }


}