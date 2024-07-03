package com.kubit.android.common.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.kubit.android.R
import com.kubit.android.common.util.ConvertUtil
import kotlin.math.max

/**
 * 코인의 가격 변화량을 보여주는 CustomView
 */
class KubitChangeRateStickView : View {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    /**
     * 부호가 있는 가격 변화율
     */
    private var _signedChangeRate: Double = 0.05
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }
    val signedChangeRate: Float get() = _signedChangeRate.toFloat()

    // region Resource
    @ColorInt
    private val candleColor: Int = ContextCompat.getColor(context, R.color.candle)

    @ColorInt
    private val coinBlueColor: Int = ContextCompat.getColor(context, R.color.coin_blue)

    @ColorInt
    private val coinRedColor: Int = ContextCompat.getColor(context, R.color.coin_red)

    @ColorInt
    private val coinGrayColor: Int = ContextCompat.getColor(context, R.color.gray)
    // endregion Resource

    // region Paint
    private val bluePaint: Paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 5f
        strokeCap = Paint.Cap.SQUARE
        color = coinBlueColor
        letterSpacing = -0.05f
    }

    private val redPaint: Paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 5f
        strokeCap = Paint.Cap.SQUARE
        color = coinRedColor
        letterSpacing = -0.05f
    }

    private val grayPaint: Paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 5f
        strokeCap = Paint.Cap.SQUARE
        color = coinGrayColor
        letterSpacing = -0.05f
    }
    // endregion Paint

    init {
        setBackgroundColor(candleColor)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = ConvertUtil.dp2px(context, 10)
        val height = max(MeasureSpec.getSize(heightMeasureSpec), ConvertUtil.dp2px(context, 40))
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas != null) {
            drawStick(canvas)
        }
    }

    private fun drawStick(pCanvas: Canvas) {
        val width = measuredWidth
        val height = measuredHeight

        val halfWidth = width / 2f
        val halfHeight = height / 2f
        val unitWidth = ConvertUtil.dp2px(context, 1f)
        val unitHeight = height * 0.01f

        val left = paddingLeft.toFloat()
        val top = halfHeight - unitHeight
        val right = width - paddingRight.toFloat()
        val bottom = halfHeight

        val tmpChangeRate = signedChangeRate
        if (tmpChangeRate > 0) {
            pCanvas.drawRect(left, top, right, bottom, redPaint)

            val redHeight = halfHeight * tmpChangeRate
            pCanvas.drawRect(
                halfWidth - unitWidth,
                bottom - redHeight,
                halfWidth + unitWidth,
                bottom,
                redPaint
            )
        } else if (tmpChangeRate < 0) {
            pCanvas.drawRect(left, top, right, bottom, bluePaint)

            val blueHeight = halfHeight * (-tmpChangeRate)
            pCanvas.drawRect(
                halfWidth - unitWidth,
                bottom,
                halfWidth + unitWidth,
                bottom + blueHeight,
                bluePaint
            )
        } else {
            pCanvas.drawRect(left, top, right, bottom, grayPaint)
        }
    }

    fun setSignedChangeRate(pSignedChangeRate: Double) {
        _signedChangeRate = pSignedChangeRate
    }

}