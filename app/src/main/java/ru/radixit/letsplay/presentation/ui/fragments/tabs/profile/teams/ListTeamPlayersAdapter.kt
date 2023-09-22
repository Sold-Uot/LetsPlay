package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.radixit.letsplay.data.model.UserEntity
import ru.radixit.letsplay.data.network.response.UserForTeamPlayers
import ru.radixit.letsplay.databinding.ItemPlayerForTeamBinding
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.visible

typealias SelectTeamPlayerOnClick = ((UserForTeamPlayers) -> Unit)

class ListTeamPlayersAdapter(val myTeam: Boolean) :
    RecyclerView.Adapter<ListTeamPlayersAdapter.ListPlayersViewHolder>() {
    private var list = emptyList<UserForTeamPlayers>()

    private var selectTeamPlayerOnClick : SelectTeamPlayerOnClick? = null
    inner class ListPlayersViewHolder(private val binding: ItemPlayerForTeamBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(user: UserForTeamPlayers) {

            with(binding) {
                playerName.text = user.name
                playerPosition.text = user.position

                if (myTeam) {
                    checkBox5.visible()
                    checkBox5.isChecked = true
                } else {
                    checkBox5.gone()
                }

                checkBox5.setOnCheckedChangeListener { compoundButton, b ->
                    when (b) {
                        true -> {}
                        false -> {
                           selectTeamPlayerOnClick!!.invoke(user)
                        }
                    }
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListPlayersViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPlayerForTeamBinding.inflate(layoutInflater, parent, false)
        return ListPlayersViewHolder(binding)

    }

    fun setData(list : List<UserForTeamPlayers>){
        this.list = list
        notifyDataSetChanged()
    }

    fun onClick(listener : SelectTeamPlayerOnClick){
        selectTeamPlayerOnClick = listener
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ListPlayersViewHolder, position: Int) {
        holder.bind(list[position])
    }
}