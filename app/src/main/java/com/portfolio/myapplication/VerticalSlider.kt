package com.portfolio.myapplication

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

    private var paint: Paint
    private var maskPaint: Paint
    private var cornerRadius = 100f

    private var mWidth = 0
    private var mHeight = 0

    private var yPos: Float = 0f
    private var volume = 0

    companion object {
        private const val STEP = 10
    }

    private var stepInterval = 0f

    private val sliderSubView: SliderView

    private var backgroundColor: Int? = null

    //    private var yPos = 0f


    init {
        if (background is ColorDrawable) {
            backgroundColor = (background as ColorDrawable).color
        }
        val metrics = context.resources.displayMetrics
        cornerRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            cornerRadius,
            metrics
        )
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.RED
        maskPaint =
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        setWillNotDraw(false)
        sliderSubView = SliderView(context)


        if (attrs != null) {
            // Attribute initialization
            val a = context.obtainStyledAttributes(
                attrs,
                R.styleable.VerticalSlider, defStyleAttr, 0
            )
            a.getColor(R.styleable.VerticalSlider_seekbarColor, Color.YELLOW).let {
                sliderSubView.setHandlerColor(it)
            }
            /*
              a.getInt(R.styleable.ImageCircleButton_imagePadding, 20).let {
                  padding = it.toFloat()
              }
              a.getBoolean(R.styleable.ImageCircleButton_clicked, false).let {
                  isClicked = it
              }*/
            a.recycle()
        }

        this.addView(sliderSubView)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        stepInterval = ((mHeight / STEP).toFloat())

        Log.d("VIEW", "$w, $h")

    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_UP -> {

            }
            MotionEvent.ACTION_MOVE -> {
                Log.d("VIEW", "1")
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_UP -> {

            }
            MotionEvent.ACTION_MOVE -> {
                Log.d("VIEW", "${ev.y}")
                sliderSubView.setYPos(ev.y)
            }
        }
        return true
    }

    override fun draw(canvas: Canvas) {
        if (maskBitmap == null) {
            maskBitmap = createMask(width, height)
            offscreenBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            offscreenCanvas = Canvas(offscreenBitmap!!)
        }
        super.draw(offscreenCanvas)

        offscreenCanvas!!.drawBitmap(maskBitmap!!, 0f, 0f, maskPaint)
        canvas.drawBitmap(offscreenBitmap!!, 0f, 0f, paint)
        /*offscreenCanvas!!.drawRect(0f, 0f, width.toFloat(), height.toFloat(), Paint().apply {
            style = Paint.Style.FILL
            color = Color.RED
        })*/
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

    /*fun setYPos(y: Float): Int {

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
    }*/


    private fun setYPosWithVol(volume: Int): Int {

//        this.yPos = y

        this.volume = volume

//        setYPosWithVol(height - (volume * stepInterval), volume)
        var drawPos = mHeight - (volume * stepInterval)

        when (volume) {
            0 -> drawPos = height.toFloat()
            10 -> drawPos = 0f
        }

        sliderSubView.setYPos(drawPos)
//        invalidate()
        return volume
    }


    /*fun setVolume(vol: Int) {
        val aStep: Float = (height / 10.0f)
        setYPosWithVol(height - (vol * aStep), vol)
    }*/


    fun getVolume(): Int {
        return volume
    }

    fun setHandlerColor(color: String) {
        sliderSubView.setHandlerColor(color)
    }

    fun setHandlerColor(color: Int) {
        sliderSubView.setHandlerColor(color)
    }

    private inner class SliderView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : View(context, attrs, defStyleAttr) {

        private var paint: Paint = Paint()

        private var textPaint: Paint = Paint()

        private var newBitmap: Bitmap? = null
        private var newCanvas: Canvas? = null

        init {
//            paint.style = Paint.Style.FILL
            paint.color = Color.BLUE
            paint.isAntiAlias = false
            textPaint.color = Color.BLACK
            textPaint.style = Paint.Style.STROKE
            textPaint.textSize = 15f
            textPaint.isAntiAlias = false
        }

        /*    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
                super.onSizeChanged(w, h, oldw, oldh)
                mWidth = w
                mHeight = w
            }
    */
        override fun onDraw(canvas: Canvas) {

            /*if (newBitmap == null) {
                newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                newCanvas = Canvas(newBitmap!!)
            }*/
            super.onDraw(canvas)


            canvas.drawColor((if(backgroundColor == null) Color.WHITE else backgroundColor!!))
//            Log.d("VIEW", "yPos ${yPos}")
            Log.d("VIEW", "height ${height.toFloat()}")
            canvas.drawRect(0f, yPos, width.toFloat(), height.toFloat(), this.paint)
            canvas.drawText("0", (width / 4f), (height/3f), this.textPaint)

//            canvas.drawBitmap(newBitmap!!, 0f, 0f, this.paint)

//            offscreenCanvas!!.drawRect(0f, yPos, width.toFloat(), height.toFloat(), this@VerticalSlider.paint)

        }

        fun setYPos(y: Float) {
            yPos = y
            volume = (10 - (yPos / stepInterval)).toInt()

            if (yPos < 0) {
                volume = 10
                yPos = 0f
            } else if (yPos > mHeight) {
                volume = 0
                yPos = mHeight.toFloat()
            }
            Log.d("VIEW", "volume $volume")

            this@VerticalSlider.invalidate()

//            super.invalidate()
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
