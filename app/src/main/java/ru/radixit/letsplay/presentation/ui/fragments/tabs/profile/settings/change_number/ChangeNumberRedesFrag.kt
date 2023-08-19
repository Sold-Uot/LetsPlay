package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings.change_number

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.FragChangeNumberPhoneRedesBinding

@AndroidEntryPoint
class ChangeNumberRedesFrag : Fragment() {
    private lateinit var binding:FragChangeNumberPhoneRedesBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragChangeNumberPhoneRedesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.reviewsMatBtn.setOnClickListener {
            findNavController().navigate(ChangeNumberRedesFragDirections.actionChangeNumberRedesFragToConfirmNumberRedesFrag())
        }
    }
}