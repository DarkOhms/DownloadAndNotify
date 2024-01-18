package com.example.downloadandnotify

import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import com.example.downloadandnotify.databinding.ActivityDetailsBinding

class DetailsActivity: AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details)

        val extras = intent.extras
        val notificationId = intent.getIntExtra("notificationId", -1)
        val status = extras?.getString("status")
        binding.status.text = status
        if(status == "Failed"){
            binding.status.setTextColor(Color.RED)
        }
        binding.filename.text = extras?.getString("fileName")

        binding.button.setOnClickListener { navigateBackToMainActivity() }
        Log.d("Details Activity", "We made it this far.")

        NotificationManagerCompat.from(this).cancel(notificationId)

    }



    private fun navigateBackToMainActivity() {
        finish()
    }
}