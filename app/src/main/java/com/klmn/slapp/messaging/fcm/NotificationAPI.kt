package com.klmn.slapp.messaging.fcm

import com.klmn.slapp.domain.PushNotification
import com.klmn.slapp.domain.TokenRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {
    companion object { const val CONTENT_TYPE = "application/json" }

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postTokenRequest(
        @Body tokenRequest: TokenRequest
    ): Response<ResponseBody>
}