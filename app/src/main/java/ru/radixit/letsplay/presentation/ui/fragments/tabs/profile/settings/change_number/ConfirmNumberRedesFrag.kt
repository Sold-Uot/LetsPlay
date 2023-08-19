package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings.change_number

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.FragConfirmCodeNumberRedesBinding

@AndroidEntryPoint
class ConfirmNumberRedesFrag : Fragment() {
    private lateinit var binding:FragConfirmCodeNumberRedesBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragConfirmCodeNumberRedesBinding.inflate(layoutInflater)
        return binding.root     
    }
}