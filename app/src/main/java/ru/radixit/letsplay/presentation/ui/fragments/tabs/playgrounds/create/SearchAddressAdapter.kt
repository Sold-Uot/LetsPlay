package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.create

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.radixit.letsplay.data.model.Suggestion
import ru.radixit.letsplay.databinding.ItemSearchAddressBinding

typealias SelectItemOnClickListener = ((Suggestion) -> Unit)

class SearchAddressAdapter :
    RecyclerView.Adapter<SearchAddressAdapter.SearchAddressesViewHolder>() {

    private var dataList = emptyList<Suggestion>()
    private var selectItemOnClickListener: SelectItemOnClickListener? = null

    class SearchAddressesViewHolder(private val binding: ItemSearchAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Suggestion,
            selectItemOnClickListener: SelectItemOnClickListener
        ) {
            binding.name.text = item.value
            itemView.setOnClickListener {
                item.location?.let { it1 ->
                    Log.d("geoLat","lat = ${it1.geoLat}, lon = ${it1.geoLon}")
                    selectItemOnClickListener.invoke(
                        item
                    )
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): SearchAddressesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSearchAddressBinding.inflate(layoutInflater, parent, false)
                return SearchAddressesViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAddressesViewHolder {
        return SearchAddressesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SearchAddressesViewHolder, position: Int) {
        holder.bind(dataList[position], selectItemOnClickListener!!)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(list: List<Suggestion>) {
        dataList = list
        notifyDataSetChanged()
    }

    fun selectItem(listener: SelectItemOnClickListener?) {
        selectItemOnClickListener = listener
    }

}
