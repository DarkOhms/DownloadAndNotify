package com.example.downloadandnotify

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DownloadViewModel: ViewModel(){

    var buttonSelectionURL: DownloadURL? = null
    private val _isDownloading = MutableLiveData<Boolean>()
    val isDownloading: LiveData<Boolean>
        get() = _isDownloading


    //int 0 - 100
    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int>
        get() = _progress

    init {
        _isDownloading.value = false
        _progress.value = 0
    }

    fun startDownload() {
        // download logic here
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
}