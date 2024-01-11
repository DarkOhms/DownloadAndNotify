package com.example.downloadandnotify

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.downloadandnotify.utils.DownloadURL

class DownloadViewModel: ViewModel(){

    var buttonSelectionURL: DownloadURL? = null
    private val _isDownloading = MutableLiveData<Boolean>()
    val isDownloading: LiveData<Boolean>
        get() = _isDownloading

    //float between 0 and 1
    private val _progress = MutableLiveData<Float>()
    val progress: LiveData<Float>
        get() = _progress

    init {
        _isDownloading.value = false
        _progress.value = 0F
    }

    fun startDownload() {
        // download logic here
        _isDownloading.value = true
        Log.d("ViewModel", "startDownload() called")
        when(buttonSelectionURL){
            DownloadURL.GLIDE -> {
                //download glide
            }
            DownloadURL.LOADAPP -> {
                //download loadapp
            }
            DownloadURL.RETROFIT -> {
                //download retrofit
            }

            else -> {
            //do nothing
            }
        }

    }

    fun setButtonSelection(downloadURL: DownloadURL) {
            buttonSelectionURL = downloadURL
        Log.d("ViewModel", downloadURL.toString())
    }
    fun setProgress(progress:Float){
        _progress.value = progress
        Log.d("ViewModel", "setProgress called")
    }

    fun setDownloading(downloading: Boolean){
            _isDownloading.value = downloading
        Log.d("ViewModel", "setDownloading called")
    }
}