package com.kubit.android.model.data.chart

/**
 * 가격 차트 메인 지표
 */
enum class ChartMainIndicator {
    /**
     * 이동 평균선
     */
    MOVING_AVERAGE,

    /**
     * 볼린져밴드
     */
    BOLLINGER_BANDS,

    /**
     * 일목균형표
     */
    DAILY_BALANCE_TABLE,

    /**
     * 피봇
     */
    PIVOT,

    /**
     * Envelopes
     */
    ENVELOPES,

    /**
     * Price Channels
     */
    PRICE_CHANNELS
}