package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.tabs.teams

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.network.response.Team
import ru.radixit.letsplay.databinding.ItemTeamRecyclerviewBinding

class TeamAdapter : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    private var list = emptyList<Team>()

    class TeamViewHolder(private val binding: ItemTeamRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): TeamViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTeamRecyclerviewBinding.inflate(layoutInflater, parent, false)
                return TeamViewHolder(binding)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(team: Team) {
            if (team.my) binding.myTeam.visibility = View.VISIBLE else binding.myTeam.visibility =
                View.GONE
            binding.teamName.text = team.title
            binding.countMembers.text = "${team.count} участника"
            if (team.photo == null) {
                binding.photo.visibility = View.GONE
                binding.nameOnAvatar.visibility = View.VISIBLE
                binding.nameOnAvatar.text = "${team.title.toString()[0]}"
                binding.constraint.visibility = View.VISIBLE
                binding.constraint.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.clicked_text_color
                    )
                )
            } else {
                binding.photo.visibility = View.VISIBLE
                binding.nameOnAvatar.visibility = View.GONE
                binding.constraint.visibility = View.GONE
                Glide.with(binding.root).load(team.photo.url).into(binding.photo)
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