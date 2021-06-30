package com.nitin.drawingapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
class DrawingView(context: Context, attrs: AttributeSet): View(context, attrs) {
    private var mDrawPath: CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()
    private var style: Paint.Style = Paint.Style.STROKE
    private var color = Color.BLACK
    private var canvas: Canvas? = null
    private val mPaths = ArrayList<CustomPath>()
    private var mUndoPaths = ArrayList<CustomPath>()
    private var shape: Int = 0
    private var beginX = 0f
    private var beginY = 0f
    private var redoCount: Int = 0

    init {
        setUpDrawing()
    }

    private fun setUpDrawing() {
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize, style)
        mDrawPaint!!.color = color
        mDrawPaint!!.isAntiAlias = true
//        mDrawPaint!!.style = Paint.Style.STROKE
        mBrushSize = 8.toFloat()
        mDrawPaint!!.strokeWidth = mBrushSize
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
    }

    fun onClickUndo() {
        if (mPaths.size > 0) {
            mUndoPaths.add(mPaths.removeAt(mPaths.size - 1))
            redoCount++
            invalidate()
        }
    }

    fun onClickRedo(){
        if(mUndoPaths.size > 0 && redoCount > 0){
            mPaths.add(mUndoPaths.removeAt(mUndoPaths.size - 1))
            redoCount--
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)

        for (path in mPaths) {
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            mDrawPaint!!.style = path.style
            canvas.drawPath(path, mDrawPaint!!)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y
        redoCount = 0

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize
                mDrawPath!!.style = style
                mDrawPath!!.reset()
                mDrawPath!!.moveTo(touchX!!, touchY!!)
                beginX = touchX
                beginY = touchY
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                val left = min(beginX, touchX!!)
                val right = max(beginX, touchX)
                val top = min(beginY, touchY!!)
                val bottom = max(beginY, touchY)

                when (shape) {
                    0,1 -> {
                        mDrawPath!!.style = style
                        mDrawPath!!.lineTo(touchX, touchY)
                    }
//                    1 -> {
//                        mDrawPath!!.reset()
////                        mDrawPath!!.lineTo(touchX, touchY)
//                        mDrawPath!!.addPath(CustomPath(color, mBrushSize), left, top)
//                    }
                    2, 3 -> {
                        mDrawPath!!.reset()
                        mDrawPaint!!.style = Paint.Style.FILL
                        mDrawPath!!.addRect(left, top, right, bottom, Path.Direction.CCW)
                    }
                    4, 5 -> {
                        mDrawPath!!.reset()
                        mDrawPath!!.style = style
                        mDrawPath!!.addCircle(
                            (left+right)/2,
                            (top+bottom)/2,
                            ((((left - right).toDouble().pow(2.0) +
                                    (top - bottom).toDouble().pow(2.0))).pow(0.5) / 2).toFloat(),
                            Path.Direction.CCW
                        )
                    }
                    6, 7 -> {
                        mDrawPath!!.reset()
                        mDrawPath!!.style = style
                        mDrawPath!!.addOval(left, top, right, bottom, Path.Direction.CCW)
                    }
                }

            }
            MotionEvent.ACTION_UP -> {
                val left = min(beginX, touchX!!)
                val right = max(beginX, touchX)
                val top = min(beginY, touchY!!)
                val bottom = max(beginY, touchY)
                when (shape) {
                    0,1 -> {
                        mDrawPath!!.lineTo(touchX, touchY)
                        mDrawPath = CustomPath(color, mBrushSize, style)
                        mPaths.add(mDrawPath!!)
                    }
//                    1 -> {
//                        mDrawPath!!.reset()
//                        mDrawPath!!.style = Paint.Style.FILL
//                        mDrawPath!!.rLineTo(left, top)
//                        mDrawPath = CustomPath(color, mBrushSize)
//                        mPaths.add(mDrawPath!!)
//                    }
                    2, 3 -> {
                        mDrawPath!!.reset()
                        mDrawPath!!.addRect(left, top, right, bottom, Path.Direction.CCW)
                        mDrawPath = CustomPath(color, mBrushSize, style)
                        mPaths.add(mDrawPath!!)
                    }
                    4, 5 -> {
                        mDrawPath!!.reset()
                        mDrawPath!!.addCircle(
                            (left+right)/2,
                            (top+bottom)/2,
                            ((((left - right).toDouble().pow(2.0) +
                                    (top - bottom).toDouble().pow(2.0))).pow(0.5) / 2).toFloat(),
                            Path.Direction.CCW
                        )
                        mDrawPath = CustomPath(color, mBrushSize, style)
                        mPaths.add(mDrawPath!!)
                    }
                    6, 7 -> {
                        mDrawPath!!.reset()
                        mDrawPath!!.addOval(left, top, right, bottom, Path.Direction.CCW)
                        mDrawPath = CustomPath(color, mBrushSize, style)
                        mPaths.add(mDrawPath!!)
                    }
                }
            }
            else -> {
                return false
            }
        }

        invalidate()
        return true
    }

    fun setSizeForBrush(newSize: Float) {
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            newSize, resources.displayMetrics)
        mDrawPaint!!.strokeWidth = mBrushSize
    }

    fun setColor(newColor: Int) {
        color = newColor
    }

    fun setStyle(newStyle: Int){
        style = if(newStyle % 2 == 0 || newStyle == 1)
            Paint.Style.STROKE
        else
            Paint.Style.FILL
    }

    fun setShape( newShape: Int){
        shape = newShape
        //0 -> pencil
        //1 -> line
        //2 -> rectangle stroke
        //3 -> rectangle fill
        //4 -> circle stroke
        //5 -> circle fill
        //6 -> oval stroke
        //7 -> oval fill
    }

    internal inner class CustomPath(
        var color: Int,
        var brushThickness: Float,
        var style: Paint.Style
    ):
        Path()
}