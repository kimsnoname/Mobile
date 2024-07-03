package com.kubit.android.investment.viewholder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kubit.android.R
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.databinding.ItemInvestmentAssetBinding
import com.kubit.android.model.data.investment.InvestmentAssetData

class InvestmentAssetViewHolder(
    private val binding: ItemInvestmentAssetBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val context: Context get() = binding.root.context

    private val coinBlueColor: Int = ContextCompat.getColor(context, R.color.coin_blue)
    private val coinRedColor: Int = ContextCompat.getColor(context, R.color.coin_red)
    private val textColor: Int = ContextCompat.getColor(context, R.color.text)

    private fun getTextColor(pChangeValuation: Double): Int =
        if (pChangeValuation > 0) coinRedColor else if (pChangeValuation < 0) coinBlueColor else textColor

    fun bindData(pData: InvestmentAssetData) {
        val pos = bindingAdapterPosition

        binding.apply {
            // 보유 KRW
            tvInvestmentAssetItemKrw.text = ConvertUtil.tradePrice2string(pData.KRW)
            // 총 보유자산
            tvInvestmentAssetItemTotalAsset.text = ConvertUtil.tradePrice2string(pData.totalAsset)
            // 총매수
            tvInvestmentAssetItemTotalBidPrice.text =
                ConvertUtil.tradePrice2string(pData.totalBidPrice)
            // 평가손익
            tvInvestmentAssetItemChangeValuation.text =
                ConvertUtil.tradePrice2string(pData.changeValuation)
            tvInvestmentAssetItemChangeValuation.setTextColor(getTextColor(pData.changeValuation))
            // 총평가
            tvInvestmentAssetItemTotalValuation.text =
                ConvertUtil.tradePrice2string(pData.totalValuation)
            // 수익률
            tvInvestmentAssetItemEarningRate.text = ConvertUtil.changeRate2string(pData.earningRate)
            tvInvestmentAssetItemEarningRate.setTextColor(getTextColor(pData.changeValuation))
        }
    }

}