package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.databinding.FragSettingsSaveRedesBinding

@AndroidEntryPoint
class SettingsSaveRedesFrag : Fragment() {

    private var _binding: FragSettingsSaveRedesBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragSettingsSaveRedesBinding.inflate(inflater, container, false)
//        binding.materialCardViewChangePassword.setOnClickListener {
//            findNavController().navigate(R.id.action_settingsSaveFragment_to_changePasswordDialogFragment)
//        }
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        viewClicks()
        return binding.root
    }

    private fun viewClicks() {
        with(binding){
            showAllLastActivate.setOnClickListener {
                findNavController().navigate(SettingsSaveRedesFragDirections.actionSettingsSaveRedesFragToHistoryActiveFrag())
            }
            numberPhoneConst.setOnClickListener {
                findNavController().navigate(SettingsSaveRedesFragDirections.actionSettingsSaveRedesFragToChangeNumberRedesFrag())
            }
            mailConst.setOnClickListener {
                findNavController().navigate(SettingsSaveRedesFragDirections.actionSettingsSaveRedesFragToChangeEmailRedesFrag())
            }
            passwordConst.setOnClickListener {
                findNavController().navigate(SettingsSaveRedesFragDirections.actionSettingsSaveRedesFragToChangePasswordRedesFrag())
            }
        }
    }
}