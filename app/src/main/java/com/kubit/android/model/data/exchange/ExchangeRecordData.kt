package com.kubit.android.model.data.exchange

data class ExchangeRecordData(
    /**
     * 입금 or 출금
     */
    val exchangeType: ExchangeType,
    /**
     * 입출금 금액
     */
    val krw: Double,
    /**
     * 입출금 시각
     */
    val time: String
)
