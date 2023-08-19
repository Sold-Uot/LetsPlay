package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.radixit.letsplay.data.network.response.Team
import ru.radixit.letsplay.databinding.ItemCommandProfRvRedesBinding

class ListTeamProfileRedesAdapter :
    RecyclerView.Adapter<ListTeamProfileRedesAdapter.TeamViewHolder>() {

    private var list = emptyList<Team>()

    class TeamViewHolder(private val binding: ItemCommandProfRvRedesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): TeamViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCommandProfRvRedesBinding.inflate(layoutInflater, parent, false)
                return TeamViewHolder(binding)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(team: Team) {
            with(binding) {
                titleEventTv.text = team.title
                if (team.photo == null) {
                    itemAvatarImg.visibility = View.GONE
                } else {
                    itemAvatarImg.visibility = View.VISIBLE
                    Glide.with(root).load(team.photo.url).into(itemAvatarImg)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        return TeamViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(list: List<Team>) {
        this.list = list
        notifyDataSetChanged()
    }
}