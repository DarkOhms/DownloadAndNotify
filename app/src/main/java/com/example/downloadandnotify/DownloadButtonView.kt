package com.example.downloadandnotify

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.databinding.BindingAdapter

/*
12/6/23
For phase one I will focus on getting the button working to download and interact with the viewModel.
I will change colors and text but leave progress animation for phase 2.
 */
class DownloadButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var viewModel: DownloadViewModel? = null
    var progress: Float = 0f
    var isDownloading: Boolean = false
    private val buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val downloadAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 1000 // Adjust the duration as needed
        addUpdateListener { animation ->
            progress = animation.animatedValue as Float
            invalidate()
        }
    }

    init {
        buttonPaint.color = Color.BLUE
        textPaint.color = Color.WHITE
        textPaint.textSize = 40f
        circlePaint.color = Color.GREEN
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw button
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), buttonPaint)

        // Draw changing color from right to left
        val rightColor = Color.rgb(255, (255 * (1 - progress)).toInt(), 0)
        buttonPaint.color = rightColor

        // Draw text
        val text = if (isDownloading) "Downloading..." else "Start Download"
        val textWidth = textPaint.measureText(text)
        val textX = (width - textWidth) / 2
        val textY = height / 2 - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(text, textX, textY, textPaint)

        // Draw download circle
        val circleRadius = height / 2f
        val circleX = textX + textWidth + circleRadius
        val circleY = height / 2f
        val circleRect = RectF(circleX - circleRadius, circleY - circleRadius, circleX + circleRadius, circleY + circleRadius)
        canvas.drawArc(circleRect, 0f, 360 * progress, true, circlePaint)
    }


    override fun performClick(): Boolean {
        return super.performClick()
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!isDownloading) {
                    startDownload()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun startDownload() {
        isDownloading = true
        viewModel?.startDownload()
        downloadAnimator.start()
    }
    fun setDownloadViewModel(viewModel: DownloadViewModel) {
        this.viewModel = viewModel
    }
}

@BindingAdapter("app:viewModel")
fun setViewModel(view: DownloadButtonView, viewModel: DownloadViewModel) {
    view.setDownloadViewModel(viewModel)
}