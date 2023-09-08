package ru.radixit.letsplay.presentation.ui.fragments.tabs.chat

import android.annotation.SuppressLint
import android.util.Log
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
import ru.radixit.letsplay.utils.visible

class UserChatAdapter(private val id: Int, private var userName:String) :
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
            Log.e("chat", chat.toString())
            binding.name.text = if (userName != null ) userName else chat.name.toString()
            binding.message.text = chat.messageText
            binding.date.text = chat.createdAt.split(" ")[1]



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
