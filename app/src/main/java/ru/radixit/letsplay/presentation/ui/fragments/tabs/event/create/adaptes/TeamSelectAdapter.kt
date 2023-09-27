package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.network.response.Team
import ru.radixit.letsplay.databinding.ItemTeamsEventRvBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter.SelectTeamListMembers
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.setOnSingleClickListener
import ru.radixit.letsplay.utils.visible

class TeamSelectAdapter() : RecyclerView.Adapter<TeamSelectAdapter.TeamSelectViewHolder>() {


    private var list = emptyList<Team>()
    private var selectTeamListMembers: SelectTeamListMembers? = null


    companion object {
        fun from(parent: ViewGroup): TeamSelectAdapter.TeamSelectViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemTeamsEventRvBinding.inflate(layoutInflater, parent, false)
            return TeamSelectAdapter.TeamSelectViewHolder(binding)
        }
    }


    class TeamSelectViewHolder(private val binding: ItemTeamsEventRvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(team: Team, selectTeamListMembers: SelectTeamListMembers) {
            Log.e("teamm" , team.toString())
            binding.countTeamsTv.text = "${team.count.toString()} игроков"

//            if (team.my) binding.myTeamStar.visible() else binding.myTeamStar.gone()
            if (team.photo == null) {
                binding.teamsIconImg.gone()
                binding.nameOnAvatar.visible()
                binding.nameOnAvatar.text = "${team.title.toString().uppercase()[0]}"


            } else {
                binding.nameOnAvatar.gone()
                binding.teamsIconImg.visible()
                Glide.with(binding.root).load(team.photo.url).into(binding.teamsIconImg)
            }

            binding.teamsTitleTv.text = team.title

            binding.root.setOnSingleClickListener{
                selectTeamListMembers.invoke(team)

            }



        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamSelectViewHolder {
        return from(parent)
    }

    fun onClick(listener: SelectTeamListMembers) {
        selectTeamListMembers = listener

    }

    fun setData(list : List<Team>){
        this.list = list
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TeamSelectViewHolder, position: Int) {
        holder.bind(list[position], selectTeamListMembers!!)
    }
}