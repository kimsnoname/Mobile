package com.kubit.android.model.data.investment

import com.kubit.android.model.data.transaction.TransactionType

data class NotYetData(
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
     * 거래 타입, 매도 or 매수
     */
    val transactionType: TransactionType,
    /**
     * 주문시간
     */
    val time: String,
    /**
     * 주문수량
     */
    val quantity: Double,
    /**
     * 주문가격
     */
    val price: Double,
    /**
     * 미체결량
     */
    val notYetQuantity: Double,
    /**
     * 선택 여부
     */
    var isSelected: Boolean = false
)