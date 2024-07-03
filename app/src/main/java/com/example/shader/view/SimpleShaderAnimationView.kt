package com.example.shader.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RuntimeShader
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.example.shader.shaders.gradientAnimShader
import com.example.shader.util.FrameUpdateMonitor

open class SimpleShaderAnimationView(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mShader: RuntimeShader
    private val mTexPaint: TextPaint = TextPaint().apply {
        color = Color.WHITE
        textSize = 90f
    }

    init {
        mShader = createRuntimeShader()
        FrameUpdateMonitor.startMonitor { time ->
            if (time != 0f) {
                mShader.setFloatUniform("time", time)
                invalidate()
            }
        }
    }

    protected val mPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }

    open fun createRuntimeShader(): RuntimeShader {
        return gradientAnimShader
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != 0 && h != 0) {
            mShader.setFloatUniform("resolution", floatArrayOf(w.toFloat(), h.toFloat()))
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()
        mPaint.shader = mShader
        canvas?.drawRect(0.0f, 0.0f, width, height, mPaint)
        canvas?.drawText("A RuntimeShader calculates a per-pixel color based on the output of a user defined\n Android Graphics Shading Language (AGSL) function.\n" +
                "Android Graphics Shading Language\n" +
                "The AGSL syntax is very similar to OpenGL ES Shading Language, but there are some important differences that are highlighted here. Most of these differences are summed up in one basic fact: With GPU shading languages, you are programming a stage of the GPU pipeline. With AGSL, you are programming a stage of the Canvas or RenderNode drawing pipeline.\n" +
                "In particular, a GLSL fragment shader controls the entire behavior of the GPU between the rasterizer and the blending hardware. That shader does all of the work to compute a color, and the color it generates is exactly what is fed to the blending stage of the pipeline.\n" +
                "In contrast, AGSL functions exist as part of a larger pipeline. When you issue a Canvas drawing operation, Android (generally) assembles a single GPU fragment shader to do all of the required work. This shader typically includes several pieces."
        , 300f, 300f, mTexPaint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        FrameUpdateMonitor.stopMonitor()
    }

    companion object {
        private val DEFAULT_SHADER = RuntimeShader(
            """
              uniform vec2 uResolution;
              
              vec4 main(vec2 coords)
              {
                vec2 uv = coords / uResolution;
                vec3 col = vec3(0.);
                col.rg = uv;
                return vec4(col, 1.0);
              }
            """.trimIndent()
        )
    }
}