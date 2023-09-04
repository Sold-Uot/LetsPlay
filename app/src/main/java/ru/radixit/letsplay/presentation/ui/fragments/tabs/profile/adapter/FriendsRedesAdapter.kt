package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter

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
import ru.radixit.letsplay.databinding.ItemFriendsProfRvRedesBinding

typealias SelectItemOnClickListener = ((User) -> Unit)
typealias ShowActionsOnClickListener = ((String) -> Unit)

class FriendsRedesAdapter :
    PagingDataAdapter<User, FriendsRedesAdapter.FriendRedesViewHolder>(FriendComparator) {

    private var selectItemOnClickListener: SelectItemOnClickListener? = null
    private var showActionsOnClickListener: ShowActionsOnClickListener? = null

    class FriendRedesViewHolder(private val binding: ItemFriendsProfRvRedesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): FriendRedesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFriendsProfRvRedesBinding.inflate(layoutInflater, parent, false)
                return FriendRedesViewHolder(binding)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(
            friend: User,
            selectItemOnClickListener: SelectItemOnClickListener,

        ) {
            binding.playerNameTv.text = friend.name ?: "Не указано"
//            binding.playerPosition.text = (friend.userType ?: "Не указано").toString()
            if (friend.photo == null) {
                binding.itemAvatarImg.visibility = View.GONE
                binding.nameOnAvatar.visibility = View.VISIBLE
                binding.nameOnAvatar.text = "${friend.name.toString()[0]}"
                binding.playingMarCard.visibility = View.VISIBLE
                val cardColor = ContextCompat.getColor(
                    itemView.context,
                    R.color.violet
                )
                binding.playingMarCard.setCardBackgroundColor(cardColor)
            } else {
                binding.itemAvatarImg.visibility = View.VISIBLE
                binding.nameOnAvatar.visibility = View.GONE
                binding.playingMarCard.visibility = View.GONE
                Glide.with(binding.root).load(friend.photo.url).into(binding.itemAvatarImg)
            }
            itemView.setOnClickListener {
                selectItemOnClickListener.invoke(friend)
            }

//            binding.showActions.setOnClickListener {
//                showActionsOnClickListener.invoke(friend.id.toString())
//            }
        }
    }

    fun selectItem(listener: SelectItemOnClickListener?) {
        selectItemOnClickListener = listener
    }

    fun showActions(listener: ShowActionsOnClickListener?) {
        showActionsOnClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRedesViewHolder {
        return FriendRedesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FriendRedesViewHolder, position: Int) {
        holder.bind(getItem(position)!!, selectItemOnClickListener!!)
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
