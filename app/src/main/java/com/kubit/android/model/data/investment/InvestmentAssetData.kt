package com.kubit.android.model.data.investment

import com.github.mikephil.charting.data.PieEntry
import com.kubit.android.R

data class InvestmentAssetData(
    /**
     * 보유 원화 금액
     */
    val KRW: Double,
    /**
     * 총 보유자산
     */
    val totalAsset: Double,
    /**
     * 총매수 금액
     */
    val totalBidPrice: Double,
    /**
     * 평가손익
     */
    val changeValuation: Double,
    /**
     * 총평가 금액
     */
    val totalValuation: Double,
    /**
     * 수익률
     */
    val earningRate: Double,
    /**
     * 보유 자산 리스트
     */
    val userWalletList: List<InvestmentWalletData>,
    /**
     * 포트폴리오 리스트
     */
    val portfolioList: List<PieEntry>
) : InvestmentData {

    override fun getItemCount(): Int = 2 + userWalletList.size

    override fun getItemType(itemPos: Int): Int = when (itemPos) {
        0 -> R.layout.item_investment_asset
        1 -> R.layout.item_investment_portfolio
        else -> R.layout.item_investment_wallet
    }

}