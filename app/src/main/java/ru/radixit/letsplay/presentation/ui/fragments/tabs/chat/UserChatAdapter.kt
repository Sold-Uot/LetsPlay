package ru.radixit.letsplay.presentation.ui.fragments.tabs.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_notifications.view.*
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.UserMessage
import ru.radixit.letsplay.databinding.ItemAnotherChatBinding
import ru.radixit.letsplay.databinding.ItemUserChatBinding
import ru.radixit.letsplay.utils.gone

class UserChatAdapter(private val id: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var dataList = mutableListOf<UserMessage>()

    class UserChatViewHolder(private val binding: ItemUserChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): UserChatViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemUserChatBinding.inflate(layoutInflater, parent, false)
                return UserChatViewHolder(binding)
            }
        }

        fun bind(chat: UserMessage) {
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
                    Glide.with(binding.root).load(chat.photo.url).into(binding.photo)
                }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when (viewType) {
        1 -> UserChatViewHolder.from(parent)
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

    override fun getItemCount() = dataList.size

    override fun getItemViewType(position: Int) = if (dataList[position].userId == id) 1 else 2

}
