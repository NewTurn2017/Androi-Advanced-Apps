package com.advanced.chapter2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class WaveformView @JvmOverloads constructor
    (context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val ampList = mutableListOf<Float>()
    private val rectList = mutableListOf<RectF>()
    private val rectWidth = 15f
    private var tick = 0

    private val redPaint = Paint().apply {
        color = 0xFFFF0000.toInt()
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for (rectF in rectList) {
            canvas?.drawRect(rectF, redPaint)
        }
    }

    fun addAmplitude(maxAmplitude: Float) {

        val amplitude = maxAmplitude / Short.MAX_VALUE * this.height * 0.8f
        ampList.add(amplitude)
        rectList.clear()

        val maxRect = this.width / rectWidth

        val amps = ampList.takeLast(maxRect.toInt())

        for ((i, amp) in amps.withIndex()) {
            val rect = RectF()
            rect.top = (this.height / 2f) - amp / 2
            rect.bottom = rect.top + amp
            rect.left = i * rectWidth
            rect.right = rect.left + (rectWidth - 5f)
            rectList.add(rect)
        }
        invalidate()
    }

    fun replayAmplitude(duration: Int) {
        rectList.clear()
        val maxRect = (this.width) / rectWidth
        val amps = ampList.take(tick).takeLast(maxRect.toInt())
        for ((i, amp) in amps.withIndex()) {
            val rect = RectF()
            rect.top = (this.height / 2f) - amp / 2
            rect.bottom = rect.top + amp
            rect.left = i * rectWidth
            rect.right = rect.left + (rectWidth - 5f)
            rectList.add(rect)
        }

        tick++

        invalidate()
    }

    fun clearData() {
        ampList.clear()
    }

    fun clearWave() {
        rectList.clear()
        tick = 0
        invalidate()
    }
}