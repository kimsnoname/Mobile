package com.kubit.android.model.data.orderbook

data class OrderBookData(
    /**
     * 마켓 코드
     */
    val market: String,
    /**
     * 호가 생성 시각
     */
    val timestamp: Long,
    /**
     * 호가 매도 총 잔량
     */
    val totalAskSize: Double,
    /**
     * 호가 매수 총 잔량
     */
    val totalBidSize: Double,
    /**
     * 단위별 호가 데이터
     */
    val unitDataList: List<OrderBookUnitData>
) {

    override fun toString(): String {
        return "$TAG{" +
                "market=$market, " +
                "timestamp=$timestamp, " +
                "totalAskSize=$totalAskSize, " +
                "totalBidSize=$totalBidSize, " +
                "unitDataList=$unitDataList}"
    }

    companion object {
        private const val TAG: String = "OrderBookData"
    }

}