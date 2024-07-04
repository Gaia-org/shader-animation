package com.example.shader.util

import android.view.Choreographer

/**
 * Used for listening frame time with second unit.
 */
object FrameUpdateMonitor {
    @Volatile
    private var isStart = false
    private val mMonitorFrameCallback by lazy { InternalFrameCallback() }
    private var mListener: (Float) -> Unit = {}
    private var mStartTime = 0L

    fun startMonitor(listener: (Float) -> Unit) {
        if (isStart) {
            return
        }
        mListener = listener
        Choreographer.getInstance().postFrameCallback(mMonitorFrameCallback)
        isStart = true
    }

    fun stopMonitor() {
        isStart = false
        Choreographer.getInstance().removeFrameCallback { mMonitorFrameCallback }
    }

    class InternalFrameCallback : Choreographer.FrameCallback {

        override fun doFrame(frameTimeNanos: Long) {
            if (mStartTime == 0L) {
                mStartTime = frameTimeNanos
            } else {
                val time = (frameTimeNanos - mStartTime) / 1000000f
                val secondTime = time / 1000f
                mListener.invoke(secondTime)
            }
            Choreographer.getInstance().postFrameCallback(this)
        }

    }
}