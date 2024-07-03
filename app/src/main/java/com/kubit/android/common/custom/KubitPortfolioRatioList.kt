package com.kubit.android.common.custom

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updateMargins
import com.github.mikephil.charting.data.PieEntry
import com.kubit.android.R
import com.kubit.android.common.util.ConvertUtil

class KubitPortfolioRatioList : ViewGroup {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    private val portfolioChartColor: List<Int> = listOf(
        ContextCompat.getColor(context, R.color.portfolio_01),
        ContextCompat.getColor(context, R.color.portfolio_02),
        ContextCompat.getColor(context, R.color.portfolio_03),
        ContextCompat.getColor(context, R.color.portfolio_04),
        ContextCompat.getColor(context, R.color.portfolio_05),
        ContextCompat.getColor(context, R.color.portfolio_06),
        ContextCompat.getColor(context, R.color.portfolio_07),
        ContextCompat.getColor(context, R.color.portfolio_08),
        ContextCompat.getColor(context, R.color.portfolio_09),
        ContextCompat.getColor(context, R.color.portfolio_10)
    )

    private val textColor: Int = ContextCompat.getColor(context, R.color.text)
    private val notoSansKrRegular: Typeface? =
        ResourcesCompat.getFont(context, R.font.noto_sans_kr_regular)

    init {
        for (color in portfolioChartColor) {
            addView(createPortfolioRatioLayout(color))
        }
    }

    fun bindData(portfolioList: List<PieEntry>) {
        for (idx in portfolioList.indices) {
            val portfolio = portfolioList[idx]

            if (idx < childCount) {
                val child = getChildAt(idx)
                child.visibility = View.VISIBLE

                if (child is ConstraintLayout) {
                    val codeTextView = child.getChildAt(1) as TextView
                    val ratioTextView = child.getChildAt(2) as TextView

                    codeTextView.text = portfolio.data as? String ?: ""
                    ratioTextView.text = portfolio.label
                }
            }
        }

        for (idx in portfolioList.size until childCount) {
            val child = getChildAt(idx)
            child.visibility = View.GONE
        }
    }

    private fun createPortfolioRatioLayout(pColor: Int): ConstraintLayout =
        ConstraintLayout(context).apply {
            id = View.generateViewId()
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val circleView = KubitCircleView(context).apply {
                id = View.generateViewId()
                layoutParams = ConstraintLayout.LayoutParams(
                    ConvertUtil.dp2px(context, 5),
                    ConvertUtil.dp2px(context, 5)
                )
                setColor(pColor)
            }

            val codeTextView = TextView(context).apply {
                id = View.generateViewId()
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    updateMargins(
                        left = ConvertUtil.dp2px(context, 5)
                    )
                }
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f)
                setTextColor(textColor)
                if (notoSansKrRegular != null) {
                    typeface = notoSansKrRegular
                }
                includeFontPadding = false
                letterSpacing = -0.05f
            }

            val ratioTextView = TextView(context).apply {
                id = View.generateViewId()
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f)
                setTextColor(textColor)
                if (notoSansKrRegular != null) {
                    typeface = notoSansKrRegular
                }
                includeFontPadding = false
                letterSpacing = -0.05f
                gravity = Gravity.RIGHT
            }

            addView(circleView)
            addView(codeTextView)
            addView(ratioTextView)

            val set = ConstraintSet()
            set.clone(this)

            set.connect(
                circleView.id,
                ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT
            )
            set.connect(
                circleView.id,
                ConstraintSet.TOP,
                codeTextView.id,
                ConstraintSet.TOP
            )
            set.connect(
                circleView.id,
                ConstraintSet.RIGHT,
                codeTextView.id,
                ConstraintSet.LEFT
            )
            set.connect(
                circleView.id,
                ConstraintSet.BOTTOM,
                codeTextView.id,
                ConstraintSet.BOTTOM
            )

            set.connect(
                codeTextView.id,
                ConstraintSet.LEFT,
                circleView.id,
                ConstraintSet.RIGHT
            )
            set.connect(
                codeTextView.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )
            set.connect(
                codeTextView.id,
                ConstraintSet.RIGHT,
                ratioTextView.id,
                ConstraintSet.LEFT
            )
            set.connect(
                codeTextView.id,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )

            set.connect(
                ratioTextView.id,
                ConstraintSet.LEFT,
                codeTextView.id,
                ConstraintSet.RIGHT
            )
            set.connect(
                ratioTextView.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )
            set.connect(
                ratioTextView.id,
                ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT
            )
            set.connect(
                ratioTextView.id,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )

            setConstraintSet(set)
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight

        val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        val childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

        var yPos = paddingTop
        for (idx in 0 until childCount) {
            val child = getChildAt(idx)

            if (child.visibility != View.GONE) {
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
                yPos += child.measuredHeight
            }
        }

        val height = yPos
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val xPos = paddingLeft
        var yPos = paddingTop

        for (idx in 0 until childCount) {
            val child = getChildAt(idx)

            if (child.visibility != View.GONE) {
                val childWidth = child.measuredWidth
                val childHeight = child.measuredHeight

                child.layout(xPos, yPos, xPos + childWidth, yPos + childHeight)
                yPos += childHeight
            }
        }
    }

    override fun checkLayoutParams(lp: ViewGroup.LayoutParams?) = lp is LayoutParams

    override fun generateDefaultLayoutParams() = LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )

    class LayoutParams(width: Int, height: Int) : ViewGroup.LayoutParams(width, height)

    companion object {
        private const val TAG: String = "KubitPortfolioRatioList"
    }

}