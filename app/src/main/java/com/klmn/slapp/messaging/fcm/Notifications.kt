package com.klmn.slapp.messaging.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.klmn.slapp.R
import com.klmn.slapp.domain.BuyNotification
import com.klmn.slapp.ui.MainActivity
import kotlin.random.Random

fun MessagingService.pushNotification(data: BuyNotification) {
    val intent = Intent(this, MainActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        .putExtra("notification", data)

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notificationId = Random.nextInt()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel(notificationManager)

    val contactName = contactProvider.getContact(data.uid)?.displayName ?: data.uid
    val icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_shopping_bag, theme)?.toBitmap()
    val color = ResourcesCompat.getColor(resources, R.color.accentColor, theme)

    val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
    val notification = NotificationCompat.Builder(this, MessagingService.CHANNEL_ID)
        .setContentTitle(getString(R.string.notification_title, contactName, data.listName))
        .setContentText(getString(R.string.notification_text))
        .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
        .setSmallIcon(R.drawable.ic_shopping_cart)
        .setLargeIcon(icon)
        .setColor(color)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()
    notificationManager.notify(notificationId, notification)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun createNotificationChannel(notificationManager: NotificationManager) {
    val channelName = "SLAppChannel"
    val channel = NotificationChannel(MessagingService.CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
        description = "SLApp Shopping Notifications"
        enableLights(true)
        lightColor = Color.WHITE
    }
    notificationManager.createNotificationChannel(channel)
}
