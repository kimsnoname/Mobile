package com.kubit.android.common.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updateMargins
import com.kubit.android.R
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.model.data.coin.PriceChangeType
import com.kubit.android.model.data.orderbook.OrderBookUnitData
import com.kubit.android.model.data.transaction.TransactionType

class KubitOrderBookLayout : ViewGroup {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    private val itemMinHeight: Int = ConvertUtil.dp2px(context, 35)
    private val itemMargin: Int = ConvertUtil.dp2px(context, 1)

    private var _canScroll: Boolean = false
    private val canScroll: Boolean get() = _canScroll

    private var _minTranslationY: Float = -1f
    private val minTranslationY: Float get() = _minTranslationY

    private var _maxTranslationY: Float = 0f
    private val maxTranslationY: Float get() = _maxTranslationY

    // region Resource
    @ColorInt
    private val textColor: Int = ContextCompat.getColor(context, R.color.text)

    @ColorInt
    private val transactionBlue: Int = ContextCompat.getColor(context, R.color.transaction_blue)

    @ColorInt
    private val transactionRed: Int = ContextCompat.getColor(context, R.color.transaction_red)

    @ColorInt
    private val coinBlue: Int = ContextCompat.getColor(context, R.color.coin_blue)

    @ColorInt
    private val coinRed: Int = ContextCompat.getColor(context, R.color.coin_red)

    private val notoSansKrRegular: Typeface? =
        ResourcesCompat.getFont(context, R.font.noto_sans_kr_regular)
    // endregion Resource

    init {
        val askOrderBookUnitData =
            OrderBookUnitData(TransactionType.ASK, -1.0, 0.0, PriceChangeType.RISE, 0.5)
        val bidOrderBookUnitData =
            OrderBookUnitData(TransactionType.BID, -1.0, 0.0, PriceChangeType.FALL, 0.5)
        for (idx in 0 until 15) {
            val item = createOrderBookUnitView(askOrderBookUnitData)
            addView(item)
        }
        for (idx in 0 until 15) {
            val item = createOrderBookUnitView(bidOrderBookUnitData)
            addView(item)
        }
    }

    private fun getTextColor(changeType: PriceChangeType): Int {
        return when (changeType) {
            PriceChangeType.EVEN -> textColor
            PriceChangeType.RISE -> coinRed
            PriceChangeType.FALL -> coinBlue
        }
    }

    private fun getBackgroundColor(type: TransactionType): Int {
        return when (type) {
            TransactionType.ASK -> transactionBlue
            TransactionType.BID -> transactionRed
        }
    }

    private fun createOrderBookUnitView(unitData: OrderBookUnitData): ViewGroup {
        return ConstraintLayout(context).apply {
            id = View.generateViewId()
            layoutParams = generateDefaultLayoutParams()
            setBackgroundColor(getBackgroundColor(unitData.type))

            // 6:4 비율로 나눌 Guideline
            val guideLine = Guideline(context).apply {
                this.id = View.generateViewId()
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    orientation = ConstraintLayout.LayoutParams.VERTICAL
                }
            }

            // Price
            val priceTextView = TextView(context).apply {
                id = View.generateViewId()
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    updateMargins(
                        left = ConvertUtil.dp2px(context, 10),
                        right = ConvertUtil.dp2px(context, 10)
                    )
                }
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
                setTextColor(getTextColor(unitData.change))
                if (notoSansKrRegular != null) {
                    typeface = notoSansKrRegular
                }
                includeFontPadding = false
                letterSpacing = -0.05f
                gravity = Gravity.RIGHT
            }

            // ChangeRate
            val changeRateTextView = TextView(context).apply {
                id = View.generateViewId()
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    updateMargins(
                        left = ConvertUtil.dp2px(context, 10),
                        right = ConvertUtil.dp2px(context, 10)
                    )
                }
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f)
                setTextColor(getTextColor(unitData.change))
                if (notoSansKrRegular != null) {
                    typeface = notoSansKrRegular
                }
                includeFontPadding = false
                letterSpacing = -0.05f
                gravity = Gravity.RIGHT
            }

            // OrderSize
            val orderSizeTextView = TextView(context).apply {
                id = View.generateViewId()
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    updateMargins(
                        left = ConvertUtil.dp2px(context, 10),
                        right = ConvertUtil.dp2px(context, 10)
                    )
                }
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f)
                setTextColor(textColor)
                if (notoSansKrRegular != null) {
                    typeface = notoSansKrRegular
                }
                includeFontPadding = false
                letterSpacing = -0.05f
                gravity = Gravity.LEFT
            }

            addView(guideLine)
            addView(priceTextView)
            addView(changeRateTextView)
            addView(orderSizeTextView)

            val set = ConstraintSet()
            set.clone(this)

            set.setGuidelinePercent(guideLine.id, 0.65f)
            set.connect(
                priceTextView.id,
                ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT
            )
            set.connect(
                priceTextView.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )
            set.connect(
                priceTextView.id,
                ConstraintSet.RIGHT,
                guideLine.id,
                ConstraintSet.LEFT
            )
            set.connect(
                priceTextView.id,
                ConstraintSet.BOTTOM,
                changeRateTextView.id,
                ConstraintSet.TOP
            )

            set.connect(
                changeRateTextView.id,
                ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT
            )
            set.connect(
                changeRateTextView.id,
                ConstraintSet.TOP,
                priceTextView.id,
                ConstraintSet.BOTTOM
            )
            set.connect(
                changeRateTextView.id,
                ConstraintSet.RIGHT,
                guideLine.id,
                ConstraintSet.LEFT
            )
            set.connect(
                changeRateTextView.id,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )

            set.connect(
                orderSizeTextView.id,
                ConstraintSet.LEFT,
                guideLine.id,
                ConstraintSet.RIGHT
            )
            set.connect(
                orderSizeTextView.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )
            set.connect(
                orderSizeTextView.id,
                ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT
            )
            set.connect(
                orderSizeTextView.id,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )

            setConstraintSet(set)
        }
    }

    fun update(pOrderBookUnitDataList: List<OrderBookUnitData>) {
        for (idx in 0 until childCount) {
            val child = getChildAt(idx)
            val unitData = pOrderBookUnitDataList.getOrNull(idx)

            if (child is ConstraintLayout) {
                val priceTextView = child.getChildAt(1) as TextView
                val changeRateTextView = child.getChildAt(2) as TextView
                val orderSizeTextView = child.getChildAt(3) as TextView

                priceTextView.text = ConvertUtil.tradePrice2string(unitData?.price ?: 0.0)
                changeRateTextView.text = ConvertUtil.changeRate2string(unitData?.changeRate ?: 0.0)
                orderSizeTextView.text = ConvertUtil.orderSize2string(unitData?.size ?: 0.0)

                unitData?.change?.let { change ->
                    priceTextView.setTextColor(getTextColor(change))
                    changeRateTextView.setTextColor(getTextColor(change))
                }
            }
        }
    }

    fun fitCenter() {
        val parentHeight = (parent as View).measuredHeight
        val height = measuredHeight

        if (height > parentHeight) {
            translationY = (parentHeight / 2f) - (height / 2f)

            _minTranslationY = parentHeight.toFloat() - height
            _canScroll = true
        }
    }

    private var _deltaY: Float = 0f
    private val deltaY: Float get() = _deltaY

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return if (ev != null && canScroll) {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    _deltaY = y - ev.rawY
                }

                MotionEvent.ACTION_MOVE -> {
                    val toY = (ev.rawY + deltaY)
                    translationY = when {
                        toY < minTranslationY -> minTranslationY
                        toY > maxTranslationY -> maxTranslationY
                        else -> toY
                    }
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {

                }
            }
            true
        } else {
            false
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight

        val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        val childHeightMeasuresSpec =
            MeasureSpec.makeMeasureSpec(itemMinHeight, MeasureSpec.EXACTLY)

        var yPos = paddingTop + itemMargin
        for (idx in 0 until childCount) {
            val child = getChildAt(idx)
            child.measure(childWidthMeasureSpec, childHeightMeasuresSpec)
            yPos += child.measuredHeight + itemMargin
        }

        val height = yPos
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val xPos = paddingLeft
        var yPos = paddingTop + itemMargin

        for (idx in 0 until childCount) {
            val child = getChildAt(idx)

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            child.layout(xPos, yPos, xPos + childWidth, yPos + childHeight)
            yPos += child.measuredHeight + itemMargin
        }
    }

    override fun checkLayoutParams(lp: ViewGroup.LayoutParams?) = lp is LayoutParams

    override fun generateDefaultLayoutParams() = LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )

    class LayoutParams(width: Int, height: Int) : ViewGroup.LayoutParams(width, height)

    companion object {
        private const val TAG: String = "KubitOrderBookLayout"
    }

}