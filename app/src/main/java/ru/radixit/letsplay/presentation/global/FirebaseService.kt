package ru.radixit.letsplay.presentation.global

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.global.SessionManager
import ru.radixit.letsplay.presentation.ui.MainActivity
import ru.radixit.letsplay.presentation.ui.fragments.tabs.chat.UserChatFragmentArgs
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseService : FirebaseMessagingService() {

    companion object {
        const val TAG = "MyFirebaseMessaging"
    }

    private var broadcaster: LocalBroadcastManager? = null
    private val processLater = false
    private val channelID = "PushNotification"

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this)
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Токен: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "От: ${remoteMessage.data["chatType"]}")
        if (remoteMessage.notification != null) {
            showNotification(remoteMessage)
        }
        if (processLater) {

            Log.d(TAG, "по очереди")

        } else {
            handleNow(remoteMessage) // Обработать в течение 10 секунд
        }
    }

    private fun handleNow(remoteMessage: RemoteMessage) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {

            remoteMessage.notification?.let {
                val intent = Intent("MyData")
                intent.putExtra("message", remoteMessage.data["text"])
                broadcaster?.sendBroadcast(intent)
            }
        }
    }


    private fun showNotification(remoteMessage: RemoteMessage) {
        createNotificationChannel()
        var pendingIntent: PendingIntent? = null
        if (remoteMessage.data["type"] == "chat") {
            val args = UserChatFragmentArgs.Builder(
                remoteMessage.data["chatType"]!!.toInt(),
                remoteMessage.data["receiverId"]!!.toInt(),
            ).build().toBundle()

            pendingIntent = NavDeepLinkBuilder(this)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.chat_graph)
                .setDestination(R.id.userChatFragment)
                .setArguments(args)
                .createPendingIntent()
        }
        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.mipmap.ic_launcher_lets_play)

            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) { notify(0, builder.build()) }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelID,
                "Let's play channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

}