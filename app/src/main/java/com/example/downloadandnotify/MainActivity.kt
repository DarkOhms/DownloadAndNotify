package com.example.downloadandnotify

import android.app.DownloadManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.example.downloadandnotify.databinding.ActivityMainBinding

/*
I began this project without the starter code but I later found and inspected the
starter code and used some snippets to start my download logic.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel : DownloadViewModel by viewModels()
    private var downloadID: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = mainViewModel
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
        binding.downloadButtonView.setOnTouchListener{view, event ->
            //check for selection
            if(mainViewModel.buttonSelectionURL == null){
                Toast.makeText(this, "Please select a download option.", Toast.LENGTH_SHORT).show()
                return@setOnTouchListener true
            }else {
                false
                //we already did a null check
                val selection: DownloadURL = mainViewModel.buttonSelectionURL!!
                Log.d("URL selection", selection.url.toString())
                val request =
                    DownloadManager.Request(selection.url.toUri())
                        .setTitle(selection.s)
                        .setMimeType("application/zip")
                        .setDescription(getString(R.string.app_description))
                        .setRequiresCharging(false)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                downloadID =
                    downloadManager.enqueue(request)// enqueue puts the download request in the queue
                view.onTouchEvent(event)
                view.performClick()
            }
        }
    }
}