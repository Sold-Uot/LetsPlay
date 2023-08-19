package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.data.network.response.EventMembersResp
import ru.radixit.letsplay.databinding.ItemFriendsRvRedesBinding

class PlayerRedesAdapter(
    private val click:(EventMembersResp.Member) -> Unit
):
    ListAdapter<EventMembersResp.Member, PlayerRedesAdapter.FriendRedesViewHolder>(FriendComparator) {

    private var selectItemOnClickListener: SelectItemOnClickListener? = null
    private var showActionsOnClickListener: ShowActionsOnClickListener? = null

    inner class FriendRedesViewHolder(private val binding: ItemFriendsRvRedesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(
            model: EventMembersResp.Member
        ) {
            binding.playerNameTv.text = model.name ?: "Не указано"
            if (model.photo == null) {
                binding.itemAvatarImg.visibility = View.GONE
                binding.nameOnAvatar.visibility = View.VISIBLE
                binding.nameOnAvatar.text = "${model.name.toString()[0]}"
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
                Glide.with(binding.root).load(model.photo).into(binding.itemAvatarImg)
            }
            itemView.setOnClickListener {
                click(model)
            }
        }
    }

    fun selectItem(listener: SelectItemOnClickListener?) {
        selectItemOnClickListener = listener
    }

    fun showActions(listener: ShowActionsOnClickListener?) {
        showActionsOnClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRedesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFriendsRvRedesBinding.inflate(layoutInflater, parent, false)
        return FriendRedesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendRedesViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }


    object FriendComparator : DiffUtil.ItemCallback<EventMembersResp.Member>() {

        override fun areItemsTheSame(oldItem: EventMembersResp.Member, newItem: EventMembersResp.Member): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventMembersResp.Member, newItem: EventMembersResp.Member): Boolean {
            return oldItem == newItem
        }
    }
}