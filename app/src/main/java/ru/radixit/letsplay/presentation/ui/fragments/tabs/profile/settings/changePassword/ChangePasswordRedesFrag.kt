package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings.changePassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.FragChangePasswordRedesBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings.change_email.ChangeEmailRedesFragDirections

@AndroidEntryPoint
class ChangePasswordRedesFrag : Fragment() {
    private lateinit var binding: FragChangePasswordRedesBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragChangePasswordRedesBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.continueMatBtn.setOnClickListener {
            findNavController().navigate(ChangeEmailRedesFragDirections.actionChangeEmailRedesFragToConfirmCodeEmailFrag())
        }
        binding.toolbar2.setNavigationOnClickListener{
            findNavController().popBackStack()
        }
    }
}