package ru.radixit.letsplay.utils

import ru.radixit.letsplay.data.model.FriendEntity
import ru.radixit.letsplay.data.model.Member
import ru.radixit.letsplay.data.model.User

class FriendMapper () {
    companion object{
        fun mapInFriendEntity( user : User) : FriendEntity {

            return FriendEntity(user.id,user.name , photo_id = user.photo?.id , photo_url = user.photo?.url , user.surname , user.userType,user.username )
        }

        fun mapFriendInMember(friendEntity: FriendEntity) = friendEntity.id_user
        fun mapListWithFriendInMember(list: List<FriendEntity>) = list.map { mapFriendInMember(it) }
    }
}