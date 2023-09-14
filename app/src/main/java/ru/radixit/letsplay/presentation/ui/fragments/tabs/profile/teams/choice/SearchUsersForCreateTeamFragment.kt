package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams.choice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.FragmentSearchUsersForCreateTeamBinding
import ru.radixit.letsplay.presentation.global.BaseFragment

@AndroidEntryPoint

class SearchUsersForCreateTeamFragment : BaseFragment() {
    private var _binding: FragmentSearchUsersForCreateTeamBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentSearchUsersForCreateTeamBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}