package ru.radixit.letsplay.domain.repository

interface SharedPrefRepository {

    suspend fun removeAuthUser()

    fun saveAuthToken(token: String)

    fun fetchAuthToken(): String

    fun saveInvitesNotifications(invite: Boolean)

    fun fetchNotificationsInvite(): Boolean

    fun saveEventsNotifications(events: Boolean)

    fun fetchNotificationsEvents(): Boolean

    fun saveFriendsNotifications(friends: Boolean)

    fun fetchNotificationsFriends(): Boolean

    fun saveMessagesNotifications(messages: Boolean)

    fun fetchNotificationsMessages(): Boolean

    fun fetchRefreshToken(): String?

    fun saveRefreshToken(refreshToken: String)

}