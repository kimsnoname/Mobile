package com.kubit.android.common.deco

import android.graphics.Canvas
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView

class BorderItemDecoration(
    private val borderPos: List<BorderPos>,
    private val borderWidth: Int,
    private val borderColor: Int
) : RecyclerView.ItemDecoration() {

    private val mPaint = Paint().apply {
        color = borderColor
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        for (idx in 0 until parent.childCount) {
            val child = parent.getChildAt(idx)
            val position = parent.getChildAdapterPosition(child)

            if (position != RecyclerView.NO_POSITION) {
                val lp = child.layoutParams as RecyclerView.LayoutParams

                for (pos in borderPos) {
                    when (pos) {
                        BorderPos.LEFT -> {
                            val top = parent.paddingTop
                            val bottom = parent.height - parent.paddingBottom
                            c.drawRect(
                                left.toFloat(),
                                top.toFloat(),
                                left.toFloat() + borderWidth,
                                bottom.toFloat(),
                                mPaint
                            )
                        }

                        BorderPos.TOP -> {
                            val top = (child.top + lp.topMargin).toFloat()
                            val bottom = top + borderWidth
                            c.drawRect(left.toFloat(), top, right.toFloat(), bottom, mPaint)
                        }

                        BorderPos.RIGHT -> {
                            val top = parent.paddingTop
                            val bottom = parent.height - parent.paddingBottom
                            c.drawRect(
                                right.toFloat() - borderWidth,
                                top.toFloat(),
                                right.toFloat(),
                                bottom.toFloat(),
                                mPaint
                            )
                        }

                        BorderPos.BOTTOM -> {
                            val top = (child.bottom + lp.bottomMargin).toFloat()
                            val bottom = top + borderWidth
                            c.drawRect(left.toFloat(), top, right.toFloat(), bottom, mPaint)
                        }
                    }
                }
            }
        }
    }

    enum class BorderPos {
        LEFT, TOP, RIGHT, BOTTOM
    }

    companion object {
        private const val TAG: String = "BorderItemDecoration"
    }

}