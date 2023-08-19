package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.radixit.letsplay.data.model.PlaygroundInDetail
import ru.radixit.letsplay.databinding.ItemInfoImageFullScreenBinding

class PlaygInfoFullScreenAdapter (private val positionRV: (String) -> Unit):
    RecyclerView.Adapter<PlaygInfoFullScreenAdapter.PlaygroundInfoViewHolder>() {

    private var list = arrayOf<PlaygroundInDetail.Photo?>()

    inner class PlaygroundInfoViewHolder(private val binding: ItemInfoImageFullScreenBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: PlaygroundInDetail.Photo?) {
            if (photo == null) {
                binding.imageField.isVisible = false
                binding.imageView30.visibility = View.VISIBLE
            } else {
                binding.imageField.isVisible = true
                binding.imageView30.visibility = View.GONE
                Glide.with(binding.root).load(photo.url).into(binding.imageField)
            }
        }
    }

    override fun onViewAttachedToWindow(holder: PlaygroundInfoViewHolder) {
        super.onViewAttachedToWindow(holder)
        positionRV.invoke("${holder.layoutPosition+1}/${list.size}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaygroundInfoViewHolder {
        val binding = ItemInfoImageFullScreenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaygroundInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaygroundInfoViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(listPhotos: Array<PlaygroundInDetail.Photo?>) {
        list = listPhotos
        notifyDataSetChanged()
    }

}
