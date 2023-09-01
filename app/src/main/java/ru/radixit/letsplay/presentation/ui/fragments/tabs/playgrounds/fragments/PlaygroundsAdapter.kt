package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_playgrounds_recyclerview.view.*
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.Playground

typealias SelectItemOnClickListener = ((Playground) -> Unit)

class PlaygroundsAdapter : PagingDataAdapter<Playground, ViewHolder>(UIMODEL_COMPARATOR) {

    private var selectItemOnClickListener: SelectItemOnClickListener? = null

    class PlaygroundViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.name_field)
        private val address: TextView = view.findViewById(R.id.address_field)
        private val verified: ImageView = view.findViewById(R.id.verified)
        private val price: TextView = view.findViewById(R.id.price_content_scroll)
        private val imageField: ImageView = view.findViewById(R.id.image_field)
        private val coverage: TextView = view.findViewById(R.id.coverage)
        private val streetAddress: TextView = view.findViewById(R.id.street_address)
        private var playground: Playground? = null

        fun bind(playground: Playground, selectItemOnClickListener: SelectItemOnClickListener) {
            showRepoData(playground, selectItemOnClickListener)
        }

        private fun showRepoData(
            playground: Playground,
            selectItemOnClickListener: SelectItemOnClickListener
        ) {
            this.playground = playground
            name.text = playground.title
            streetAddress.text = playground.address
            coverage.text = "${playground.coating} покрытие"
            verified.isVisible = playground.verified == true
            address.text = playground.address
            price.text = "${playground.price} р"
            if (playground.photos.isEmpty()) {
                imageField.isVisible = false
                itemView.imageView30.visibility = View.VISIBLE
            } else {
                imageField.isVisible = true
                itemView.imageView30.visibility = View.GONE
                Glide.with(itemView).load(playground.photos[0].url).into(imageField)
            }

            itemView.playground.setOnClickListener { selectItemOnClickListener.invoke(playground) }
        }

        companion object {
            fun create(parent: ViewGroup): PlaygroundViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_playgrounds_recyclerview, parent, false)
                return PlaygroundViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return PlaygroundViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is Playground -> (holder as PlaygroundViewHolder).bind(
                    uiModel,
                    selectItemOnClickListener!!
                )
                else -> {}
            }
        }
    }

    fun selectItem(listener: SelectItemOnClickListener?) {
        selectItemOnClickListener = listener
    }

    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<Playground>() {
            override fun areItemsTheSame(oldItem: Playground, newItem: Playground): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Playground, newItem: Playground): Boolean =
                oldItem == newItem
        }
    }
}