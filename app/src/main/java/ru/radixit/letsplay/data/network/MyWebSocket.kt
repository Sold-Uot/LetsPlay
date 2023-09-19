package ru.radixit.letsplay.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import ru.radixit.letsplay.data.model.ChatMessage
import javax.inject.Inject

class MyWebSocket @Inject constructor(
    retrofitInstance: RetrofitInstance
) {

    private var webSocket: WebSocket? = null
    private val _liveData = MutableLiveData<ChatMessage>()
    val liveData: LiveData<ChatMessage> get() = _liveData

    private fun outputData(message: ChatMessage) {
        _liveData.postValue(message)
    }

    init {
        val client = retrofitInstance.client
        val request = retrofitInstance.request
        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                webSocket.close(1000, null)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                Log.d("Receiving bytes : %s", bytes.hex())
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                Log.d("Receiving bytes : %s", text.toString())

                val myClass: ChatMessage = Gson().fromJson(text, ChatMessage::class.java)
                outputData(myClass)
            }
        })
        client.dispatcher.executorService.shutdown()

    }

    fun send(chatType: Int, receiverId: Int, senderId: Int, message: String) {
        webSocket?.send(
            "{\n" +
                    "    \"event\":\"eventChat\",\n" +
                    "    \"data\":{\n" +
                    "    \"chatType\":$chatType,\n" +
                    "    \"receiverId\":$receiverId,\n" +
                    "    \"senderId\":$senderId,\n" +
                    "    \"messageText\":\"$message\"\n" +
                    "    }\n" +
                    "}"
        )
    }
}