package ru.radixit.letsplay.utils

import com.google.gson.Gson
import dagger.Lazy
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.radixit.letsplay.BuildConfig
import ru.radixit.letsplay.data.global.SessionManager
import ru.radixit.letsplay.data.network.request.RefreshTokenRequest
import ru.radixit.letsplay.data.network.response.RefreshResponse
import javax.inject.Inject

class AuthTokenInterceptor @Inject constructor(
    private val sessionManager: Lazy<SessionManager>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val chainRequest = chain.request()
        if (chain.request().headers["isAuthorization"] == "false") {
            return chain.proceed(chainRequest.newBuilder().removeHeader("Authorization").build())
        }
        val accessToken = sessionManager.get().fetchAuthToken()
        val request = chainRequest.newBuilder()
            .addHeader("Authorization", accessToken)
            .build()
//        val timeToken = sessionManager.get().fetchTimeToken()
//        val isTime = timeToken < System.currentTimeMillis() / 1000
        val oldResponse = chain.proceed(request)
        if (/*isTime ||*/ oldResponse.code == 401) {
            val client = OkHttpClient()
            val refreshRequest = RefreshTokenRequest(sessionManager.get().fetchRefreshToken()!!)
            val gson = Gson()
            val json = gson.toJson(refreshRequest)
            val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            val nRequest = Request.Builder()
                .put(body)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .url("${BuildConfig.BASE_URL}/auth/v1/refresh")
                .build()
            val response = client.newCall(nRequest).execute()
            if (response.code == 200) {
                val refreshTokenResponseModel: RefreshResponse =
                    gson.fromJson(response.body?.string(), RefreshResponse::class.java)
                val newToken = refreshTokenResponseModel.accessToken
                sessionManager.get().saveTimeToken(refreshTokenResponseModel.timeToken)
                sessionManager.get().saveAuthToken(newToken)
                return chain.proceed(
                    chain
                        .request()
                        .newBuilder()
                        .addHeader("Authorization", newToken)
                        .build()
                )
            }
        }
        return oldResponse
    }
}