package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import ru.radixit.letsplay.R
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.visible


class SpinnerCreatePlaygAdapter(
    context: Context,
    private val items: List<String>,
    private val hint: String,
    private val isPrice: Boolean = false,
    private val click: () -> Unit
) :
    ArrayAdapter<String>(context, R.layout.item_drop_down_playg_spinner, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context)
                .inflate(R.layout.item_selected_playg_spinner, parent, false)
        }
        view?.setOnClickListener {
            click.invoke()
        }
        val dayTV: TextView = view!!.findViewById(R.id.title_tv)
        val list = getItem(position)!!.split(":")
        if (list.size >= 2) {
            dayTV.text = list[1]
        } else {
            dayTV.text = getItem(position)
        }
        val hintTv: TextView = view.findViewById(R.id.title_hint_tv)
        hintTv.text = hint
        return view
    }
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context)
                .inflate(R.layout.item_drop_down_create_playg_spinner, parent, false)
        }
        view?.gone()
        return view!!
    }
}