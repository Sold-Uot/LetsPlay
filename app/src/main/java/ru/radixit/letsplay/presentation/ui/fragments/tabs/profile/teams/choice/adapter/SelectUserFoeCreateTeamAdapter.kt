package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams.choice.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.UserEntity
import ru.radixit.letsplay.databinding.ItemBlackListBinding

typealias SelectUserEntityItemOnClickListener = (UserEntity) -> Unit

class SelectUserFoeCreateTeamAdapter : Adapter<SelectUserFoeCreateTeamAdapter.SelectUserForCreatTeamViewHolder>() {
    private var dataList = emptyList<UserEntity>()

    private var selectUserEntityItemOnClickListener: SelectUserEntityItemOnClickListener? = null

    class SelectUserForCreatTeamViewHolder(val binding : ItemBlackListBinding) : ViewHolder(binding.root) {
        companion object{
            fun from(parent: ViewGroup): SelectUserForCreatTeamViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)

                val binding = ItemBlackListBinding.inflate(layoutInflater, parent, false)
                return SelectUserForCreatTeamViewHolder(binding)
            }}

        fun bind(
            user : UserEntity,
             selectUserEntityItemOnClickListener: SelectUserEntityItemOnClickListener


        ) {

            binding.playerName.text = user.name ?: "Не указано"
            binding.playerPosition.text = user.userType ?: "Не указано"
            if (user.photo_id == null || user.photo_url == null) {
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
                Glide.with(binding.root).load(user.photo_url).into(binding.photo)
            }

            itemView.setOnClickListener {
                selectUserEntityItemOnClickListener.invoke(user)
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectUserForCreatTeamViewHolder = SelectUserForCreatTeamViewHolder.from(parent)


    override fun getItemCount(): Int  = dataList.size



    override fun onBindViewHolder(holder: SelectUserForCreatTeamViewHolder, position: Int) {
        holder.bind(dataList[position] , selectUserEntityItemOnClickListener!!)
    }
    fun selectItem(listener: SelectUserEntityItemOnClickListener) {
        selectUserEntityItemOnClickListener = listener
        notifyDataSetChanged()

    }

    fun setData(list: List<UserEntity>) {
        dataList = list
        notifyDataSetChanged()
    }
}