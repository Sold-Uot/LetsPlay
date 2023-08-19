package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.tabs.events

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.radixit.letsplay.data.model.Event
import ru.radixit.letsplay.databinding.ItemEventsCurrentBinding

class EventsAdapter : PagingDataAdapter<Event, EventsAdapter.EventsViewHolder>(EventComparator) {

    class EventsViewHolder(private val binding: ItemEventsCurrentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): EventsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemEventsCurrentBinding.inflate(layoutInflater, parent, false)
                return EventsViewHolder(binding)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(event: Event) {
            if (event.preview != null) {
                binding.eventPhoto.isVisible = true
                Glide.with(binding.root).load(event.preview.url).into(binding.eventPhoto)
            } else {
                binding.eventPhoto.isVisible = false
            }
            binding.levelGame.text = event.gameLevel
            binding.placeEvent.text = event.title
            binding.countPlayers.text = event.countMember
            binding.myEvent.isVisible = event.my == true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        return EventsViewHolder.from(parent)
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
