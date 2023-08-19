package ru.radixit.letsplay.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.radixit.letsplay.BuildConfig
import ru.radixit.letsplay.data.network.api.DaDataApi
import ru.radixit.letsplay.domain.repository.SharedPrefRepository
import ru.radixit.letsplay.utils.SessionCookieJar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RetrofitInstance @Inject constructor(
    private val repository: SharedPrefRepository
) {


    val interceptor = HttpLoggingInterceptor()
        .apply {
            this.level = HttpLoggingInterceptor.Level.NONE
        }

    val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .cookieJar(SessionCookieJar())
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    val request = Request.Builder()
        .url(BuildConfig.BASE_URL_WS + "/v1?token=${repository.fetchAuthToken()}")
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .client(client)
            .baseUrl("https://suggestions.dadata.ru/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: DaDataApi by lazy {
        retrofit.create(DaDataApi::class.java)
    }
}

class ServiceInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .build()
        return chain.proceed(request)
    }
}