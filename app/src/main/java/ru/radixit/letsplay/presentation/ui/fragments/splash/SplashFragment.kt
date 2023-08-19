package ru.radixit.letsplay.presentation.ui.fragments.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragmentSplashBinding


@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        viewModel.splashNavCommand.observe(viewLifecycleOwner) { splashNavCommand ->
            val navController = Navigation.findNavController(
                requireActivity(),
                R.id.nav_host_fragment_activity_main
            )

            val mainGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
            mainGraph.setStartDestination(when (splashNavCommand) {
                SplashNavCommand.NAVIGATE_TO_MAIN -> R.id.rootFragment
                SplashNavCommand.NAVIGATE_TO_AUTH -> R.id.authFragment
                null -> throw IllegalArgumentException("Illegal splash navigation command")
            })

            navController.graph = mainGraph
        }
        return binding.root
    }
}