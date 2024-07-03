package com.kubit.android.model.data.wallet

data class WalletData(
    /**
     * 종목 구분 코드
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
     * 거래 가능한 수량
     */
    val quantityAvailable: Double,
    /**
     * 총 보유 수량
     */
    val quantity: Double,
    /**
     * 전체 매수 금액
     */
    val totalPrice: Double
) {

    /**
     * 매수평균가
     */
    val bidAvgPrice: Double = totalPrice / quantity

    override fun toString(): String {
        return "$TAG{" +
                "market=$market, " +
                "nameKor=$nameKor, " +
                "nameEng=$nameEng, " +
                "quantityAvailable=$quantityAvailable, " +
                "quantity=$quantity, " +
                "totalPrice=$totalPrice}"
    }

    companion object {
        private const val TAG: String = "WalletData"
    }

}