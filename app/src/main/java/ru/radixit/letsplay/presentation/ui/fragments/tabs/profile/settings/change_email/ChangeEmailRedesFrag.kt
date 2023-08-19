package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings.change_email

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.FragChangeEmailRedesBinding

@AndroidEntryPoint
class ChangeEmailRedesFrag : Fragment() {
    private lateinit var binding:FragChangeEmailRedesBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragChangeEmailRedesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.continueMatBtn.setOnClickListener {
            findNavController().navigate(ChangeEmailRedesFragDirections.actionChangeEmailRedesFragToConfirmCodeEmailFrag())
        }
    }
}