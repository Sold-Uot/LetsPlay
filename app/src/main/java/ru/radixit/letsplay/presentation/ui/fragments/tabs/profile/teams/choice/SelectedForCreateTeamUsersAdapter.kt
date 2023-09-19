package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams.choice

import android.annotation.SuppressLint
import android.util.Log
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
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes.SelectedUsersAdapter

typealias SelectUserEntityItemOnClickListener = (UserEntity) -> Unit




class SelectedForCreateTeamUsersAdapter() :
    RecyclerView.Adapter<SelectedForCreateTeamUsersAdapter.SelectedForCreateTeamUsersViewHolder>() {

    private var dataList = emptyList<UserEntity>()
    private var selectUserEntityItemOnClickListener: SelectUserEntityItemOnClickListener? = null


    class SelectedForCreateTeamUsersViewHolder(private val binding: ItemBlackListBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object{
        fun from(parent: ViewGroup): SelectedForCreateTeamUsersAdapter.SelectedForCreateTeamUsersViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)

            val binding = ItemBlackListBinding.inflate(layoutInflater, parent, false)
            return SelectedForCreateTeamUsersViewHolder(binding)
        }}

        fun bind(
            user : UserEntity,

            SelectUserEntityItemOnClickListener: SelectUserEntityItemOnClickListener
        ) {
            Log.e("тута" , user.toString())
            binding.playerName.text = user.name ?: "Не указано"
            binding.playerPosition.text = user.userType ?: "Не указано"

            binding.remove.setOnClickListener {
                SelectUserEntityItemOnClickListener.invoke(user)
            }
        }



    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectedForCreateTeamUsersViewHolder {
        return SelectedForCreateTeamUsersViewHolder.from(parent)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }



    override fun onBindViewHolder(holder: SelectedForCreateTeamUsersViewHolder, position: Int) {
        holder.bind(dataList[position], selectUserEntityItemOnClickListener!!)
    }

    fun selectItem(listener: SelectUserEntityItemOnClickListener) {
        selectUserEntityItemOnClickListener = listener
    }

    fun setData(list: List<UserEntity>) {
        dataList = list
        notifyDataSetChanged()
    }

    /*class  SelectedForCreateTeamUsersViewHolder(private val binding: ItemBlackListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): SelectedForCreateTeamUsersAdapter.SelectedForCreateTeamUsersViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBlackListBinding.inflate(layoutInflater, parent, false)
                return SelectedForCreateTeamUsersViewHolder(binding)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(
            user : UserEntity,

            SelectUserEntityItemOnClickListener: SelectUserEntityItemOnClickListener
        ) {
            Log.e("тута" , user.toString())
            binding.playerName.text = user.name ?: "Не указано"
            binding.playerPosition.text = user.userType ?: "Не указано"

            binding.remove.setOnClickListener {
                SelectUserEntityItemOnClickListener.invoke(user)
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectedForCreateTeamUsersViewHolder {
        return SelectedForCreateTeamUsersViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SelectedForCreateTeamUsersViewHolder, position: Int) {
        holder.bind(dataList[position] , selectUserEntityItemOnClickListener!!)
    }
    *//*    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedUsersViewHolder {
                return SelectedUsersViewHolder.from(parent)
            }*//*

    *//*override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectedUsersAdapter.SelectedUsersViewHolder {
        return SelectedUsersAdapter.SelectedUsersViewHolder.from(parent)
    }*//*
    *//*override fun onBindViewHolder(
        holder: SelectedForCreateTeamUsersAdapter.SelectedForCreateTeamUsersViewHolder,
        position: Int
    ) {
        holder.bind(dataList[position], selectItemOnClickListener = selectUserEntityItemOnClickListener!!)
    }*//*



    override fun getItemCount(): Int {
        return dataList.size
    }

    fun selectItem(listener: SelectUserEntityItemOnClickListener) {
        selectUserEntityItemOnClickListener = listener
    }

    fun setData(list: List<UserEntity>) {
        dataList = list
        notifyDataSetChanged()
    }*/
}