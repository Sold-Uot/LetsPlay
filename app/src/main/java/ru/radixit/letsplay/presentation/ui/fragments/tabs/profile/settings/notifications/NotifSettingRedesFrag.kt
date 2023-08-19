package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.network.response.NotificationsSettingsResponse
import ru.radixit.letsplay.databinding.FragSettingNotificationRedesBinding

@AndroidEntryPoint
class NotifSettingRedesFrag : DialogFragment() {

    private var _binding: FragSettingNotificationRedesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationsViewModel by viewModels()

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
        _binding = FragSettingNotificationRedesBinding.inflate(inflater, container, false)
        viewModel.fetchNotificationsSettings()
        setupValueInviteSwitch()
        setupValueEventsSwitch()
        setupValueFriendsSwitch()
        setupValueMessagesSwitch()
        onBack()
        return binding.root
    }

    private fun onBack() {
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.checkSaveToolbar.setOnClickListener {
            viewModel.saveNotificationsSettings(
                NotificationsSettingsResponse(
                    noticeInvitation = binding.invitationsSw.isChecked,
                    noticeEvent = binding.eventsSw.isChecked,
                    noticeFriend = binding.addingFriendsSw.isChecked,
                    noticeMessage = binding.chatMessagesSw.isChecked
                )
            ){
                if(it){
                    findNavController().popBackStack()
                }
            }
        }
    }


    private fun setupValueInviteSwitch() {
        viewModel.invite.observe(viewLifecycleOwner) {
            binding.invitationsSw.isChecked = it
        }
    }


    private fun setupValueEventsSwitch() {
        viewModel.events.observe(viewLifecycleOwner) {
            binding.eventsSw.isChecked = it
        }
    }


    private fun setupValueFriendsSwitch() {
        viewModel.friends.observe(viewLifecycleOwner) {
            binding.addingFriendsSw.isChecked = it
        }
    }


    private fun setupValueMessagesSwitch() {
        viewModel.messages.observe(viewLifecycleOwner) {
            binding.chatMessagesSw.isChecked = it
        }
    }
}