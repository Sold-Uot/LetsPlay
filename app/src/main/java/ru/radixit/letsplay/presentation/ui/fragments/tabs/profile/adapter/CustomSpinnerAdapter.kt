package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ru.radixit.letsplay.R

class CustomSpinnerAdapter(context: Context, items: ArrayList<String>,private val hintTitle:String) :
    ArrayAdapter<String>(context, R.layout.item_drop_down_spinner, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_selected_spinner, parent, false)
            val textView: TextView = view.findViewById(R.id.title_tv)
            textView.text = getItem(position)
            val hintTv: TextView = view.findViewById(R.id.title_hint_tv)
            hintTv.text = hintTitle
        }
        return view!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_drop_down_spinner, parent, false)
            val textView: TextView = view.findViewById(R.id.title_tv)
            textView.text = getItem(position)
        }
        return view!!
    }
}