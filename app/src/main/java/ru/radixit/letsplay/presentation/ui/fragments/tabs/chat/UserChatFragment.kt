package ru.radixit.letsplay.presentation.ui.fragments.tabs.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.UserMessage
import ru.radixit.letsplay.databinding.FragmentUserChatBinding
import ru.radixit.letsplay.utils.SpaceItemDecoration

@AndroidEntryPoint
class UserChatFragment : DialogFragment() {

    private var _binding: FragmentUserChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserChatViewModel by viewModels()
    private val args by navArgs<UserChatFragmentArgs>()
    private var adapter: UserChatAdapter? = null
    private var userName : String? = null
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
        _binding = FragmentUserChatBinding.inflate(inflater, container, false)
        onBack()
        viewModel.fetchChatInfo(args.chatType, args.receiverId)
        viewModel.chatInfo.observe(viewLifecycleOwner) {
            userName = it.name
            binding.profileName.text = it.name ?: ""
            binding.sport.text = it.userType ?: ""
            if (it.photo == null) {
                binding.nameOnAvatar.visibility = View.VISIBLE
                binding.nameOnAvatar.text = "${it.name.toString()[0]}"
                binding.constraint.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.violet
                    )
                )
            } else {
                binding.nameOnAvatar.visibility = View.GONE
                Glide.with(binding.root).load(it.photo.url).into(binding.photo)
            }
        }
        val recyclerView = binding.recyclerView
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        adapter = UserChatAdapter(viewModel.fetchToken() , args.chatusername)
        recyclerView.addItemDecoration(SpaceItemDecoration(30))

        viewModel.fetchChat(args.receiverId, args.chatType)
        viewModel.messages.observe(viewLifecycleOwner) {
            adapter!!.setData(it)
        }
        viewModel.fetchMessage().observe(viewLifecycleOwner) {
            adapter!!.addItem(
                UserMessage(
                    userId = it.data.userId,
                    messageText = it.data.messageText,
                    createdAt = it.data.createdAt
                )
            )
            binding.recyclerView.smoothScrollToPosition(adapter?.itemCount!! - 1)
        }
        binding.send.setOnClickListener {
            viewModel.sendMessage(
                chatType = args.chatType,
                receiverId = args.receiverId,
                senderId = viewModel.fetchToken(),
                message = binding.searchEvents.text.toString()
            )

            binding.searchEvents.text.clear()
        }
        binding.profile.setOnClickListener { }
        recyclerView.adapter = adapter
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return binding.root
    }

    private fun onBack() {
        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}