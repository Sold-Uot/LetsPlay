package ru.radixit.letsplay.presentation.ui.fragments.notifications

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowId
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.Notification
import ru.radixit.letsplay.databinding.ItemNotificationsRedesignBinding

typealias SelectOnClickListener = ((Notification) -> Unit)

enum class NotificationColorType(val id: Int) {
    ORANGE(R.drawable.notif_shape_orange),
    BLUE(R.drawable.notif_shape_blue),
    GREEN(R.drawable.notif_shape_green),
    RED(R.drawable.notif_shape_red),
}

enum class NotificationValue(val id: Int) {
    ORANGE(3),
    BLUE(5),
    BLUE_TWO(7),
    GREEN(11),
    RED(12),
}

/**
 * Адаптер для списка уведомлении
 */
class NotificationsAdapter :
    PagingDataAdapter<Notification, NotificationsAdapter.NotificationsViewHolder>(
        NotificationComparator
    ) {

    private var selectItemOnClickListener: SelectOnClickListener? = null

    class NotificationsViewHolder(private val binding: ItemNotificationsRedesignBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): NotificationsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ItemNotificationsRedesignBinding.inflate(layoutInflater, parent, false)
                return NotificationsViewHolder(binding)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(
            notification: Notification,
            selectItemOnClickListener: SelectOnClickListener,
            list: List<Notification>
        ) {
            with(binding) {
                val context = root.context
                when (notification.type.value) {
                    NotificationValue.ORANGE.id -> {
                        typeTv.background = context.getDrawable(NotificationColorType.ORANGE.id)
                    }

                    NotificationValue.BLUE.id -> {
                        typeTv.background = context.getDrawable(NotificationColorType.BLUE.id)
                    }

                    NotificationValue.BLUE_TWO.id -> {
                        typeTv.background = context.getDrawable(NotificationColorType.BLUE.id)
                    }

                    NotificationValue.GREEN.id -> {
                        typeTv.background = context.getDrawable(NotificationColorType.GREEN.id)
                    }

                    NotificationValue.RED.id -> {
                        typeTv.background = context.getDrawable(NotificationColorType.RED.id)
                    }
                }
                notification.title?.let {
                    title.text = it
                }
                time.text = notification.time
                typeTv.text = notification.type.label


                if (notification.photo == null) {
                    binding.photo.visibility = View.GONE
                    binding.nameOnAvatar.visibility = View.VISIBLE
                    binding.nameOnAvatar.text = "${notification.type.label[0]}"
                } else {
                    binding.photo.visibility = View.VISIBLE
                    binding.nameOnAvatar.visibility = View.GONE
                    Glide.with(binding.root).load(notification.photo).into(binding.photo)
                }
                if (list.size == 1) {
                    pressing.background =
                        ContextCompat.getDrawable(title.context, R.drawable.item_list_notif_shape)
                } else if (list.isNotEmpty()) {
                    if (layoutPosition == 0) {
                        pressing.background = ContextCompat.getDrawable(
                            title.context,
                            R.drawable.item_list_notif_shape_top
                        )
                    } else if (layoutPosition == list.size - 1) {
                        val layoutParams = binding.root.layoutParams as RecyclerView.LayoutParams
                        layoutParams.bottomMargin = 18
                        binding.root.layoutParams = layoutParams
                        pressing.background = ContextCompat.getDrawable(
                            title.context,
                            R.drawable.item_list_notif_shape_bottom
                        )
                    }
                }

                pressing.setOnClickListener {
                    selectItemOnClickListener.invoke(notification)
                }
            }
        }
    }

    fun selectItem(listener: SelectOnClickListener?) {
        selectItemOnClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        return NotificationsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        holder.bind(getItem(position)!!, selectItemOnClickListener!!, snapshot().items)
    }

    object NotificationComparator : DiffUtil.ItemCallback<Notification>() {

        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }
    }

}
