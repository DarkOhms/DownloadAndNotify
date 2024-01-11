package com.example.downloadandnotify.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.downloadandnotify.DetailsActivity
import com.example.downloadandnotify.R

private val NOTIFICATION_ID = 0
fun NotificationManager.sendNotification(fileName: String, status:String, applicationContext: Context){

    val contentIntent = Intent(applicationContext, DetailsActivity::class.java)
    contentIntent.putExtra("fileName", fileName)
    contentIntent.putExtra("status", status)

    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val notification = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.download_notification_channel_id))
        .setContentTitle("Download $status")
        .setContentText("Your download: $fileName has finished.")
        .setSmallIcon(R.drawable.ic_download_complete)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent) // Add an intent to open the downloaded file
        .build()

    val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(NOTIFICATION_ID, notification)
}