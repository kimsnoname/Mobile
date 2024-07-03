package com.kubit.android.model.data.orderbook

import com.kubit.android.model.data.coin.PriceChangeType
import com.kubit.android.model.data.transaction.TransactionType

data class OrderBookUnitData(
    val type: TransactionType,
    val price: Double,
    val size: Double,
    val change: PriceChangeType,
    val changeRate: Double
) {

    override fun toString(): String {
        return "$TAG{" +
                "type=$type, " +
                "price=$price, " +
                "size=$size, " +
                "change=$change, " +
                "changeRate=$changeRate}"
    }

    companion object {
        private const val TAG: String = "OrderBookUnitData"
    }

}