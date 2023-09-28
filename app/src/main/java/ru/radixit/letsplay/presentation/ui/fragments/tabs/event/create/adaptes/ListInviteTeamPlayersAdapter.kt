package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.global.SessionManager
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.data.network.response.UserForTeamPlayers
import ru.radixit.letsplay.databinding.FragmentListInviteFriendBinding
import ru.radixit.letsplay.databinding.ItemPlayerForTeamBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.ListInviteTeamPlayersFrag
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.visible
import javax.inject.Inject

typealias AddTeamPlayerInInviteListOnclickListener = ((UserForTeamPlayers) -> Unit)
typealias RemoveTeamPlayerInInviteListOnclickListener = ((UserForTeamPlayers) -> Unit)
class ListInviteTeamPlayersAdapter () : RecyclerView.Adapter<ListInviteTeamPlayersAdapter.ListInviteTeamPlayersViewHolder>() {
    private var list = emptyList<UserForTeamPlayers>()

    private var addInvitePlayerOnClick : AddTeamPlayerInInviteListOnclickListener? = null
    private var removeInvitePlayerOnClick : RemoveTeamPlayerInInviteListOnclickListener? = null
    inner class ListInviteTeamPlayersViewHolder(private val binding: ItemPlayerForTeamBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user:UserForTeamPlayers,addPlayers: AddTeamPlayerInInviteListOnclickListener,removePlayer :RemoveTeamPlayerInInviteListOnclickListener){
            Log.e("element" , user.toString())
            with(binding) {


                checkBox5.visible()
                playerName.text = user.name
                playerPosition.text = user.position
                if (user.photo == null) {
                    binding.photo.visibility = View.GONE
                    binding.nameOnAvatar.visibility = View.VISIBLE
                    binding.nameOnAvatar.text = "${user.name.toString().uppercase()[0]}"
                    val cardColor = ContextCompat.getColor(
                        itemView.context,
                        R.color.violet
                    )
                    binding.cardView10.setCardBackgroundColor(cardColor)
                } else {

                    binding.photo.visibility = View.VISIBLE
                    binding.nameOnAvatar.visibility = View.GONE
                    Glide.with(binding.root).load(user.photo.url).into(binding.photo)
                }






                checkBox5.setOnCheckedChangeListener { compoundButton, b ->
                    when (b) {
                        true -> {
                            addPlayers.invoke(user)
                        }
                        false -> {
                            removePlayer.invoke(user)
                        }
                    }
                }

            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListInviteTeamPlayersViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPlayerForTeamBinding.inflate(layoutInflater, parent, false)
        return ListInviteTeamPlayersViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
        }

    fun setData(list : List<UserForTeamPlayers>){
        this.list = list
        notifyDataSetChanged()
    }
    fun addPlayerInInviteList(listOnclickListener: AddTeamPlayerInInviteListOnclickListener){
        addInvitePlayerOnClick = listOnclickListener
    }
    fun removePlayerInInviteList(listener : RemoveTeamPlayerInInviteListOnclickListener){
        removeInvitePlayerOnClick = listener
    }

    override fun onBindViewHolder(holder: ListInviteTeamPlayersViewHolder, position: Int) {

        holder.bind(list[position],addInvitePlayerOnClick!!,removeInvitePlayerOnClick!!)
    }
}