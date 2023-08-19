package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.radixit.letsplay.data.model.Event
import ru.radixit.letsplay.databinding.ItemEventRvRedesBinding
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.visible


class ListEventsRedesAdapter(private val click:(Event)->Unit) :
    PagingDataAdapter<Event, ListEventsRedesAdapter.EventsViewHolder>(
        EventComparator
    ) {

    private val builder = SpannableStringBuilder()

    inner class EventsViewHolder(private val binding: ItemEventRvRedesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(event: Event) {
            Log.d("list","eventuuuuuu = ${event}")
            with(binding) {
                if (event.preview != null) {
                    imgNotFoundImg.gone()
                    imgNotFoundTv.gone()
                    eventPhoto.isVisible = true
                    eventBottomShadowImg.isVisible = true
                    Glide.with(root).load(event.preview.url).into(eventPhoto)
                } else {
                    imgNotFoundImg.visible()
                    imgNotFoundTv.visible()
                    eventPhoto.isVisible = false
                    eventBottomShadowImg.isVisible = false
                }
                placeEventTv.text = event.address
                titleEventTv.text = event.title
                levelGameMatBtn.text = event.gameLevel

                val str1 = SpannableString(event.countMember)
                str1.setSpan(ForegroundColorSpan(Color.WHITE), 0,1, 0)
                builder.append(str1)
                countPlayers.text = str1
                nameEvent.text = event.createdBy?.name
                root.setOnClickListener {
                    click(event)
                    Log.d("event","event = ${event}")
                    /*
                    * event = Event(address=Комсомольский просп., 63, г. Махачкала, countMember=1/10 игроков, createdBy=CreatedBy(id=1, name=Admin, surname=Admin),
                    * gameLevel=Начинающий, id=57, my=false, playground=null, preview=null, privacy=Открытое, start=11.04.2023 12:30, status=Завершена, title=Событие номер 2,
                    * type=null)
                    *
                    *
                    * event = Event(address=Комсомольский просп., 63, г. Махачкала, countMember=1/1 игроков, createdBy=CreatedBy(id=41, name=Test, surname=null), gameLevel=Начинающий,
                    * id=51, my=false, playground=null, preview=null, privacy=Открытое, start=12.08.2022 12:00, status=Текущий, title=ertet, type=null)
                    * */
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemEventRvRedesBinding.inflate(layoutInflater, parent, false)
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
