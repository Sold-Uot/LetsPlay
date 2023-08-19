package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.Calendar
import ru.radixit.letsplay.databinding.ItemFreeHoursListRedesBinding
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.visible


class FreeHoursRedesAdapter(private val click: (Pair<Calendar, Calendar?>) -> Unit) :  ListAdapter<Pair<Calendar, Calendar?>,FreeHoursRedesAdapter.FreeHoursRedesViewHolder>(
    diffUtil) {

    private var selectedPosition = -1

    inner class FreeHoursRedesViewHolder(private val binding: ItemFreeHoursListRedesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(calendar: Pair<Calendar, Calendar?>) {
            Log.d("height","height = ${binding.freeMatCard.layoutParams.height}")
            if(selectedPosition == position){
                Log.d("selectedPosition","selectedPosition = ${selectedPosition},position = ${position}")
                binding.time.setTextColor(ContextCompat.getColor(binding.root.context,R.color.main_orange))
                binding.freeMatCard.setCardBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.background_black))
            }else{
                binding.time.setTextColor(ContextCompat.getColor(binding.root.context,R.color.white))
                binding.freeMatCard.setCardBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.background_for_edittext))
            }
            binding.time.text = "${calendar.first.value.split(":")[0]} : ${calendar.first.value.split(":")[1]}"
            calendar.second?.let {
                binding.time.text = "${calendar.first.value.split(":")[0]} : ${calendar.first.value.split(":")[1]}" +
                        " - ${it.value.split(":")[0]}:${it.value.split(":")[1]}"
            }?: kotlin.run {
                binding.time.text = "${calendar.first.value.split(":")[0]} : ${calendar.first.value.split(":")[1]}"
            }
            itemView.setOnClickListener {
                click(calendar)
                selectedPosition = position
                binding.time.setTextColor(ContextCompat.getColor(binding.root.context,R.color.main_orange))
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreeHoursRedesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFreeHoursListRedesBinding.inflate(layoutInflater, parent, false)
        return FreeHoursRedesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FreeHoursRedesViewHolder, position: Int) {
        getItem(position).let {
            holder.bind(it)
        }
    }

    companion object {
        private val diffUtil =
            object : DiffUtil.ItemCallback<Pair<Calendar, Calendar?>>() {
                override fun areItemsTheSame(
                    oldItem: Pair<Calendar, Calendar?>,
                    newItem: Pair<Calendar, Calendar?>
                ): Boolean = false

                override fun areContentsTheSame(
                    oldItem: Pair<Calendar, Calendar?>,
                    newItem: Pair<Calendar, Calendar?>
                ): Boolean = false
            }
    }
}
