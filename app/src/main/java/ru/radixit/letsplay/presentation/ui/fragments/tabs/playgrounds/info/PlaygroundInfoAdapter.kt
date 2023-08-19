package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import ru.radixit.letsplay.data.model.PlaygroundInDetail
import ru.radixit.letsplay.databinding.ItemPlaygroundInfoImageBinding
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.visible

enum class CallBackInfoAdapter{
    CLICK_ITEM,
    SCROLL_ITEM
}
class PlaygroundInfoAdapter(private val clickItem:(CallBackInfoAdapter,Array<PlaygroundInDetail.Photo?>?,String?)->Unit) :
    RecyclerView.Adapter<PlaygroundInfoAdapter.PlaygroundInfoViewHolder>() {

    private var list = emptyList<PlaygroundInDetail.Photo?>()

    inner class PlaygroundInfoViewHolder(private val binding: ItemPlaygroundInfoImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: PlaygroundInDetail.Photo?) {
            if (photo == null) {
                binding.imageField.isVisible = false
                binding.imageView30.visibility = View.VISIBLE
            } else {
                binding.imageView30.gone()
                binding.imageField.visible()
                Glide.with(binding.root).load(photo.url)
                    .into(binding.imageField)
                binding.root.setOnClickListener {
                    clickItem.invoke(CallBackInfoAdapter.CLICK_ITEM,list.toTypedArray(),null)
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: PlaygroundInfoViewHolder) {
        super.onViewAttachedToWindow(holder)
        clickItem.invoke(CallBackInfoAdapter.SCROLL_ITEM,null,"${holder.layoutPosition+1}/${list.size}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaygroundInfoViewHolder {
        val binding = ItemPlaygroundInfoImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaygroundInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaygroundInfoViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(listPhotos: List<PlaygroundInDetail.Photo?>) {
        list = listPhotos
        notifyDataSetChanged()
    }

}
