package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.FriendEntity
import ru.radixit.letsplay.databinding.FragmentListInviteFriendBinding
import ru.radixit.letsplay.databinding.ItemFriendForCreateEventBinding
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.visible


class ListInviteFriendAdapter (): RecyclerView.Adapter<ListInviteFriendAdapter.ListInviteFriendViewHolder>() {

    private var list = emptyList<FriendEntity>()
    class ListInviteFriendViewHolder(private val binding: ItemFriendForCreateEventBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(user :FriendEntity){


            with(binding) {
                friendName.text = user.name
                friendPosition.text = user.userType
                if (user.photo_id == null || user.photo_url.isNullOrEmpty()) {
                    binding.photo.visibility = android.view.View.GONE
                    binding.nameOnAvatar.visibility = android.view.View.VISIBLE
                    binding.nameOnAvatar.text = "${user.name.toString().uppercase()[0]}"
                    val cardColor = androidx.core.content.ContextCompat.getColor(
                        itemView.context,
                        ru.radixit.letsplay.R.color.violet
                    )
                    binding.cardView10.setCardBackgroundColor(cardColor)
                } else {

                    binding.photo.visibility = android.view.View.VISIBLE
                    binding.nameOnAvatar.visibility = android.view.View.GONE
                    Glide.with(binding.root).load(user.photo_url).into(binding.photo)
                }



            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListInviteFriendAdapter.ListInviteFriendViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFriendForCreateEventBinding.inflate(layoutInflater, parent, false)
        return ListInviteFriendViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ListInviteFriendAdapter.ListInviteFriendViewHolder,
        position: Int
    ) {
        holder.bind(list[position])
    }
    fun setData(list :List<FriendEntity>){
        this.list = list
    }

    override fun getItemCount(): Int  = list.size
}