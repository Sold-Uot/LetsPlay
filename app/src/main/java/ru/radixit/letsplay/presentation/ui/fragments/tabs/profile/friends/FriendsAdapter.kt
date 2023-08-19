package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.friends

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
import ru.radixit.letsplay.databinding.ItemFriendsRecyclerBinding

typealias SelectItemOnClickListener = ((User) -> Unit)
typealias ShowActionsOnClickListener = ((String) -> Unit)

class FriendsAdapter :
    PagingDataAdapter<User, FriendsAdapter.FriendViewHolder>(FriendComparator) {

    private var selectItemOnClickListener: SelectItemOnClickListener? = null
    private var showActionsOnClickListener: ShowActionsOnClickListener? = null

    class FriendViewHolder(private val binding: ItemFriendsRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): FriendViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFriendsRecyclerBinding.inflate(layoutInflater, parent, false)
                return FriendViewHolder(binding)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(
            friend: User,
            selectItemOnClickListener: SelectItemOnClickListener,
            showActionsOnClickListener: ShowActionsOnClickListener
        ) {
            binding.playerName.text = friend.name ?: "Не указано"
            binding.playerPosition.text = (friend.userType ?: "Не указано").toString()
            if (friend.photo == null) {
                binding.photo.visibility = View.GONE
                binding.nameOnAvatar.visibility = View.VISIBLE
                binding.nameOnAvatar.text = "${friend.name.toString()[0]}"
                binding.constraint.visibility = View.VISIBLE
                binding.constraint.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.violet
                    )
                )
            } else {
                binding.photo.visibility = View.VISIBLE
                binding.nameOnAvatar.visibility = View.GONE
                binding.constraint.visibility = View.GONE
                Glide.with(binding.root).load(friend.photo.url).into(binding.photo)
            }
            itemView.setOnClickListener {
                selectItemOnClickListener.invoke(friend)
            }

            binding.showActions.setOnClickListener {
                showActionsOnClickListener.invoke(friend.id.toString())
            }
        }
    }

    fun selectItem(listener: SelectItemOnClickListener?) {
        selectItemOnClickListener = listener
    }

    fun showActions(listener: ShowActionsOnClickListener?) {
        showActionsOnClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        return FriendViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(getItem(position)!!, selectItemOnClickListener!!, showActionsOnClickListener!!)
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
