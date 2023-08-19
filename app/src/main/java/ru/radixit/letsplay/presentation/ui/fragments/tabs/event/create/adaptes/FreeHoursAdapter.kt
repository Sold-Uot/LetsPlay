package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.radixit.letsplay.data.model.Calendar
import ru.radixit.letsplay.databinding.ItemFreeHoursListBinding

typealias SelectTimeOnClickListener = (Calendar) -> Unit

class FreeHoursAdapter : RecyclerView.Adapter<FreeHoursAdapter.FreeHoursViewHolder>() {

    private var dataList = emptyList<Calendar>()
    private var selectTimeOnClickListener: SelectTimeOnClickListener? = null

    class FreeHoursViewHolder(private val binding: ItemFreeHoursListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): FreeHoursViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFreeHoursListBinding.inflate(layoutInflater, parent, false)
                return FreeHoursViewHolder(binding)
            }
        }

        fun bind(calendar: Calendar, selectTimeOnClickListener: SelectTimeOnClickListener?) {
            binding.hour.text = calendar.value.split(":")[0]
            binding.minute.text = calendar.value.split(":")[1]
            itemView.setOnClickListener {
                selectTimeOnClickListener?.invoke(calendar)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreeHoursViewHolder {
        return FreeHoursViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: FreeHoursViewHolder, position: Int) {
        holder.bind(dataList[position], selectTimeOnClickListener)
    }

    fun setData(list: List<Calendar>) {
        dataList = list
        notifyDataSetChanged()
    }

    fun selectTime(listener: SelectTimeOnClickListener) {
        selectTimeOnClickListener = listener
    }

}
