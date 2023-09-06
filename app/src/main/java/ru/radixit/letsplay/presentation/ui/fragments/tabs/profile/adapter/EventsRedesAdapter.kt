package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.Event
import ru.radixit.letsplay.databinding.ItemEventsProfRedesRvBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.ProfileRedesignFragDirections
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.visible

class EventsRedesAdapter : PagingDataAdapter<Event, EventsRedesAdapter.EventsViewHolder>(
    EventComparator
) {

    inner class EventsViewHolder(private val binding: ItemEventsProfRedesRvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(event: Event) {
            with(binding){
                if (event.preview != null) {
                    itemAvatarImg.scaleType = ImageView.ScaleType.CENTER_CROP
                    Glide.with(root).load(event.preview.url).into(itemAvatarImg)
                }else{
                    itemAvatarImg.setPadding(10,10,10,10)
                    itemAvatarImg.gone()
                    itemAvatarImg2.visible()
                }
                titleEventTv.text = event.title

                root.setOnClickListener {
                    itemView.findNavController().navigate(ProfileRedesignFragDirections.actionProfileRedesignFragToEventDescRedesignFrag(
                        event,null
                    ))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemEventsProfRedesRvBinding.inflate(layoutInflater, parent, false)
        return EventsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }


    object EventComparator : DiffUtil.ItemCallback<Event>() {

        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }

}
