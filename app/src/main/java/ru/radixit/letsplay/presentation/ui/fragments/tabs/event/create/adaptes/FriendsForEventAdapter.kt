package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.databinding.ItemUserForEventBinding


typealias SelectUserOnClickListener = ((User) -> Unit)
typealias RemoveUserOnClickListener = ((User) -> Unit)

class FriendsForEventAdapter :
    PagingDataAdapter<User, FriendsForEventAdapter.FriendsForEventViewHolder>(FriendComparator) {

    private var selectItemOnClickListener: SelectUserOnClickListener? = null
    private var removeItemOnClickListener: RemoveUserOnClickListener? = null

    class FriendsForEventViewHolder(private val binding: ItemUserForEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): FriendsForEventViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemUserForEventBinding.inflate(layoutInflater, parent, false)
                return FriendsForEventViewHolder(binding)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(
            friend: User,
            selectItemOnClickListener: SelectUserOnClickListener,
            removeItemOnClickListener: RemoveUserOnClickListener
        ) {
            binding.playerName.text = friend.name ?: "Не указано"
            binding.playerPosition.text = (friend.userType ?: "Не указано").toString()
            if (friend.photo == null) {
                binding.photo.visibility = View.GONE
                binding.nameOnAvatar.visibility = View.VISIBLE
                binding.nameOnAvatar.text =
                    "${friend.name.toString()[0]}${friend.surname.toString()[0]}"
                binding.constraint.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.violet
                    )
                )
            } else {
                binding.photo.visibility = View.VISIBLE
                binding.nameOnAvatar.visibility = View.GONE
                Glide.with(binding.root).load(friend.photo.url).into(binding.photo)
            }

            binding.checkBox5.setOnClickListener {
                if (binding.checkBox5.isChecked) {
                    selectItemOnClickListener.invoke(friend)
                } else {
                    removeItemOnClickListener.invoke(friend)
                }
            }
        }
    }

    fun selectItem(listener: SelectUserOnClickListener?) {
        selectItemOnClickListener = listener
    }

    fun removeItem(listener: RemoveUserOnClickListener?) {
        removeItemOnClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsForEventViewHolder {
        return FriendsForEventViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FriendsForEventViewHolder, position: Int) {
        holder.bind(getItem(position)!!, selectItemOnClickListener!!, removeItemOnClickListener!!)
    }


    object FriendComparator : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
