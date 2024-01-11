package com.example.downloadandnotify

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

/*
12/6/23
For phase one I will focus on getting the button working to download and interact with the viewModel.
I will change colors and text but leave progress animation for phase 2.

1/5/2024
I have decided to make the circle animate continuously for the duration of the download and animate
from right to left based on the download's progress.
 */
class DownloadButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var viewModel: DownloadViewModel? = null
    var progress: Float = 0f
    var animationProgress: Float = 0f
    var isDownloading: Boolean = false
    val RADIUS_OFFSET = 70

    private var initialButtonColor: Int = ContextCompat.getColor(this.context, R.color.buttonColor)
    private val buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)


    private val downloadAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        repeatCount = ValueAnimator.INFINITE
        duration = 2000
        addUpdateListener { animation ->
            animationProgress = animation.animatedValue as Float
            invalidate()
            if(!isDownloading)
                this.cancel()
        }
    }


    private val finishedAnimation = ValueAnimator.ofFloat(0f,1f).apply {
        duration = 1000
        addUpdateListener { animation ->
            progress = animation.animatedValue as Float
            invalidate()

            if (this.isRunning && this.animatedValue == 1f) {
                viewModel?.setDownloading(false)
                viewModel?.setProgress(0f)
                this.cancel()
            }

        }
    }

    init {
        buttonPaint.color = initialButtonColor
        textPaint.color = Color.WHITE
        textPaint.textSize = 50f
        circlePaint.color = Color.rgb(221,169,70)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        Log.d("OnDraw", "onDrawCalled isDownloading = " + isDownloading.toString())
        // Draw button
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), buttonPaint)

        // Draw changing color from right to left
        val leftColor = ContextCompat.getColor(this.context, R.color.progressColor)
        if(isDownloading) {
            progressPaint.color = leftColor
            canvas.drawRect(0f, 0f, (width.toFloat() * progress), height.toFloat(), progressPaint)
        }

        // Draw text
        val text = if (isDownloading) "Downloading... " else "Download"
        val textWidth = textPaint.measureText(text)
        val textX = (width - textWidth) / 2
        val textY = height / 2 - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(text, textX, textY, textPaint)

        // Draw download circle
        if(isDownloading) {
            val circleRadius = height / 2f - RADIUS_OFFSET
            val circleX = textX + textWidth + circleRadius
            val circleY = height / 2f
            val circleRect = RectF(
                circleX - circleRadius,
                circleY - circleRadius,
                circleX + circleRadius,
                circleY + circleRadius
            )
            canvas.drawArc(circleRect, 0f, 360 * animationProgress, true, circlePaint)
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if(!isDownloading) {
                    startDownload()
                    invalidate()
                }
                Log.d("TouchEvent", "after startDownload is called")
            }
        }
        return super.onTouchEvent(event)
    }
    fun updateProgress(externalProgress: Float) {

        if (externalProgress in 0f..1f) {
            progress = externalProgress
            finishedAnimation.currentPlayTime = (externalProgress * finishedAnimation.duration).toLong()
        } else {
            throw IllegalArgumentException("Progress should be in the range 0.0 to 1.0")
        }


    }

    private fun startDownload() {
        Log.d("DownloadButtonView", "startDownload() called")
        viewModel?.startDownload()
        downloadAnimator.start()
        invalidate()
        //invalidate is moved to the touch event to avoid delays
    }
    fun downloadComplete(){
        finishedAnimation.start()
    }
    fun setDownloadViewModel(viewModel: DownloadViewModel) {
        this.viewModel = viewModel
        Log.d("BindingAdapter", "setViewModel() is called")
    }

}


@BindingAdapter("app:viewModel")
fun setViewModel(view: DownloadButtonView, viewModel: DownloadViewModel) {
    view.setDownloadViewModel(viewModel)
}

@BindingAdapter("app:isDownloading")
fun setIsDownloading(view: DownloadButtonView, isDownloading: Boolean) {
    Log.d("BindingAdapter", "setIsDownloading() is called")
    view.isDownloading = isDownloading
}


@BindingAdapter("app:progress")
fun setProgress(view: DownloadButtonView, progress: Float) {
    view.progress = progress
}