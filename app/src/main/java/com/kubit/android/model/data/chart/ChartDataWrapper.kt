package com.kubit.android.model.data.chart

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry

data class ChartDataWrapper(
    /**
     * 가격 차트에 표시할 가격 데이터
     */
    val candleEntries: List<CandleEntry>,
    /**
     * 거래량 그래프에 표시할 거래량 데이터
     */
    val transactionVolumeEntries: List<BarEntry>,
    /**
     * 거래량 그래프 막대 Color Resource ID List
     */
    val transactionVolumeColors: List<Int>,
    /**
     * 거래량 그래프에 표시할 거래량 이평선 데이터, unit in 5
     */
    val transactionVolumeAvg5Entries: List<Entry>,
    /**
     * 거래량 그래프에 표시할 거래량 이평선 데이터, unit in 10
     */
    val transactionVolumeAvg10Entries: List<Entry>,
    /**
     * 거래량 그래프에 표시할 거래량 이평선 데이터, unit in 20
     */
    val transactionVolumeAvg20Entries: List<Entry>
) {

    override fun toString(): String {
        return "$TAG{" +
                "candleEntries=$candleEntries}"
    }

    companion object {
        private const val TAG: String = "ChartDataWrapper"
    }

}