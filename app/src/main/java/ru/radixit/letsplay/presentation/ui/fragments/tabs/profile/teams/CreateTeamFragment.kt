package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragmentCreatTeamBinding

@AndroidEntryPoint
class CreateTeamFragment : DialogFragment() {

    private var _binding : FragmentCreatTeamBinding? = null
    private val binding get() = _binding!!

    private val vm  by viewModels<CreateTeamViewModel>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentCreatTeamBinding.inflate(inflater,container,false)

        return binding.root


    }


}