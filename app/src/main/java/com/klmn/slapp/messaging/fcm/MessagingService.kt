package com.klmn.slapp.messaging.fcm

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import com.google.gson.Gson
import com.klmn.slapp.data.datastore.UserPreferences
import com.klmn.slapp.domain.PushNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class MessagingService : FirebaseMessagingService() {
    companion object {
        const val TAG = "SLApp.FCM"
        const val CHANNEL_ID = "channel_slapp"
    }

    @Inject lateinit var userPreferences: UserPreferences
    @Inject lateinit var notificationAPI: NotificationAPI

    /*
    *       PLAN:
    *           each slapp contains a list of tokens
    *           when the user fetches the lists, if their token is not in th slapp they add it
    *           when sending a token, the sender should listen for token-not-registered errors,
    *               and remove non-registered tokens from the slapp
    *
    *       IMPLEMENTATION:
    *           tokens will be managed in firestore in a different collection with same ids
    *           a message sending api will have its own firestore service that manages the
    *               registration-token collection. it will have a function that receives the
    *               sender's token and the listId, and send messages to everyone in the list but
    *               the sender. it will also listen to the token (in userPreferences) and update
    *               the collection.
    *           this class will receive the messages and send push notifications
    *
    * */

    override fun onCreate() {
        Firebase.messaging.token.addOnCompleteListener {
            if (it.isSuccessful) it.result?.let(this@MessagingService::onNewToken)
            else Log.w(TAG, "failed to fetch registration token ${it.exception}")
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "refreshed registration token $token")
//        CoroutineScope(Dispatchers.IO).launch {
//            userPreferences.saveRegistrationToken(token)
//        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        pushNotification(message.data["title"], message.data["message"])
    }

    suspend fun sendNotification(notification: PushNotification) = withContext(Dispatchers.IO) {
        try {
            val response = notificationAPI.postNotification(notification)
            if (response.isSuccessful) {
                println(Gson().toJson(response))
            } else Log.e(TAG, response.errorBody().toString())
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
}