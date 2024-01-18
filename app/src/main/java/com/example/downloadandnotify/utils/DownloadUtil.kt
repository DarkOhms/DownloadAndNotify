package com.example.downloadandnotify.utils

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.net.toUri
import com.example.downloadandnotify.MainActivity
import com.example.downloadandnotify.R

enum class DownloadURL(val s: String, val url: String) {
    GLIDE("GLIDE", "https://github.com/bumptech/glide/archive/refs/heads/master.zip"),
    LOADAPP("LOADAPP", "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"),
    RETROFIT("RETROFIT", "https://github.com/square/retrofit/archive/refs/heads/master.zip")
}
class DownloadUtil {

    //this returns the download ID for the broadcast receiver
    public fun createRequest(selection:DownloadURL, context: Context): Long {
        val request =
            DownloadManager.Request(selection.url.toUri())
                .setTitle(selection.s)
                .setMimeType("application/zip")
                .setDescription(getString(context, R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val downloadID = downloadManager.enqueue(request)
        val preferences = context.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)
        preferences.edit()
            .putLong("download_id", downloadID)
            .apply()
        return downloadID
  }

}
/*
There should be a way to consolidate the updateProgress() function from MainActivity
 and the DownloadBroadcastReceiver but I wasn't able to figure that out so I have a certain
 amount of duplicate code and some strange coupling between the receiver, the sendNotification
 and MainActivity.  NOT IDEAL.  If someone has a better way to do this I'm open for suggestions.
 */
class DownloadBroadcastReceiver: BroadcastReceiver(){
    @SuppressLint("Range")
    override fun onReceive(context: Context?, intent: Intent?) {
        //get download manager, downloadId and mainActivity
        val downloadManager = context?.getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        val downloadId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)

        val storedDownloadId = context.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)
            .getLong("download_id", -1L)

        var progress = 0

        if (downloadId == storedDownloadId) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)
            Log.d("DownloadReceiver", "STATUS_RUNNING")

                var status: String

                if (cursor.moveToFirst()) {

                    val fileName = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                    when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            status = "Successful"
                            progress = 100
                            Log.d("DownloadReceiver", "STATUS_SUCCESSFUL")
                            sendDownloadNotification(fileName, status, context)
                            cursor.close()
                        }

                        DownloadManager.STATUS_FAILED -> {
                            status = "Failed"
                            progress = 0
                            Log.d("DownloadReceiver", "STATUS_FAILED")
                            sendDownloadNotification(fileName, status, context)
                            cursor.close()
                        }

                        else -> {
                            status = "In Progress"
                            progress = cursor.getInt(
                                cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                            ) * 100 /
                                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            Log.d("DownloadReceiver", "STATUS_RUNNING")
                        }
                    }

                    cursor.close()
                    Log.d("DownloadReceiver", "updateProgress() called")

                }
            }
        }

    private fun sendDownloadNotification(fileName:String, downloadStatus:String, context: Context) {
        Log.d("DownloadReceiver", "send notification called")
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(fileName, downloadStatus, context)
    }

}
