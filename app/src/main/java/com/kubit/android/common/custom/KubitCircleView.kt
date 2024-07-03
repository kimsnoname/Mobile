package com.kubit.android.common.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.kubit.android.R

class KubitCircleView : View {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    private var _circleColor: Int = ContextCompat.getColor(context, R.color.secondary)
        set(value) {
            field = value
            mPaint.color = value
            invalidate()
        }
    private val circleColor: Int get() = _circleColor

    private val mPaint: Paint = Paint().apply {
        color = circleColor
        isAntiAlias = true
        isDither = false
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    fun setColor(pColor: Int) {
        _circleColor = pColor
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val cx = measuredWidth / 2f
        val cy = measuredHeight / 2f
        val radius = measuredWidth / 2f
        canvas?.drawCircle(cx, cy, radius, mPaint)
    }

}