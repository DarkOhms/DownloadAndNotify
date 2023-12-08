package com.example.downloadandnotify

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.widget.Toast

enum class DownloadURL(val s: String, val url: String) {
    GLIDE("GLIDE", "https://github.com/bumptech/glide/archive/refs/heads/master.zip"),
    LOADAPP("LOADAPP", "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"),
    RETROFIT("RETROFIT", "https://github.com/square/retrofit/archive/refs/heads/master.zip")
}
class DownloadUtil {


}