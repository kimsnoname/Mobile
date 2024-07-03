package com.kubit.android.model.data.investment

import com.kubit.android.model.data.transaction.TransactionType

data class RecordData(
    /**
     * 거래 고유 ID
     */
    val transactionID: Int,
    /**
     * 코인 코드, ex) KRW-BTC -> BTC
     */
    val coinCode: String,
    /**
     * 코인 한글명
     */
    val nameKor: String,
    /**
     * 코인 영문명
     */
    val nameEng: String,
    /**
     * 거래 타입, 매수 or 매도
     */
    val transactionType: TransactionType,
    /**
     * 체결시간
     */
    val time: String,
    /**
     * 거래금액
     */
    val transactionPrice: Double,
    /**
     * 거래수량
     */
    val quantity: Double,
    /**
     * 거래단가
     */
    val transactionUnitPrice: Double,
    /**
     * 수수료
     */
    val fee: Double,
    /**
     * 정산금액
     */
    val returnPrice: Double
)
