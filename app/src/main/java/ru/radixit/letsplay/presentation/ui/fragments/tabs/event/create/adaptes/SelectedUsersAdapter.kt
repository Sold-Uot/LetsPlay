package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.data.model.UserEntity
import ru.radixit.letsplay.databinding.ItemBlackListBinding

typealias SelectItemOnClickListener = ((User  ) -> Unit)

class SelectedUsersAdapter :
    RecyclerView.Adapter<SelectedUsersAdapter.SelectedUsersViewHolder>() {

    private var dataList = arrayListOf<User>()
    private var selectItemOnClickListener: SelectItemOnClickListener? = null

    class SelectedUsersViewHolder(private val binding: ItemBlackListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): SelectedUsersViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBlackListBinding.inflate(layoutInflater, parent, false)
                return SelectedUsersViewHolder(binding)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(
            user : User   ,

            selectItemOnClickListener: SelectItemOnClickListener
        ) {
            binding.playerName.text = user.name ?: "Не указано"
            binding.playerPosition.text = user.userType ?: "Не указано"
            if (user.photo == null) {
                binding.nameOnAvatar.visibility = View.VISIBLE
                binding.nameOnAvatar.text =
                    "${user.name?.get(0) ?: ""}${user.username?.get(0) ?: ""}"
                binding.constraint.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.violet
                    )
                )
            } else {
                binding.nameOnAvatar.visibility = View.GONE
                Glide.with(binding.root).load(user.photo.url).into(binding.photo)
            }
            binding.remove.setOnClickListener {
                selectItemOnClickListener.invoke(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedUsersViewHolder {
        return SelectedUsersViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SelectedUsersViewHolder, position: Int) {
        holder.bind(dataList[position], selectItemOnClickListener!!)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun selectItem(listener: SelectItemOnClickListener?) {
        selectItemOnClickListener = listener
    }

    fun setData(list: ArrayList<User>) {
        dataList = list
        notifyDataSetChanged()
    }
}