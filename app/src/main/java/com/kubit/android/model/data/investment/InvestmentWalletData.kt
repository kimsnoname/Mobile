package com.kubit.android.model.data.investment

data class InvestmentWalletData(
    /**
     * 마켓 코드
     */
    val market: String,
    /**
     * 코인 한글명
     */
    val nameKor: String,
    /**
     * 코인 영문명
     */
    val nameEng: String,
    /**
     * 평가손익
     */
    val changeValuation: Double,
    /**
     * 수익률
     */
    val earningRate: Double,
    /**
     * 보유수량
     */
    val quantity: Double,
    /**
     * 매수평균가
     */
    val bidAvgPrice: Double,
    /**
     * 평가금액
     */
    val valuationPrice: Double,
    /**
     * 매수금액
     */
    val askTotalPrice: Double
)