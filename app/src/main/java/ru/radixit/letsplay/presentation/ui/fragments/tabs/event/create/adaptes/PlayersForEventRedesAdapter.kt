package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.databinding.ItemListPlayersRedesEventBinding


typealias SelectUserRedesOnClickListener = ((User) -> Unit)
typealias RemoveUserRedesOnClickListener = ((User) -> Unit)

class PlayersForEventRedesAdapter :
    PagingDataAdapter<User, PlayersForEventRedesAdapter.FriendsForEventRedesViewHolder>(FriendComparator) {

    private var selectItemOnClickListener: SelectUserRedesOnClickListener? = null
    private var removeItemOnClickListener: RemoveUserRedesOnClickListener? = null

    private var isSelectedAll = false
    class FriendsForEventRedesViewHolder(private val binding: ItemListPlayersRedesEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): FriendsForEventRedesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListPlayersRedesEventBinding.inflate(layoutInflater, parent, false)
                return FriendsForEventRedesViewHolder(binding)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(
            friend: User,
            selectItemOnClickListener: SelectUserRedesOnClickListener,
            removeItemOnClickListener: RemoveUserRedesOnClickListener,
            isSelect : Boolean
        ) {
            Log.e("friend" , friend.toString())
            binding.namePlayersTv.text = friend.name ?: "Не указано"
            binding.positionPlayersTv.text = (friend.userType ?: "Не указано").toString()
            if (friend.photo == null) {
                binding.playersImg.visibility = View.VISIBLE
                binding.namePlayersTv.visibility = View.VISIBLE
                Glide.with(binding.root).load(R.color.clicked_text_color).circleCrop().into(binding.playersImg)
                binding.nameOnAvatar.text =
                    "${friend.name.toString()[0]}"

               /* binding.playersMatCard.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.violet
                    )
                )*/
            } else {
                binding.playersImg.visibility = View.VISIBLE
                binding.namePlayersTv.visibility = View.VISIBLE
                Glide.with(binding.root).load(friend.photo.url).circleCrop(). into(binding.playersImg)

            }

            if (isSelect){
                binding.checkAdd.isChecked = isSelect
            }


            binding.checkAdd.setOnClickListener {
                if (binding.checkAdd.isChecked) {
                    selectItemOnClickListener.invoke(friend)
                } else {
                    removeItemOnClickListener.invoke(friend)
                }
            }
        }
    }

    fun selectItem(listener: SelectUserRedesOnClickListener?) {
        selectItemOnClickListener = listener
    }

    fun removeItem(listener: RemoveUserRedesOnClickListener?) {
        removeItemOnClickListener = listener
    }

    fun selectAll(i : Boolean){
        isSelectedAll = i
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsForEventRedesViewHolder {
        return FriendsForEventRedesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FriendsForEventRedesViewHolder, position: Int) {
        holder.bind(getItem(position)!!, selectItemOnClickListener!!, removeItemOnClickListener!!, isSelectedAll)

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
