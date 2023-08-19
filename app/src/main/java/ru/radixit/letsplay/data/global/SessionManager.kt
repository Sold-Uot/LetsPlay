package ru.radixit.letsplay.data.global

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

typealias AuthToken = String

class SessionManager @Inject constructor(@ApplicationContext private val context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences("LetsPlay", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val REFRESH_TOKEN = "refresh_token"
        const val REGISTRATION_TOKEN = "registration_token"
        const val FORGOT_EMAIL_OR_PHONE_TOKEN = "forgot_email_Or_phone"
        const val FORGOT_TOKEN = "forgot_token"
        const val INVITES_BOOLEAN = "invites_boolean"
        const val EVENTS_BOOLEAN = "events_boolean"
        const val FRIENDS_BOOLEAN = "friends_boolean"
        const val MESSAGES_BOOLEAN = "messages_boolean"
        const val MONDAY_START = "monday_start"
        const val TUESDAY_START = "tuesday_start"
        const val WEDNESDAY_START = "wednesday_start"
        const val THURSDAY_START = "thursday_start"
        const val FRIDAY_START = "friday_start"
        const val SATURDAY_START = "saturday_start"
        const val SUNDAY_START = "sunday_start"
        const val MONDAY_END = "monday_end"
        const val TUESDAY_END = "tuesday_end"
        const val WEDNESDAY_END = "wednesday_end"
        const val THURSDAY_END = "thursday_end"
        const val FRIDAY_END = "friday_end"
        const val SATURDAY_END = "saturday_end"
        const val SUNDAY_END = "sunday_end"
        const val TOKEN = "token"
        const val TIME_TOKEN = "time_token"
        const val FIREBASE_TOKEN = "firebase_token"
    }

    fun saveToken(token: Int?) {
        val editor = prefs.edit()
        editor.putInt(TOKEN, token!!)
        editor.apply()
    }

    fun fetchToken(): Int {
        return prefs.getInt(TOKEN, 0)
    }

    fun saveTimeToken(timeToken: Int) {
        val editor = prefs.edit()
        editor.putInt(TIME_TOKEN, timeToken)
        editor.apply()
    }

    fun fetchTimeToken(): Int {
        return prefs.getInt(TIME_TOKEN, 0)
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): AuthToken {
        return prefs.getString(USER_TOKEN, null).toString()
    }

    fun removeAuthToken() = prefs
        .edit()
        .remove(USER_TOKEN)
        .apply()

    fun saveRefreshToken(token: String) {
        val editor = prefs.edit()
        editor.putString(REFRESH_TOKEN, token)
        editor.apply()
    }

    fun fetchRefreshToken(): String? {
        return prefs.getString(REFRESH_TOKEN, null)
    }

    fun removeRefreshToken() = prefs
        .edit()
        .remove(REFRESH_TOKEN)
        .apply()

    fun saveRegistrationToken(token: String) {
        val editor = prefs.edit()
        editor.putString(REGISTRATION_TOKEN, token)
        editor.apply()
    }

    fun fetchRegistrationToken(): String? {
        return prefs.getString(REGISTRATION_TOKEN, null)
    }

    fun saveForgotEmailOrPhone(token: String) {
        val editor = prefs.edit()
        editor.putString(FORGOT_EMAIL_OR_PHONE_TOKEN, token)
        editor.apply()
    }

    fun fetchForgotEmailOrPhone(): String? {
        return prefs.getString(FORGOT_EMAIL_OR_PHONE_TOKEN, null)
    }

    fun saveForgotToken(token: String) {
        val editor = prefs.edit()
        editor.putString(FORGOT_TOKEN, token)
        editor.apply()
    }

    fun fetchForgotToken(): String? {
        return prefs.getString(FORGOT_TOKEN, null)
    }

    fun removeForgot() {
        prefs.edit()
            .remove(FORGOT_EMAIL_OR_PHONE_TOKEN)
            .remove(FORGOT_TOKEN)
            .apply()
    }

    fun saveFirebaseToken(token: String) {
        val editor = prefs.edit()
        editor.putString(FIREBASE_TOKEN, token)
        editor.apply()
    }

    fun fetchFirebaseToken(): String? {
        return prefs.getString(FIREBASE_TOKEN, null)
    }

    fun removeFirebase() {
        prefs.edit()
            .remove(FIREBASE_TOKEN)
            .apply()
    }

    fun saveInvitesNotifications(invite: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(INVITES_BOOLEAN, invite)
        editor.apply()
    }

    fun fetchInvitesNotifications(): Boolean {
        return prefs.getBoolean(INVITES_BOOLEAN, false)
    }

    fun saveEventsNotifications(events: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(EVENTS_BOOLEAN, events)
        editor.apply()
    }

    fun fetchEventsNotifications(): Boolean {
        return prefs.getBoolean(EVENTS_BOOLEAN, false)
    }

    fun saveFriendsNotifications(friends: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(FRIENDS_BOOLEAN, friends)
        editor.apply()
    }

    fun fetchFriendsNotifications(): Boolean {
        return prefs.getBoolean(FRIENDS_BOOLEAN, false)
    }

    fun saveMessagesNotifications(message: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(MESSAGES_BOOLEAN, message)
        editor.apply()
    }

    fun fetchMessagesNotifications(): Boolean {
        return prefs.getBoolean(MESSAGES_BOOLEAN, false)
    }

    fun saveMondayStart(graphic: String?) {
        val editor = prefs.edit()
        editor.putString(MONDAY_START, graphic)
        editor.apply()
    }

    fun saveTuesdayStart(graphic: String?) {
        val editor = prefs.edit()
        editor.putString(TUESDAY_START, graphic)
        editor.apply()
    }

    fun saveWednesdayStart(graphic: String?) {
        val editor = prefs.edit()
        editor.putString(WEDNESDAY_START, graphic)
        editor.apply()
    }

    fun saveThursdayStart(graphic: String?) {
        val editor = prefs.edit()
        editor.putString(THURSDAY_START, graphic)
        editor.apply()
    }

    fun saveFridayStart(graphic: String?) {
        val editor = prefs.edit()
        editor.putString(FRIDAY_START, graphic)
        editor.apply()
    }

    fun saveSaturdayStart(graphic: String?) {
        val editor = prefs.edit()
        editor.putString(SATURDAY_START, graphic)
        editor.apply()
    }

    fun saveSundayStart(graphic: String?) {
        val editor = prefs.edit()
        editor.putString(SUNDAY_START, graphic)
        editor.apply()
    }

    fun saveMondayEnd(graphic: String?) {
        val editor = prefs.edit()
        editor.putString(MONDAY_END, graphic)
        editor.apply()
    }

    fun saveTuesdayEnd(graphic: String?) {
        val editor = prefs.edit()
        editor.putString(TUESDAY_END, graphic)
        editor.apply()
    }

    fun saveWednesdayEnd(graphic: String?) {
        val editor = prefs.edit()
        editor.putString(WEDNESDAY_END, graphic)
        editor.apply()
    }

    fun saveThursdayEnd(graphic: String?) {
        val editor = prefs.edit()
        editor.putString(THURSDAY_END, graphic)
        editor.apply()
    }

    fun saveFridayEnd(graphic: String?) {
        val editor = prefs.edit()
        editor.putString(FRIDAY_END, graphic)
        editor.apply()
    }

    fun saveSaturdayEnd(graphic: String?) {
        val editor = prefs.edit()
        editor.putString(SATURDAY_END, graphic)
        editor.apply()
    }

    fun saveSundayEnd(graphic: String?) {
        val editor = prefs.edit()
        editor.putString(SUNDAY_END, graphic)
        editor.apply()
    }

    fun getMondayStart(): String? {
        return prefs.getString(MONDAY_START, null)
    }

    fun getTuesdayStart(): String? {
        return prefs.getString(TUESDAY_START, null)
    }

    fun getWednesdayStart(): String? {
        return prefs.getString(WEDNESDAY_START, null)
    }

    fun getThursdayStart(): String? {
        return prefs.getString(THURSDAY_START, null)
    }

    fun getFridayStart(): String? {
        return prefs.getString(FRIDAY_START, null)
    }

    fun getSaturdayStart(): String? {
        return prefs.getString(SATURDAY_START, null)
    }

    fun getSundayStart(): String? {
        return prefs.getString(SUNDAY_START, null)
    }

    fun getMondayEnd(): String? {
        return prefs.getString(MONDAY_END, null)
    }

    fun getTuesdayEnd(): String? {
        return prefs.getString(TUESDAY_END, null)
    }

    fun getWednesdayEnd(): String? {
        return prefs.getString(WEDNESDAY_END, null)
    }

    fun getThursdayEnd(): String? {
        return prefs.getString(THURSDAY_END, null)
    }

    fun getFridayEnd(): String? {
        return prefs.getString(FRIDAY_END, null)
    }

    fun getSaturdayEnd(): String? {
        return prefs.getString(SATURDAY_END, null)
    }

    fun getSundayEnd(): String? {
        return prefs.getString(SUNDAY_END, null)
    }
}