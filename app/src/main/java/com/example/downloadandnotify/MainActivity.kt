package com.example.downloadandnotify

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.downloadandnotify.databinding.ActivityMainBinding
import com.example.downloadandnotify.utils.DownloadBroadcastReceiver
import com.example.downloadandnotify.utils.DownloadURL
import com.example.downloadandnotify.utils.DownloadUtil
import com.example.downloadandnotify.utils.sendNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/*
I began this project without the starter code but I later found and inspected the
starter code and used some snippets to start my download logic.

isDownloading -> MainActivity -> ViewModel -> CustomView
progress -> MainActivity -> ViewModel -> CustomView
stopAnimation -> MainActivity -> ViewModel -> CustomView
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var myCustomButton: DownloadButtonView
    private val mainViewModel : DownloadViewModel by viewModels()
    private val downloadUtil = DownloadUtil()
    var downloadID: Long = 0
    val downloadReceiver = DownloadBroadcastReceiver()
    val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //create the notification channel
        createChannel(
            getString(R.string.download_notification_channel_id),
            getString(R.string.download_notification_channel_name)
        )
        //create intent filter and register receiver
        registerReceiver(downloadReceiver, filter)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = mainViewModel
        myCustomButton = binding.downloadButtonView

        mainViewModel.isDownloading.observe(this){isDownloading ->
            binding.downloadButtonView.isDownloading = isDownloading
            binding.downloadButtonView.invalidate()
        }

        mainViewModel.progress.observe(this){progress ->
            binding.downloadButtonView.updateProgress(progress)
        }

    binding.buttonGroup.setOnCheckedChangeListener { _, selectedOption ->
        Log.d("Button selected", selectedOption.toString())
        when (selectedOption) {
            R.id.glide_button -> {
                mainViewModel.setButtonSelection(DownloadURL.GLIDE)
            }
            R.id.loadApp_button -> {
                mainViewModel.setButtonSelection(DownloadURL.LOADAPP)
            }
            R.id.retrofit_button -> {
                mainViewModel.setButtonSelection(DownloadURL.RETROFIT)
            }
        }
    }
    binding.downloadButtonView.setOnTouchListener{ view, _ ->
        //check for selection
        if(mainViewModel.buttonSelectionURL == null){
            Toast.makeText(this, "Please select a download option.", Toast.LENGTH_SHORT).show()
            return@setOnTouchListener true
        }else {
            //we already did a null check
            if(!myCustomButton.isDownloading){
                //only if we aren't downloading already, we will start a custom download
                val selection: DownloadURL = mainViewModel.buttonSelectionURL!!
                Log.d("URL selection", selection.url.toString())
                downloadID = downloadUtil.createRequest(selection,this)
                trackProgress(downloadID)
                view.performClick()
            }else{
                Toast.makeText(this, "Download not finished.", Toast.LENGTH_SHORT)
                return@setOnTouchListener false
            }

        }
    }
}

override fun onDestroy() {
    super.onDestroy()
    unregisterReceiver(downloadReceiver)
}
private fun createChannel(channelId: String, channelName: String) {

if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
    val notificationChannel = NotificationChannel(
        channelId,
        channelName,
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        setShowBadge(false)
    }
    notificationChannel.enableLights(true)
    notificationChannel.lightColor = Color.RED
    notificationChannel.enableVibration(true)
    notificationChannel.description = "Download Status"

    val notificationManager = getSystemService(NotificationManager::class.java)

    notificationManager.createNotificationChannel(notificationChannel)
    Log.d("Create Channel", "Creation called")
}

}

fun sendDownloadNotification(fileName:String, downloadStatus:String){
Log.d("MainActivity", "send notification called")
val notificationManager = ContextCompat.getSystemService(
    this,
    NotificationManager::class.java
) as NotificationManager
notificationManager.sendNotification(fileName, downloadStatus, this)
}

fun updateProgress(progress: Int){
    if(progress == 100){
        myCustomButton.downloadComplete()
    }else{
        val progressFloat = progress.toFloat()/100.0F
        mainViewModel.setProgress(progressFloat)
        Log.d("UpdateProgress", progressFloat.toString())


    }
    Log.d("Main Activity", "Progress is " + progress.toString())
}

/*
trackProgress uses coroutines to poll the download service every 300 milliseconds
to track the download's progress and update the progress and isDownloading variables
in the viewModel without overly taxing the system resources and locking up the UI.
*/
    private fun trackProgress(downloadID:Long)= GlobalScope.launch {

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        // using query method
        var finishDownload = false
        var progress: Int

        while (!finishDownload) {
            delay(300)
            val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                when (status) {
                    DownloadManager.STATUS_FAILED -> {
                        finishDownload = true
                        withContext(Dispatchers.Main){ mainViewModel.setDownloading(false)}
                    }

                    DownloadManager.STATUS_PAUSED -> {Log.d("TrackProgress", "STATUS_ PAUSED")}
                    DownloadManager.STATUS_PENDING -> {Log.d("TrackProgress", "STATUS_ PENDING")}
                    DownloadManager.STATUS_RUNNING -> {
                        Log.d("TrackProgress", "Status Running")
                        val total =
                            cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        if (total > 0) {
                            val downloaded =
                                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                            if(downloaded > 0) {
                                progress = (downloaded * 100L / total).toInt()
                                Log.d("TrackProgress", "updateProgress() called")
                                withContext(Dispatchers.Main) { updateProgress(progress) }
                            }
                        }
                    }

                    DownloadManager.STATUS_SUCCESSFUL -> {
                        progress = 100
                        withContext(Dispatchers.Main){
                            updateProgress(progress)
                            finishDownload = true
                            Toast.makeText(this@MainActivity, "Download Completed", Toast.LENGTH_SHORT)
                            .show()
                        }
                    }
                }
            }
        }
    }
}