package com.portfolio.myapplication

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout

class VerticalSlider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private var maskBitmap: Bitmap? = null
    private var offscreenBitmap: Bitmap? = null
    private var offscreenCanvas: Canvas? = null
    private var paint: Paint
    private var maskPaint: Paint
    private var cornerRadius = 100f

    private val verticalUpDownSlideView: SliderView

    private var yPos = 500f
    private var volume = 0

    init {
        val metrics = context.resources.displayMetrics
        cornerRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            cornerRadius,
            metrics
        )
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        maskPaint =
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        setWillNotDraw(false)
        verticalUpDownSlideView = SliderView(context)


        if (attrs != null) {
            // Attribute initialization
            val a = context.obtainStyledAttributes(
                attrs,
                R.styleable.VerticalSlider, defStyleAttr, 0
            )
            /*  a.getDrawable(R.styleable.VerticalSlider_arcColor)?.let {
                  backgroundDisabled = it
              }
              a.getInt(R.styleable.ImageCircleButton_imagePadding, 20).let {
                  padding = it.toFloat()
              }
              a.getBoolean(R.styleable.ImageCircleButton_clicked, false).let {
                  isClicked = it
              }*/
            a.recycle()
        }

        this.addView(verticalUpDownSlideView)
    }


    override fun draw(canvas: Canvas) {
        if (offscreenBitmap == null) {
            offscreenBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            offscreenCanvas = Canvas(offscreenBitmap!!)
        }
        super.draw(offscreenCanvas)

        if (maskBitmap == null) {
            maskBitmap = createMask(width, height)
        }
        offscreenCanvas!!.drawBitmap(maskBitmap!!, 0f, 0f, maskPaint)
        canvas.drawBitmap(offscreenBitmap!!, 0f, 0f, paint)
    }


    private fun createMask(width: Int, height: Int): Bitmap {
        val mask = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(mask)
        val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        canvas.drawRoundRect(
            RectF(0f, 0f, width.toFloat(), height.toFloat()),
            cornerRadius,
            cornerRadius,
            paint
        )
        return mask
    }

    fun setYPos(y: Float): Int {

        this.yPos = y
        var drawPos = y
//        var yPos = y
//        var volume = 0

        val height = height.toFloat()

        if (yPos < 15f) {
            yPos = 0f
        } else if (yPos > height) {
            yPos = height
        }

        val aStep: Float = (height / 10.0f)

        volume = (10 - (yPos / aStep)).toInt()

        when (volume) {
            0 -> drawPos = height
            10 -> drawPos = 0f
        }

        verticalUpDownSlideView.setYPos(drawPos)
        invalidate()
        return volume
    }

    fun setYPosWithVol(y: Float, volume: Int): Int {

        this.yPos = y
        this.volume = volume

        var drawPos = y

        val height = height.toFloat()

        if (yPos < 0) {
            yPos = 0f
        } else if (yPos > height) {
            yPos = height
        }


        when (volume) {
            0 -> drawPos = height
        }

        verticalUpDownSlideView.setYPos(drawPos)
        invalidate()
        return volume
    }



    fun setVolume(vol: Int) {
        val aStep: Float = (height / 10.0f)
        setYPosWithVol(height - (vol * aStep), vol)
    }


    fun getVolume(): Int {
        return volume
    }

    fun setHandlerColor(color: String) {
        verticalUpDownSlideView.setHandlerColor(color)
    }

    fun setHandlerColor(color: Int) {
        verticalUpDownSlideView.setHandlerColor(color)
    }

    private inner class SliderView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : View(context, attrs, defStyleAttr) {

        private var yPos: Float = 3000f

        private var paint: Paint = Paint()

        init {
            paint.style = Paint.Style.FILL
            paint.color = Color.GRAY
            paint.isAntiAlias = false
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawRect(0f, height.toFloat(), width.toFloat(), yPos, paint)
        }

        fun setYPos(y: Float) {
            this.yPos = y
            super.invalidate()
        }

        fun setHandlerColor(color: String) {
            paint.color = Color.parseColor(color)
        }

        fun setHandlerColor(color: Int) {
            paint.color = color
        }

    }

    fun releaseBitmap() {
        if (offscreenBitmap != null) {
            offscreenBitmap!!.recycle()
            offscreenBitmap = null
            offscreenCanvas!!.setBitmap(null)
            offscreenCanvas = null

        }
        if (maskBitmap != null) {
            maskBitmap!!.recycle()
            maskBitmap = null
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

    }
}
