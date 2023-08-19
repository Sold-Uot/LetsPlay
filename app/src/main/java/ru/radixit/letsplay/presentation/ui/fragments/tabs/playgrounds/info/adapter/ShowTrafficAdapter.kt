package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info.adapter

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.PlaygroundInDetail
import ru.radixit.letsplay.databinding.ItemTrafficRedesRvBinding
import java.time.LocalDate

class ShowTrafficAdapter(private val click: (PlaygroundInDetail.Schedule)->Unit) : androidx.recyclerview.widget.ListAdapter<PlaygroundInDetail.Schedule, ShowTrafficAdapter.MyViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemTrafficRedesRvBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        getItem(position).let {
            holder.bind(it)
        }
    }
    inner class MyViewHolder(private val binding: ItemTrafficRedesRvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: PlaygroundInDetail.Schedule){
            val date: LocalDate = LocalDate.now()
            val dow: Int = date.dayOfWeek.ordinal
            Log.d("dow","dow = ${dow}, pos = ${layoutPosition}")
            if(dow == layoutPosition){
                val context = binding.dayTv.context
                binding.dayTv.setTextColor(ContextCompat.getColor(context,R.color.white))
                binding.timeTv.setTextColor(ContextCompat.getColor(context,R.color.white))

                binding.dayTv.setTypeface(null, Typeface.BOLD);
                binding.timeTv.setTypeface(null, Typeface.BOLD);
            }
            binding.dayTv.text = model.weekDay
            binding.timeTv.text = model.time
            binding.root.setOnClickListener {
                click.invoke(model)
            }
        }
    }

    companion object {
        private val SINGLE_VIEW = 1
        private val MANY_VIEW = 2
        private val diffUtil =
            object : DiffUtil.ItemCallback<PlaygroundInDetail.Schedule>() {
                override fun areItemsTheSame(
                    oldItem: PlaygroundInDetail.Schedule,
                    newItem: PlaygroundInDetail.Schedule
                ): Boolean = oldItem.time == newItem.time

                override fun areContentsTheSame(
                    oldItem: PlaygroundInDetail.Schedule,
                    newItem: PlaygroundInDetail.Schedule
                ): Boolean = oldItem.time == newItem.time && oldItem.weekDay == newItem.weekDay
            }
    }
}