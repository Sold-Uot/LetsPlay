package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams.choice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragmentSelectedUsersForCreateTeamBinding
import ru.radixit.letsplay.presentation.global.BaseFragment


class SelectedUsersForCreateTeamFragment : BaseFragment() {

    private var _binding  : FragmentSelectedUsersForCreateTeamBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        _binding = FragmentSelectedUsersForCreateTeamBinding.inflate(inflater,  container , false )
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}