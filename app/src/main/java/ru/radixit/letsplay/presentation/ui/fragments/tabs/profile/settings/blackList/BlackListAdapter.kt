package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings.blackList

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
import ru.radixit.letsplay.databinding.ItemBlackListBinding

typealias SelectItemOnClickListener = ((String) -> Unit)
typealias RemoveItemOnClickListener = ((String) -> Unit)

class BlackListAdapter :
    PagingDataAdapter<User, BlackListAdapter.BlackListViewHolder>(BlackListComparator) {

    private var selectItemOnClickListener: SelectItemOnClickListener? = null
    private var removeItemOnClickListener: RemoveItemOnClickListener? = null

    class BlackListViewHolder(private val binding: ItemBlackListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): BlackListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBlackListBinding.inflate(layoutInflater, parent, false)
                return BlackListViewHolder(binding)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(
            blackListItem: User,
            selectItemOnClickListener: SelectItemOnClickListener,
            removeItemOnClickListener: RemoveItemOnClickListener?
        ) {
            binding.playerName.text = blackListItem.name ?: "Не указано"
            binding.playerPosition.text = blackListItem.userType ?: "Не указано"
            if (blackListItem.photo == null) {
                binding.nameOnAvatar.visibility = View.VISIBLE
                binding.nameOnAvatar.text =
                    "${blackListItem.name?.get(0) ?: ""}${blackListItem.username?.get(0) ?: ""}"
                binding.constraint.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.violet
                    )
                )
            } else {
                binding.nameOnAvatar.visibility = View.GONE
                Glide.with(binding.root).load(blackListItem.photo.url).into(binding.photo)
            }
            binding.remove.setOnClickListener {
                removeItemOnClickListener!!.invoke(blackListItem.id.toString())
            }
            binding.parent.setOnClickListener {
                selectItemOnClickListener.invoke(blackListItem.id.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlackListViewHolder {
        return BlackListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BlackListViewHolder, position: Int) {
        holder.bind(getItem(position)!!, selectItemOnClickListener!!, removeItemOnClickListener)
    }

    fun selectItem(listener: SelectItemOnClickListener?) {
        selectItemOnClickListener = listener
    }

    fun removeItem(listener: RemoveItemOnClickListener?) {
        removeItemOnClickListener = listener
    }

    object BlackListComparator : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

}