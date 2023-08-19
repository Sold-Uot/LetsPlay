package ru.radixit.letsplay.data.network.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import ru.radixit.letsplay.data.network.request.DaDataRequest
import ru.radixit.letsplay.data.network.response.DaDataResponse

interface DaDataApi {

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Authorization: Token 2b3472b25d37ed33d7ef55448cb541745256f5b7",
        "X-Secret: c94a784904e74cb22bed49d11a703f7d8401ee20"
    )
    @POST("suggestions/api/4_1/rs/suggest/address")
    suspend fun addresses(@Body request: DaDataRequest): Response<DaDataResponse>
}
