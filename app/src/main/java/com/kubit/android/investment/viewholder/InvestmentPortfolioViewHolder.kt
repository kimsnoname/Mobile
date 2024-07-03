package com.kubit.android.investment.viewholder

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.kubit.android.R
import com.kubit.android.databinding.ItemInvestmentPortfolioBinding
import com.kubit.android.model.data.investment.InvestmentAssetData

class InvestmentPortfolioViewHolder(
    private val binding: ItemInvestmentPortfolioBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val context: Context get() = binding.root.context

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

    init {
        binding.apply {
            pcInvestmentPortfolio.apply {
                // Whether to show the middle hole
                isDrawHoleEnabled = true
                holeRadius = 40f
                setHoleColor(Color.TRANSPARENT)
                transparentCircleRadius = 0f

                // Whether to show text in the middle of the pie chart
                setDrawCenterText(true)
                centerText = context.getString(R.string.investmentPortfolioItem_assetPercent)
                setCenterTextColor(ContextCompat.getColor(context, R.color.text))
                setCenterTextSize(14f)

                isRotationEnabled = false
                // Displayed as a percentage
                setUsePercentValues(true)
                description.isEnabled = false

                setBackgroundColor(Color.TRANSPARENT)
                legend.isEnabled = false

                setEntryLabelColor(ContextCompat.getColor(context, R.color.background))
                setEntryLabelTextSize(12f)
            }

            ivInvestmentPortfolioButton.setOnClickListener {
                val prev = clInvestmentPortfolioChart.visibility
                clInvestmentPortfolioChart.visibility =
                    if (prev == View.VISIBLE) View.GONE else View.VISIBLE
                ivInvestmentPortfolioButton.setImageResource(
                    if (prev == View.VISIBLE) R.drawable.icon_up else R.drawable.icon_down
                )
            }
        }
    }

    fun bindData(pData: InvestmentAssetData) {
        val pos = bindingAdapterPosition

        binding.apply {
            pcInvestmentPortfolio.apply {
                val portfolioDataSet = PieDataSet(pData.portfolioList, "").apply {
                    colors = portfolioChartColor
                    setDrawValues(false)
                    selectionShift = 0f
                }
                data = PieData(portfolioDataSet)

                invalidate()
            }

            cvInvestmentPortfolio.bindData(pData.portfolioList)
        }
    }

}