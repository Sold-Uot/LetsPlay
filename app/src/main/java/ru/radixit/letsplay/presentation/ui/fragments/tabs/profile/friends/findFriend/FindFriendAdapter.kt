package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.friends.findFriend

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.databinding.ItemFindFriendRecyclerBinding

typealias SelectItemOnClickListener = ((User) -> Unit)
typealias OnClickAddFriend = ((User) -> Unit)

class FindFriendAdapter :
    PagingDataAdapter<User, FindFriendAdapter.FindFriendViewHolder>(FindFriendComparator) {




    private var selectItemOnClickListener: SelectItemOnClickListener? = null
    private var onClickAddFriend :OnClickAddFriend? = null

    class FindFriendViewHolder(private val binding: ItemFindFriendRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): FindFriendViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFindFriendRecyclerBinding.inflate(layoutInflater, parent, false)
                return FindFriendViewHolder(binding)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(
            user: User,
            selectItemOnClickListener: SelectItemOnClickListener,
            onClickAddFriend: OnClickAddFriend
        ) {
            binding.playerName.text = user.name ?: "Не указано"
            binding.playerPosition.text = (user.userType ?: "Не указано").toString()
            if (user.photo == null) {
                binding.photo.visibility = View.GONE
                binding.nameOnAvatar.visibility = View.VISIBLE
                binding.nameOnAvatar.text = "${user.name.toString().uppercase()[0]}"
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
                Glide.with(binding.root).load(user.photo.url).into(binding.photo)

            }
            binding.addFriendBtn.setOnClickListener {
                onClickAddFriend.invoke(user)
                it.visibility = View.GONE

            }
            itemView.setOnClickListener {
                selectItemOnClickListener.invoke(user)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FindFriendViewHolder {
        return FindFriendViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FindFriendViewHolder, position: Int) {
        holder.bind(getItem(position)!!, selectItemOnClickListener!!,onClickAddFriend!!)
    }

    fun clickAddFriends( listener: OnClickAddFriend?){
        onClickAddFriend = listener
    }

    fun selectItem(listener: SelectItemOnClickListener?) {
        selectItemOnClickListener = listener
    }


    object FindFriendComparator : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
