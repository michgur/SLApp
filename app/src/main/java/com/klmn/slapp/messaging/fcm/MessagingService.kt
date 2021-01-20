package com.klmn.slapp.messaging.fcm

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import com.klmn.slapp.data.datastore.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MessagingService : FirebaseMessagingService() {
    companion object {
        const val TAG = "SLApp.FCM"
        const val CHANNEL_ID = "channel_slapp"
    }

    private val userPreferences = UserPreferences(this)

    override fun onCreate() {
        if (userPreferences.registrationToken.value.isNullOrBlank())
            Firebase.messaging.token.addOnCompleteListener {
                if (it.isSuccessful && it.result != null) it.result?.let(::onNewToken)
                else Log.w(TAG, "failed to fetch registration token")
            }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "refreshed registration token $token")
        CoroutineScope(Dispatchers.IO).launch {
            userPreferences.saveRegistrationToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        pushNotification(message.data["title"], message.data["message"])
    }

//    suspend fun sendNotification(notification: PushNotification) = withContext(Dispatchers.IO) {
//        try {
//            val response = notificationAPI.postNotification(notification)
//            if (response.isSuccessful) {
//                println(Gson().toJson(response))
//            } else Log.e(TAG, response.errorBody().toString())
//        } catch (e: Exception) {
//            Log.e(TAG, e.toString())
//        }
//    }
}