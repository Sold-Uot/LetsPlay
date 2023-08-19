package ru.radixit.letsplay.presentation.ui.fragments.tabs.event

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.Event
import ru.radixit.letsplay.databinding.ItemTabEventRecyclerBinding

class EventAdapter : PagingDataAdapter<Event, EventAdapter.EventViewHolder>(EventComparator) {

    class EventViewHolder(private val binding: ItemTabEventRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): EventViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTabEventRecyclerBinding.inflate(layoutInflater, parent, false)
                return EventViewHolder(binding)
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
            binding.nameEvent.text = event.title ?: "Неизв."
            binding.levelGame.text = event.gameLevel ?: "Неизв."
            binding.placeEvent.text = event.playground?.title ?: "Неизв."
            binding.countPlayers.text = event.countMember ?: "Неизв."
            binding.item.setOnClickListener {
//                if (itemView.findNavController().currentDestination?.id == R.id.listEventsFragment) {
//                    itemView.findNavController().navigate(
//                        ListEventsFragmentDirections.actionEventFragmentToEventDescriptionFragment(event.id.toString())
//                    )
//                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
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

