package com.kubit.android.investment.viewholder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kubit.android.R
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.common.util.DLog
import com.kubit.android.databinding.ItemInvestmentWalletBinding
import com.kubit.android.model.data.investment.InvestmentAssetData

class InvestmentWalletViewHolder(
    private val binding: ItemInvestmentWalletBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val context: Context get() = binding.root.context

    private val coinBlueColor: Int = ContextCompat.getColor(context, R.color.coin_blue)
    private val coinRedColor: Int = ContextCompat.getColor(context, R.color.coin_red)
    private val textColor: Int = ContextCompat.getColor(context, R.color.text)

    private fun getTextColor(pEarningRate: Double): Int =
        if (pEarningRate > 0) coinRedColor else if (pEarningRate < 0) coinBlueColor else textColor

    fun bindData(pData: InvestmentAssetData) {
        val pos = bindingAdapterPosition
        val wallet = pData.userWalletList[pos - 2]
        DLog.d(TAG, "wallet=$wallet")

        binding.apply {
            // 코인 한글명
            tvInvestmentWalletItemNameKor.text = wallet.nameKor
            // 코인 영문명
            tvInvestmentWalletItemNameEng.text = "(${wallet.nameEng})"
            // 평가손익
            tvInvestmentWalletItemChangeValuation.text =
                ConvertUtil.tradePrice2string(wallet.changeValuation)
            tvInvestmentWalletItemChangeValuation.setTextColor(getTextColor(wallet.earningRate))
            // 수익률
            tvInvestmentWalletItemEarningRate.text =
                ConvertUtil.changeRate2string(wallet.earningRate)
            tvInvestmentWalletItemEarningRate.setTextColor(getTextColor(wallet.earningRate))
            // 보유수량
            tvInvestmentWalletItemQuantity.text = ConvertUtil.coinQuantity2string(wallet.quantity)
            // 매수평균가
            tvInvestmentWalletItemBidAvgPrice.text =
                ConvertUtil.tradePrice2string(wallet.bidAvgPrice)
            // 평가금액
            tvInvestmentWalletItemValuationPrice.text =
                ConvertUtil.tradePrice2string(wallet.valuationPrice)
            // 매수금액
            tvInvestmentWalletItemBidPrice.text =
                ConvertUtil.tradePrice2string(wallet.askTotalPrice)
        }
    }

    companion object {
        private const val TAG: String = "InvestmentWalletViewHolder"
    }

}