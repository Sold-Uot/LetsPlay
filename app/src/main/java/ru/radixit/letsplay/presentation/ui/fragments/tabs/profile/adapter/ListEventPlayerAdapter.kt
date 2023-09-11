package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter


import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import okhttp3.Dispatcher
import ru.radixit.letsplay.data.model.Event
import ru.radixit.letsplay.databinding.ItemEventsProfRedesRvBinding
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.visible

typealias SelectEventItemOnClickListener = ((Event) -> Unit)
class ListEventPlayerAdapter : RecyclerView.Adapter<ListEventPlayerAdapter.EventPlayerViewHolder>() {


    private var selectEventItemOnClickListener : SelectEventItemOnClickListener? = null
    var list = emptyList<Event>()
    class EventPlayerViewHolder(private val binding: ItemEventsProfRedesRvBinding)  :RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "LongLogTag")


        fun bind(event: ru.radixit.letsplay.data.model.Event,selectEventItemOnClickListener: SelectEventItemOnClickListener) {
            with(binding) {
                if (event.preview != null) {
                    itemAvatarImg.scaleType = ImageView.ScaleType.CENTER_CROP
                    Log.w("preview_event",event.preview.url)


                    Glide.with(root).load(event.preview.url).into(itemAvatarImg)
                } else {
                    itemAvatarImg.setPadding(10, 10, 10, 10)
                    itemAvatarImg.gone()
                    itemAvatarImg2.visible()
                }
                titleSmallEvent.text = event.title

                root.setOnClickListener{

                    selectEventItemOnClickListener(event)
                }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventPlayerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemEventsProfRedesRvBinding.inflate(layoutInflater, parent, false)
        return EventPlayerViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: EventPlayerViewHolder, position: Int) {

        holder.bind(list[position], selectEventItemOnClickListener!!)
    }
    fun selectItem(selecteventlistner: SelectEventItemOnClickListener){
        selectEventItemOnClickListener = selecteventlistner

    }
    fun setData(list: List<ru.radixit.letsplay.data.model.Event>) {
        this.list = list
        notifyDataSetChanged()
    }


}