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
import ru.radixit.letsplay.databinding.ItemBlackListRedesRvBinding

typealias SelectItemRedesOnClickListener = ((String) -> Unit)
typealias RemoveItemRedesOnClickListener = ((String) -> Unit)

class BlackListRedesAdapter :
    PagingDataAdapter<User, BlackListRedesAdapter.BlackListRedesViewHolder>(BlackListRedesComparator) {

    private var selectItemOnClickListener: SelectItemOnClickListener? = null
    private var removeItemOnClickListener: RemoveItemOnClickListener? = null

    class BlackListRedesViewHolder(private val binding: ItemBlackListRedesRvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): BlackListRedesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBlackListRedesRvBinding.inflate(layoutInflater, parent, false)
                return BlackListRedesViewHolder(binding)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(
            blackListItem: User,
            selectItemOnClickListener: SelectItemOnClickListener,
            removeItemOnClickListener: RemoveItemOnClickListener?
        ) {
            binding.playerName.text = blackListItem.name ?: "Не указано"
            if (blackListItem.photo == null) {
                binding.nameOnAvatar.visibility = View.VISIBLE
                binding.nameOnAvatar.text =
                    "${blackListItem.name?.get(0) ?: ""}${blackListItem.username?.get(0) ?: ""}"
                binding.photoMatCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.violet
                    )
                )
            } else {
                binding.nameOnAvatar.visibility = View.GONE
                Glide.with(binding.root).load(blackListItem.photo.url).into(binding.photo)
            }
            binding.closeOrangeImg.setOnClickListener {
                removeItemOnClickListener!!.invoke(blackListItem.id.toString())
            }
            binding.root.setOnClickListener {
                selectItemOnClickListener.invoke(blackListItem.id.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlackListRedesViewHolder {
        return BlackListRedesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BlackListRedesViewHolder, position: Int) {
        holder.bind(getItem(position)!!, selectItemOnClickListener!!, removeItemOnClickListener)
    }

    fun selectItem(listener: SelectItemOnClickListener?) {
        selectItemOnClickListener = listener
    }

    fun removeItem(listener: RemoveItemOnClickListener?) {
        removeItemOnClickListener = listener
    }

    object BlackListRedesComparator : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

}