package com.kubit.android.model.repository.thread

import com.kubit.android.common.util.JsonParserUtil
import com.kubit.android.model.data.coin.KubitCoinInfoData
import com.kubit.android.model.data.orderbook.OrderBookData
import org.json.JSONArray
import org.json.JSONException

class KubitOrderBookThread(
    private val selectedCoinData: KubitCoinInfoData,
    private val openingPrice: Double,
    private val onSuccessListener: (orderBookData: OrderBookData) -> Unit,
    private val onFailListener: (failMsg: String) -> Unit,
    private val onErrorListener: (e: Exception) -> Unit
) : BaseNetworkThread(TAG) {

    private val jsonParserUtil: JsonParserUtil = JsonParserUtil()

    private var _isActive: Boolean = true
    val isActive: Boolean get() = _isActive

    fun kill() {
        _isActive = false
    }

    override fun run() {
        val hsParams = HashMap<String, String>().apply {
            put("markets", selectedCoinData.market)
        }

        while (isActive) {
            val data = sendRequest(UPBIT_API_ORDER_BOOK_URL, hsParams, GET)

            try {
                val jsonRoot = JSONArray(data)
                val result = jsonParserUtil.getOrderBookData(jsonRoot, openingPrice)

                if (result != null) {
                    onSuccessListener(result)
                } else {
                    onFailListener("Fail to fetch Coin OrderBook Data!")
                }
            } catch (e: JSONException) {
                onErrorListener(e)
            }

            sleep(SLEEP_TIME)
        }
    }

    companion object {
        private const val TAG: String = "KubitOrderBookThread"

        private const val UPBIT_API_ORDER_BOOK_URL = "${UPBIT_API_HOST_URL}orderbook"
        private const val SLEEP_TIME: Long = 500
    }

}