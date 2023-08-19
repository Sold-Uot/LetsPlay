package ru.radixit.letsplay.presentation.ui.fragments.root

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragmentRootBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.utils.setupWithNavControllers

/**
 * Экран
 */
@AndroidEntryPoint
class RootFragment : BaseFragment() {

    private var _binding: FragmentRootBinding? = null
    private val binding get() = _binding!!
    private var currentNavController: LiveData<NavController>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRootBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
    }

    private fun setupBottomNavigationBar() {
        val bottomNavigationView = binding.navView

        val navGraphIds = listOf(
            R.navigation.playgrounds_graph,
            R.navigation.event_graph,
            R.navigation.chat_graph,
            R.navigation.profile_graph
        )

        val controller = bottomNavigationView.setupWithNavControllers(
            navGraphIds = navGraphIds,
            fragmentManager = childFragmentManager,
            containerId = R.id.nav_host_fragment_root,
            intent = requireActivity().intent
        )

        controller.observe(viewLifecycleOwner) { navController ->
            navigationOnApp(navController, bottomNavigationView)
        }
        currentNavController = controller
    }

    private fun navigationOnApp(navController: NavController, navView: BottomNavigationView) {
        navController.addOnDestinationChangedListener { _, _, _ ->

            when (navController.currentDestination?.id) {
                R.id.listPlaygroundsFragment -> navView.visibility = View.VISIBLE
                R.id.listEventsRedesFrag -> navView.visibility = View.VISIBLE
                R.id.fragChatRedes -> navView.visibility = View.VISIBLE
                R.id.profileRedesignFrag -> navView.visibility = View.VISIBLE
                else -> navView.visibility = View.GONE
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        setupBottomNavigationBar()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}