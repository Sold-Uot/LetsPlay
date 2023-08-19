package ru.radixit.letsplay.data.local

import ru.radixit.letsplay.data.global.SessionManager
import ru.radixit.letsplay.domain.repository.SharedPrefRepository
import javax.inject.Inject

class SharedPrefRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager
) : SharedPrefRepository {

    override suspend fun removeAuthUser() {
        sessionManager.removeAuthToken()
        sessionManager.removeRefreshToken()
        sessionManager.removeFirebase()
    }

    override fun saveAuthToken(token: String) {
        sessionManager.saveAuthToken(token)
    }

    override fun fetchAuthToken(): String {
        return sessionManager.fetchAuthToken()
    }

    override fun saveInvitesNotifications(invite: Boolean) {
        sessionManager.saveInvitesNotifications(invite)
    }

    override fun fetchNotificationsInvite(): Boolean {
        return sessionManager.fetchInvitesNotifications()
    }

    override fun saveEventsNotifications(events: Boolean) {
        sessionManager.saveEventsNotifications(events)
    }

    override fun fetchNotificationsEvents(): Boolean {
        return sessionManager.fetchEventsNotifications()
    }

    override fun saveFriendsNotifications(friends: Boolean) {
        sessionManager.saveFriendsNotifications(friends)
    }

    override fun fetchNotificationsFriends(): Boolean {
        return sessionManager.fetchFriendsNotifications()
    }

    override fun saveMessagesNotifications(messages: Boolean) {
        sessionManager.saveMessagesNotifications(messages)
    }

    override fun fetchNotificationsMessages(): Boolean {
        return sessionManager.fetchMessagesNotifications()
    }

    override fun fetchRefreshToken(): String? {
        return sessionManager.fetchRefreshToken()
    }

    override fun saveRefreshToken(refreshToken: String) {
        sessionManager.saveRefreshToken(refreshToken)
    }

}