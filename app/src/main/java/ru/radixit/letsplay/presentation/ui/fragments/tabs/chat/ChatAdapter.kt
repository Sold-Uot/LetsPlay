package ru.radixit.letsplay.presentation.ui.fragments.tabs.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.Chat
import ru.radixit.letsplay.databinding.ItemChatPlayersBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.chat.tab_layout_frags.FragEventsChatRedes
import ru.radixit.letsplay.presentation.ui.fragments.tabs.chat.tab_layout_frags.FragPlayersChatRedes
import ru.radixit.letsplay.presentation.ui.fragments.tabs.chat.tab_layout_frags.FragTeamsChatRedes
import ru.radixit.letsplay.utils.visible

/**
 * Адаптер для списка чатов
 */

typealias ClickItem = (Chat) -> Unit
class ChatAdapter :
    PagingDataAdapter<Chat, ChatAdapter.ChatViewHolder>(CHAT_COMPARATOR) {
    private var fragment: BaseFragment? = null


    private var clickItem : ClickItem? = null
    fun setFragment(fragmentArgs: BaseFragment) {
        fragment = fragmentArgs
    }

    private fun determitateFrag():Boolean {
        when (fragment) {
            is FragPlayersChatRedes -> {
                return false
            }
            is FragTeamsChatRedes -> {
                return true
            }
            is FragEventsChatRedes -> {
                return false
            }
        }
        return false
    }
    inner class ChatViewHolder(private val binding: ItemChatPlayersBinding, private val clickItem: ClickItem) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(chat: Chat) {
            Log.e("chate" , chat.toString())
            binding.name.text = chat.name
            binding.message.text = chat.messageText ?: ""
            binding.messageDate.text = chat.updatedAt!!.split(" ")[1]
            if (chat.unreadNum != null && chat.unreadNum > 0) {
                binding.unread.visibility = View.VISIBLE
                binding.unreadTxt.text = chat.unreadNum.toString()
            } else {
                binding.unread.visibility = View.GONE
            }
            binding.parent.setOnClickListener {
                if (chat.chatType == 2) {
                    itemView.findNavController().navigate(
                        FragChatRedesDirections.actionChatFragmentToGroupChatFragment(
                            determitateFrag(),
                            chat.chatType,
                            chat.receiverId,
                        )
                    )
                } else {
                    itemView.findNavController().navigate(
                        ChatFragmentDirections.actionChatFragmentToUserChatFragment(
                            chat.chatType,
                            chat.receiverId,
                        )
                    )

                }
            }
            if (chat.photo == null) {
                binding.nameOnAvatar.visibility = View.VISIBLE
                binding.nameOnAvatar.text = if (chat.name?.length!! > 0) "${chat.name[0]}" else ""
                binding.constraint.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.violet
                    )
                )
            } else {
                binding.nameOnAvatar.visibility = View.GONE
                binding.photo.visible()
                Glide.with(binding.root).load(chat.photo.url).into(binding.photo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemChatPlayersBinding.inflate(layoutInflater, parent, false)
        return ChatViewHolder(binding , clickItem!!)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position)!!)

    }
    fun clickItem(listener : ClickItem){
        clickItem = listener
    }

    fun updateItem(id: Int){
        notifyItemChanged(id)
    }


    object CHAT_COMPARATOR : DiffUtil.ItemCallback<Chat>() {

        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }

}
