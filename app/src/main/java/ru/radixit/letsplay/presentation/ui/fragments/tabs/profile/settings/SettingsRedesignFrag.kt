package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.DialogExitProfileRedesBinding
import ru.radixit.letsplay.databinding.FragSettingsRedesignBinding
import ru.radixit.letsplay.presentation.ui.MainActivity


@AndroidEntryPoint
class SettingsRedesignFrag : DialogFragment() {
    private var _binding: FragSettingsRedesignBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.FullScreenDialogStyle
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragSettingsRedesignBinding.inflate(inflater, container, false)
        onBack()
        actionToEditFragment()
        actionToNotificationsFragment()
        actionToBlackListFragment()
        exitFromProfile()
        binding.saveSettings.setOnClickListener {
            findNavController().navigate(SettingsRedesignFragDirections.actionSettingsRedesignFragToSettingsSaveRedesFrag())
        }
        viewClicks()
        return binding.root
    }

    private fun viewClicks() {
        with(binding){
            exitFromProfile.setOnClickListener {
                showDialog()
            }
            edit.setOnClickListener {
                findNavController().navigate(SettingsRedesignFragDirections.actionSettingsRedesignFragToEditProfileRedesFrag())
            }
            saveSettings.setOnClickListener {
                findNavController().navigate(SettingsRedesignFragDirections.actionSettingsRedesignFragToSettingsSaveRedesFrag())
            }
            notifSettings.setOnClickListener {
                findNavController().navigate(SettingsRedesignFragDirections.actionSettingsRedesignFragToNotifSettingRedesFrag())
            }
        }
    }

    private fun showDialog() {
        val customDialog = Dialog(requireActivity())
        val bindingDialog = DialogExitProfileRedesBinding.inflate(layoutInflater)
        customDialog.setContentView(bindingDialog.root)
        bindingDialog.cancelText.setOnClickListener {
            customDialog.dismiss()
        }
        bindingDialog.exitText.setOnClickListener {
            viewModel.exitFromProfile()
            requireActivity().startActivity(
                Intent(
                    requireActivity(),
                    MainActivity::class.java
                )
            )
            requireActivity().finish()
            customDialog.dismiss()
        }
        customDialog.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.show()
    }

    private fun exitFromProfile() {
        binding.exitFromProfile.setOnClickListener {
            showAlertDialog()
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_DARK)
        builder.setMessage("Вы уверены, что хотите выйти?")
            .setCancelable(true)
            .setPositiveButton(
                "Да"
            ) { dialog, _ ->
                viewModel.exitFromProfile()
                requireActivity().startActivity(
                    Intent(
                        requireActivity(),
                        MainActivity::class.java
                    )
                )
                requireActivity().finish()
                dialog.cancel()
            }
            .setNegativeButton(
                "Отмена"
            ) { dialog, _ -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()
        val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        negativeButton.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.clicked_text_color
            )
        )
        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
    }

    private fun actionToBlackListFragment() {
        binding.blackListConst.setOnClickListener {
                findNavController().navigate(SettingsRedesignFragDirections.actionSettingsRedesignFragToBlackListRedesFrag())
        }
    }

    private fun actionToNotificationsFragment() {
        binding.notifications.setOnClickListener {
            findNavController().navigate(SettingsRedesignFragDirections.actionSettingsRedesignFragToNotifSettingRedesFrag())
        }
    }

    private fun actionToEditFragment() {
        binding.edit.setOnClickListener {
            findNavController().navigate(SettingsRedesignFragDirections.actionSettingsRedesignFragToEditProfileRedesFrag())
        }
    }

    private fun onBack() {
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}