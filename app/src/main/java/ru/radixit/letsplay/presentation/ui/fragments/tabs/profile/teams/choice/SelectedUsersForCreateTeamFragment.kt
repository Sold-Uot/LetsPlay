package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams.choice

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragmentSelectedUsersForCreateTeamBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes.SelectedUsersAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams.CreateTeamViewModel
import ru.radixit.letsplay.utils.SpaceItemDecoration
import ru.radixit.letsplay.utils.hideKeyboardOnScroll

@AndroidEntryPoint

class SelectedUsersForCreateTeamFragment : BaseFragment() {

    private var _binding  : FragmentSelectedUsersForCreateTeamBinding? = null
    private val binding get() = _binding!!

    private val adapter = SelectedUsersAdapter()

    private lateinit var viewModel : CreateTeamViewModel
//    private val viewModel by viewModels<CreateTeamViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        _binding = FragmentSelectedUsersForCreateTeamBinding.inflate(inflater,  container , false )

        viewModel = ViewModelProvider(requireActivity())[ CreateTeamViewModel::class.java]



        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        viewModel.test.observe(this.viewLifecycleOwner) {

        }
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SpaceItemDecoration(40))
        viewModel.selectedUsers.observe(viewLifecycleOwner) {
            Log.e("user_list2w" , it.size.toString())


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

    override fun onStart() {
        super.onStart()

        lifecycleScope.launchWhenStarted {
            viewModel.state.collect{
                binding.foundNumber.text = "Найдено: ${it}"

                Log.e("user_list2w" , it.toString())

            }
        }

        viewModel.test.observe(viewLifecycleOwner) {

            binding.foundNumber.text = "123"
        }
    }
    override fun onResume() {
        super.onResume()

        Log.e("resum" , viewModel.selectedUsers.value.toString())

    }




}