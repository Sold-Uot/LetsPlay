package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.PlaygroundInDetail

class SpinnerShowTrafficAdapter(context: Context, items: List<PlaygroundInDetail.Schedule?>) :
    ArrayAdapter<PlaygroundInDetail.Schedule?>(context, R.layout.item_drop_down_playg_spinner, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_selected_play_detail_spinner, parent, false)
        }
        val dayTV: TextView = view!!.findViewById(R.id.title_tv)
        dayTV.text = "${getItem(position)?.weekDay} ${getItem(position)?.time}"
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_drop_down_playg_spinner, parent, false)
        }
        val dayTV: TextView = view!!.findViewById(R.id.day_tv)
        dayTV.text = getItem(position)?.weekDay
        val timeTV: TextView = view.findViewById(R.id.time_tv)
        timeTV.text = getItem(position)?.time
        return view
    }
}