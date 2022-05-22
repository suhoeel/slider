package com.portfolio.myapplication

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout


class VerticalSlider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var maskBitmap: Bitmap? = null
    private var offscreenBitmap: Bitmap? = null
    private var offscreenCanvas: Canvas? = null

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var maskPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private var cornerRadius = 25f

    private var mWidth = 0
    private var mHeight = 0

    private var yPos = 0f
    private var step = 0

    private var sliderCallbackListener: SliderCallbackListener? = null

    companion object {
        private const val MIN_STEP = 0
        private const val MAX_STEP = 10
    }

    private var stepInterval = 0f

    private var subSubSliderView: SubSliderView? = null

    private var backgroundColor: Int? = null
    private var subSliderBackgroundColor: Int? = null

    init {

        setWillNotDraw(false)

        if (background is ColorDrawable) {
            backgroundColor = (background as ColorDrawable).color
        }

        if (attrs != null) {
            // Attribute initialization
            val a = context.obtainStyledAttributes(
                attrs,
                R.styleable.VerticalSlider, defStyleAttr, 0
            )
            a.getColorStateList(R.styleable.VerticalSlider_sliderBackgroundColor).let {
                backgroundTintList = it
                backgroundColor = it?.defaultColor
            }
            a.getColorStateList(R.styleable.VerticalSlider_sliderBarColor).let {
                subSliderBackgroundColor = it?.defaultColor
            }
            a.getFloat(R.styleable.VerticalSlider_sliderBorderRadius, cornerRadius).let {
                // dp to pixel
                // current dp is 25dp
                val metrics = context.resources.displayMetrics
                cornerRadius = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    it,
                    metrics
                )
            }
            a.recycle()
        }
        subSubSliderView = SubSliderView(context)

        this.addView(subSubSliderView)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Account for padding
        val xpad = (paddingLeft + paddingRight).toFloat()
        val ypad = (paddingTop + paddingBottom).toFloat()

        val ww = w.toFloat() - xpad
        val hh = h.toFloat() - ypad

        mWidth = w
        mHeight = h
        stepInterval = ((mHeight / MAX_STEP).toFloat())
        yPos = h.toFloat()
        Log.d("VIEW", "$mWidth, $mHeight, $stepInterval")

    }

    fun setSliderCallbackListener(sliderCallbackListener: SliderCallbackListener) {
        this.sliderCallbackListener = sliderCallbackListener
    }

    var isBeingDragged = false
    var startY = 0f
    var lastY = 0f

    override fun onTouchEvent(ev: MotionEvent): Boolean {

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startY = ev.y
                lastY = startY
            }
            MotionEvent.ACTION_UP -> {
                isBeingDragged = false
                performClick()
            }
            MotionEvent.ACTION_MOVE -> {
                val y = ev.y
                val yDelta: Float = y - lastY
                lastY = y
                isBeingDragged = true
//                Log.d("VIEW", "currentY $yPos")
//                Log.d("VIEW", "changed ${abs(yDelta)}")
                subSubSliderView!!.setYPos(yPos + yDelta)
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun draw(canvas: Canvas) {
        if (maskBitmap == null) {
            maskBitmap = createMask(width, height)
            offscreenBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            offscreenCanvas = Canvas(offscreenBitmap!!)
        }
        super.draw(offscreenCanvas)
        offscreenCanvas!!.drawBitmap(maskBitmap!!, 0f, 0f, maskPaint)
        canvas.drawBitmap(offscreenBitmap!!, 0f, 0f, null)
    }

    private fun createMask(width: Int, height: Int): Bitmap {
        val mask = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(mask)
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

    fun setYPosWithVol(step: Int): Int {
        this.step = step
        var drawPos = mHeight - (step * stepInterval)

        when (step) {
            0 -> drawPos = height.toFloat()
            10 -> drawPos = 0f
        }

        subSubSliderView!!.setYPosWithAnim(drawPos)
        return step
    }

    fun getYPos(): Float {
        return yPos
    }

    fun getStep(): Int {
        return step
    }

    private inner class SubSliderView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : View(context, attrs, defStyleAttr) {

        private var barPaint: Paint = Paint().apply {
            color = subSliderBackgroundColor ?: Color.BLACK
            isAntiAlias = true
        }

        override fun onDraw(canvas: Canvas) {

            super.onDraw(canvas)
            canvas.drawColor(backgroundColor ?: Color.WHITE)
            canvas.drawRect(0f, yPos, width.toFloat(), height.toFloat(), barPaint)

        }

        var lastStep = -1
        fun setYPos(y: Float) {
            yPos = y
            step = (MAX_STEP - (yPos / stepInterval)).toInt()
            if (yPos < (stepInterval / MAX_STEP)) {
                step = MAX_STEP
                yPos = 0f
            } else if (yPos > mHeight - (stepInterval / MAX_STEP)) {
                step = MIN_STEP
                yPos = mHeight.toFloat()
            }
            sliderCallbackListener?.getCurrentY(yPos)
            if(step != lastStep) {
                sliderCallbackListener?.getCurrentStep(step)
                lastStep = step
            }
            this@VerticalSlider.invalidate()
        }

        var anim: ValueAnimator? = null

        fun setYPosWithAnim(y: Float) {
            var tmpYPos = y
            step = (MAX_STEP - (yPos / stepInterval)).toInt()

            if (yPos < (stepInterval / MAX_STEP)) {
                step = MAX_STEP
                tmpYPos = 0f
            } else if (yPos > mHeight - (stepInterval / MAX_STEP)) {
                step = MIN_STEP
                tmpYPos = mHeight.toFloat()
            }

            if(anim != null && anim!!.isRunning) anim!!.cancel()

            anim = ValueAnimator.ofFloat(yPos, y).apply {
                duration = 1000
                addUpdateListener {
                    yPos = it.animatedValue as Float
                    this@VerticalSlider.invalidate()
                }
            }
            anim!!.start()

            sliderCallbackListener?.getCurrentY(tmpYPos)

            if(step != lastStep) {
                sliderCallbackListener?.getCurrentStep(step)
                lastStep = step
            }
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
        releaseBitmap()
    }


}
