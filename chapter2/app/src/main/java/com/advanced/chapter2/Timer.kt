package com.advanced.chapter2

import android.os.Handler
import android.os.Looper

class Timer(listener: OnTimerTickListener) {
    private var duration = 0L
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            duration += 40L
            handler.postDelayed(this, 40)
            listener.onTick(duration)
        }
    }

    fun start() {
        handler.postDelayed(runnable, 40)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
        duration = 0
    }

}

interface OnTimerTickListener {
    fun onTick(duration: Long)
}