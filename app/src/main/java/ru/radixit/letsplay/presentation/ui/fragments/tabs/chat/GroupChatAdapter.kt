package ru.radixit.letsplay.presentation.ui.fragments.tabs.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.UserMessage
import ru.radixit.letsplay.databinding.ItemAnotherChatBinding
import ru.radixit.letsplay.databinding.ItemUserChatBinding
import ru.radixit.letsplay.utils.gone

/**
 * Адаптер для группового чата
 */
class GroupChatAdapter(private val id: Int,private val isShowAvatar: Boolean) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var dataList = mutableListOf<UserMessage>()

    inner class UserChatViewHolder(private val binding: ItemUserChatBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(chat: UserMessage) {
            Log.d("isShowAvatar","isShowAvatar = ${isShowAvatar}")
            binding.message.text = chat.messageText
            binding.date.text = chat.createdAt.split(" ")[1]
        }
    }

    inner class AnotherChatViewHolder(private val binding: ItemAnotherChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: UserMessage) {
            binding.message.text = chat.messageText
            binding.date.text = chat.createdAt.split(" ")[1]
            binding.name.text = chat.name

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when (viewType) {
        1 -> {
            val layoutInflater:LayoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemUserChatBinding.inflate(layoutInflater, parent, false)
            UserChatViewHolder(binding)
        }
        else -> {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemAnotherChatBinding.inflate(layoutInflater, parent, false)
            AnotherChatViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1) {
            (holder as UserChatViewHolder).bind(dataList[position])
        } else {
            (holder as AnotherChatViewHolder).bind(dataList[position])
        }
    }

    fun addItem(message: UserMessage) {
        dataList.add(message)
        notifyDataSetChanged()
    }

    fun setData(list: List<UserMessage>) {
        dataList = list.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataList[position].userId == id) {
            1
        } else {
            2
        }
    }

}
