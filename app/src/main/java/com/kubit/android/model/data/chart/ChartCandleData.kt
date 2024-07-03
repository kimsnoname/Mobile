package com.kubit.android.model.data.chart

data class ChartCandleData(
    val market: String,
    val candleDateTimeUTC: String,
    val candleDateTimeKST: String,
    val openingPrice: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val tradePrice: Double,
    val timestamp: Long,
    val candleAccTradePrice: Double,
    val candleAccTradeVolume: Double
) {

    override fun toString(): String {
        return "$TAG{" +
                "market=$market, " +
                "openingPrice=$openingPrice, " +
                "highPrice=$highPrice, " +
                "lowPrice=$lowPrice, " +
                "tradePrice=$tradePrice, " +
                "timestamp=$timestamp, " +
                "candleAccTradeVolume=$candleAccTradeVolume}"
    }

    companion object {
        private const val TAG: String = "ChartCandleData"
    }

}