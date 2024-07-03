package com.example.shader

import android.graphics.RenderEffect
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Choreographer
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.shader.shaders.gradientBlendAnimShader
import com.example.shader.shaders.noiseShader
import com.example.shader.shaders.pipelineZoomShader
import com.example.shader.view.SimpleShaderAnimationView

class MainActivity : AppCompatActivity(), Choreographer.FrameCallback {
    private lateinit var textView: TextView
    private lateinit var cardView: CardView
    private var mStartTime: Long = 0L
    private var isStart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
//        setContentView(SimpleShaderAnimationView(this))
    }

    private fun initView() {
        textView = findViewById<TextView>(R.id.tv_sample)
        cardView = findViewById(R.id.cardContainer)
        textView.post {
//            applyTextureShader(
//                textView,
//                floatArrayOf(textView.width.toFloat(), textView.height.toFloat())
//            )
        }
        textView.movementMethod = ScrollingMovementMethod.getInstance()
    }

    private fun applyTextureShader(view: View, resolution: FloatArray) {
        val shader = noiseShader.apply {
            setFloatUniform("resolution", resolution)
            setFloatUniform("intensity", 0.75f)
//            setFloatUniform("grainIntensity", 0.25f)
//            setFloatUniform("fiberIntensity", 0.5f)
//            setFloatUniform("amount", 0.15f)
//            setFloatUniform("contrast1", 2.0f)
//            setFloatUniform("contrast2", 2.0f)
        }
        val effect = RenderEffect.createRuntimeShaderEffect(shader, "uContent")
        view.setRenderEffect(effect)
    }

    private fun applyAnimShader(view: View, resolution: FloatArray, time: Float) {
        // Log.d(TAG, "applyAnimShader, resolution: ${resolution.toList()}, time: $time")
        if (time == 0f) {
            return
        }
        val shader = pipelineZoomShader.apply {
            setFloatUniform("resolution", resolution)
            setFloatUniform("time", time)
        }
        view.setRenderEffect(RenderEffect.createRuntimeShaderEffect(shader, "uContent"))
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onResume() {
        super.onResume()
        isStart = true
        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun onPause() {
        super.onPause()
        Choreographer.getInstance().removeFrameCallback(this)
        isStart = false
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (cardView.width == 0 || cardView.height == 0) {
            Choreographer.getInstance().postFrameCallback(this)
            return
        }
        if (mStartTime == 0L) {
            mStartTime = frameTimeNanos
        } else {
            //frameTimeNanos的单位是纳秒,这里需要计算时间差,然后转成毫秒
            val time = (frameTimeNanos - mStartTime) / 1000000f
            val secondTime = time / 1000f
            applyAnimShader(
                cardView,
                floatArrayOf(cardView.width.toFloat(), cardView.height.toFloat()),
//                floatArrayOf(textView.width.toFloat() - 500f, textView.height.toFloat() - 200f),
                secondTime
            )
        }

        Choreographer.getInstance().postFrameCallback(this)
    }
}