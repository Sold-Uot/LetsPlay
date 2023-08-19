package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.FragmentPlaygroundsBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.fragments.ListPlaygroundsFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.fragments.MapsFragment

@AndroidEntryPoint
class PlaygroundsFragment : BaseFragment() {

    private var _binding: FragmentPlaygroundsBinding? = null
    private val binding get() = _binding!!
    private val arrayListFragments = arrayListOf(ListPlaygroundsFragment(), MapsFragment())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaygroundsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedDefaultFrag()
//        binding.listMapsBtn.setOnClickListener {
//            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.frag_container_view,MapsFragment())?.commit()
//        }
//        binding.addPlaygrounds.setOnClickListener {
//            if (findNavController().currentDestination?.id == R.id.playgroundsFragment) {
//                findNavController().navigate(R.id.action_playgroundsFragment_to_createPlaygroundFragment)
//            }
//        }
//        binding.notificationBellConst.setOnClickListener {
//            if (findNavController().currentDestination?.id == R.id.playgroundsFragment) {
//                findNavController().navigate(R.id.action_playgroundsFragment_to_notificationsFragment3)
//            }
//        }

    }

    private fun selectedDefaultFrag() {
//        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.frag_container_view,ListPlaygroundsFragment())?.commit()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}