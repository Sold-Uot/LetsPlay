package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import android.widget.RadioButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.PlaygroundInDetail
import ru.radixit.letsplay.databinding.ItemDropDownCreatePlaygSpinnerBinding
import ru.radixit.letsplay.databinding.ItemTrafficRedesRvBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.create.CreatePlaygroundsViewModel
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.visible
import java.time.LocalDate
import kotlin.coroutines.CoroutineContext

class DropDownRvRedesAdapter(
    private val isPrice: Boolean = false,
    private val positionSelected: Int = -1,
    private val click: (DropDownRvRedesAdapter, Int, String?) -> Unit
) : ListAdapter<String,
        DropDownRvRedesAdapter.MyDropDownViewHolder>(diffUtil) {
    private var selectedPosition = positionSelected
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyDropDownViewHolder {
        val binding = ItemDropDownCreatePlaygSpinnerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyDropDownViewHolder(binding)
    }

    fun updateData() {
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyDropDownViewHolder, position: Int) {
        getItem(position).let {
            holder.bind(it)
        }
    }

    inner class MyDropDownViewHolder(private val binding: ItemDropDownCreatePlaygSpinnerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: String) {

            with(binding) {
                Log.d("posArgs","posArgs = ${positionSelected}")
                radioBtn.setOnCheckedChangeListener(null)
                radioBtn.isChecked = (selectedPosition == position)
                radioBtn.setOnCheckedChangeListener(object :
                    CompoundButton.OnCheckedChangeListener {
                    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                        if (p1) {
                            selectedPosition = position
                            radioBtn.isFocusable = false
                            click.invoke(
                                this@DropDownRvRedesAdapter,
                                position,
                                priceInputEd.text.toString()
                            )
                        }
                    }
                })
                root.setOnClickListener {
                        radioBtn.isChecked = true

                }
                titleTv.text = model
                val layParams = titleTv.layoutParams as ConstraintLayout.LayoutParams
                priceInputEd.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                        if (!radioBtn.isChecked) {
                            radioBtn.isChecked = true
                        }
                        return@OnKeyListener true
                    }
                    false
                })
                if (layoutPosition == currentList.size - 1) {
                    if (isPrice) {
                        layParams.bottomToBottom = ConstraintSet.UNSET
                        titleTv.layoutParams = layParams
                        priceInputLay.visible()
                    } else {
                        layParams.bottomToBottom = ConstraintSet.PARENT_ID
                        titleTv.layoutParams = layParams
                        priceInputLay.gone()
                    }
                } else {
                    layParams.bottomToBottom = ConstraintSet.PARENT_ID
                    titleTv.layoutParams = layParams
                }
            }
        }
    }

    companion object {
        private val diffUtil =
            object : DiffUtil.ItemCallback<String>() {
                override fun areItemsTheSame(
                    oldItem: String,
                    newItem: String
                ): Boolean = oldItem == newItem

                override fun areContentsTheSame(
                    oldItem: String,
                    newItem: String
                ): Boolean = oldItem == newItem && oldItem == newItem
            }
    }
}