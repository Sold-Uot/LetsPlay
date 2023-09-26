package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.data.model.Member
import ru.radixit.letsplay.data.model.MemberList
import ru.radixit.letsplay.data.network.request.DeletePlayerRequest
import ru.radixit.letsplay.databinding.FragmentListTeamPlayersBinding
import ru.radixit.letsplay.utils.SpaceItemDecoration
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.visible

@AndroidEntryPoint
class ListTeamPlayers : Fragment() {
    private var _binding: FragmentListTeamPlayersBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ListTeamPlayersViewModel>()

    private val args by navArgs<ListTeamPlayersArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListTeamPlayersBinding.inflate(inflater, container, false)

        setupView()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setupView() {
        setupRecyclerView()
        clickView()
    }

    private fun setupRecyclerView() {
        with(binding) {

            Log.e("iddd" , args.userId.toString())
            val adapter = ListTeamPlayersAdapter(args.myTeam ,args.userId)
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            viewModel.fetchPlayersList(args.id)

            viewModel.playerList.observe(viewLifecycleOwner) {
                Log.e("observer", it.list.toString())
                if (it.list!!.isEmpty()) {
                    recyclerView.gone()
                    emptyListTv.visible()
                } else {
                    adapter.setData(it.list!!)
                    recyclerView.adapter = adapter
                }

            }
            adapter.onClick {

                viewModel.deletePLayer(args.id, listOf(it.id))
            }
            recyclerView.addItemDecoration(SpaceItemDecoration(50))


            recyclerView.adapter = adapter


        }
    }

    private fun clickView() {
        onBack()

    }

    private fun onBack() {
        with(binding) {
            toolbar2.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
