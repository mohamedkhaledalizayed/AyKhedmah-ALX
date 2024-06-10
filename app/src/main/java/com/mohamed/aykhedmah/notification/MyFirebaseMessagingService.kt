package com.mohamed.aykhedmah.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.view.common.notification.NotificationScreen


class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }
    var NOTIFICATION_CHANNEL_ID = "OnlineServicesDeeplink"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("ygh", "${remoteMessage.data}")
        if (remoteMessage.data.isNotEmpty()) {

            sendNotification(
                remoteMessage.data["title"].toString(),
                remoteMessage.data["message"].toString()
            )
        }
    }

    private fun sendNotification(messageTitle: String, messageBody: String) {
        val intent = Intent(this, NotificationScreen::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri: Uri = Settings.System.DEFAULT_NOTIFICATION_URI
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setSmallIcon(R.drawable.ic_logo__1_)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(NOTIFICATION_CHANNEL_ID, "Online Service Deep Links", NotificationManager.IMPORTANCE_HIGH)
            )
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

}