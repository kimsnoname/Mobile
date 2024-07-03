package com.kubit.android.common.util

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.kubit.android.R
import com.kubit.android.common.session.KubitSession
import com.kubit.android.model.data.chart.ChartDataWrapper
import com.kubit.android.model.data.coin.CoinSnapshotData
import com.kubit.android.model.data.coin.KubitCoinInfoData
import com.kubit.android.model.data.coin.PriceChangeType
import com.kubit.android.model.data.exchange.ExchangeRecordData
import com.kubit.android.model.data.exchange.ExchangeType
import com.kubit.android.model.data.investment.InvestmentNotYetData
import com.kubit.android.model.data.investment.InvestmentRecordData
import com.kubit.android.model.data.investment.NotYetData
import com.kubit.android.model.data.investment.RecordData
import com.kubit.android.model.data.login.LoginSessionData
import com.kubit.android.model.data.market.KubitMarketData
import com.kubit.android.model.data.network.KubitNetworkResult
import com.kubit.android.model.data.network.NetworkResult
import com.kubit.android.model.data.orderbook.OrderBookData
import com.kubit.android.model.data.orderbook.OrderBookUnitData
import com.kubit.android.model.data.transaction.TransactionType
import com.kubit.android.model.data.wallet.WalletData
import com.kubit.android.model.data.wallet.WalletOverall
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Locale

class JsonParserUtil {

    // region Base Function
    fun getString(jsonObj: JSONObject, key: String, strDefault: String = "") =
        if (jsonObj.has(key) && !jsonObj.isNull(key)) jsonObj.getString(key)
        else strDefault

    fun getBoolean(jsonObj: JSONObject, key: String, default: Boolean = false): Boolean {
        return if (jsonObj.has(key) && !jsonObj.isNull(key)) {
            val value = jsonObj.getString(key).trim()

            when (value.lowercase(Locale.ROOT)) {
                "yes",
                "true",
                "y",
                "1" -> true

                else -> false
            }
        } else {
            default
        }
    }

    fun getInt(jsonObj: JSONObject, key: String, intDefault: Int = -1): Int {
        return if (jsonObj.has(key) && !jsonObj.isNull(key)) {
            val value = jsonObj.getString(key).trim()

            try {
                value.toInt()
            } catch (e: NumberFormatException) {
                intDefault
            }
        } else {
            intDefault
        }
    }

    fun getLong(jsonObj: JSONObject, key: String, longDefault: Long = -1): Long {
        return if (jsonObj.has(key) && !jsonObj.isNull(key)) {
            val value = jsonObj.getString(key).trim()

            try {
                value.toLong()
            } catch (e: NumberFormatException) {
                longDefault
            }
        } else {
            longDefault
        }
    }

    fun getFloat(jsonObj: JSONObject, key: String, floatDefault: Float = -1f): Float {
        return if (jsonObj.has(key) && !jsonObj.isNull(key)) {
            val value = jsonObj.getString(key).trim()

            try {
                value.toFloat()
            } catch (e: NumberFormatException) {
                floatDefault
            }
        } else {
            floatDefault
        }
    }

    fun getDouble(jsonObj: JSONObject, key: String, doubleDefault: Double = -1.0): Double {
        return if (jsonObj.has(key) && !jsonObj.isNull(key)) {
            val value = jsonObj.getString(key).trim()

            try {
                value.toDouble()
            } catch (e: NumberFormatException) {
                doubleDefault
            }
        } else {
            doubleDefault
        }
    }

    fun getJsonObject(jsonObject: JSONObject, key: String): JSONObject? {
        return if (jsonObject.has(key) && !jsonObject.isNull(key)) {
            try {
                jsonObject.getJSONObject(key)
            } catch (e: JSONException) {
                null
            }
        } else {
            null
        }
    }

    fun getJSONArray(jsonObject: JSONObject, key: String): JSONArray? {
        return if (jsonObject.has(key) && !jsonObject.isNull(key)) {
            try {
                jsonObject.getJSONArray(key)
            } catch (e: JSONException) {
                null
            }
        } else {
            null
        }
    }
    // endregion Base Function

    fun getKubitMarketData(jsonArray: JSONArray): KubitMarketData {
        val ret = KubitMarketData()

        if (jsonArray.length() == 0)
            return ret

        for (idx in 0 until jsonArray.length()) {
            if (!jsonArray.isNull(idx)) {
                val obj = jsonArray.getJSONObject(idx)

                if (obj != null) {
                    val market = getString(obj, KEY_MARKET)
                    val marketCode = market.split('-').ifEmpty { listOf("") }[0]
                    val nameKor = getString(obj, KEY_KOR_NAME)
                    val nameEng = getString(obj, KEY_ENG_NAME)
                    val marketWarning = (getString(obj, KEY_MARKET_WARNING) == "CAUTION")

                    ret.addCoin(
                        KubitCoinInfoData(
                            market,
                            marketCode,
                            nameKor,
                            nameEng,
                            marketWarning
                        )
                    )
                }
            }
        }

        return ret
    }

    fun getCoinSnapshotDataList(
        jsonArray: JSONArray,
        coinInfoDataList: List<KubitCoinInfoData>
    ): List<CoinSnapshotData> {
        val ret = arrayListOf<CoinSnapshotData>()

        for (idx in 0 until jsonArray.length()) {
            if (!jsonArray.isNull(idx)) {
                val obj = jsonArray.getJSONObject(idx)

                if (obj != null) {
                    val market = getString(obj, KEY_MARKET)
                    val marketCode = coinInfoDataList[idx].marketCode
                    val nameKor = coinInfoDataList[idx].nameKor
                    val nameEng = coinInfoDataList[idx].nameEng
                    val tradeDate = getString(obj, KEY_TRADE_DATE)
                    val tradeTime = getString(obj, KEY_TRADE_TIME)
                    val tradeDateKST = getString(obj, KEY_TRADE_DATE_KST)
                    val tradeTimeKST = getString(obj, KEY_TRADE_TIME_KST)
                    val tradeTimestamp = getLong(obj, KEY_TRADE_TIMESTAMP)
                    val openingPrice = getDouble(obj, KEY_OPENING_PRICE)
                    val highPrice = getDouble(obj, KEY_HIGH_PRICE)
                    val lowPrice = getDouble(obj, KEY_LOW_PRICE)
                    val tradePrice = getDouble(obj, KEY_TRADE_PRICE)
                    val prevClosingPrice = getDouble(obj, KEY_PREV_CLOSING_PRICE)
                    val change = when (getString(obj, KEY_CHANGE)) {
                        "EVEN" -> PriceChangeType.EVEN
                        "RISE" -> PriceChangeType.RISE
                        "FALL" -> PriceChangeType.FALL
                        else -> PriceChangeType.EVEN
                    }
                    val changePrice = getDouble(obj, KEY_CHANGE_PRICE)
                    val changeRate = getDouble(obj, KEY_CHANGE_RATE)
                    val signedChangePrice = getDouble(obj, KEY_SIGNED_CHANGE_PRICE)
                    val signedChangeRate = getDouble(obj, KEY_SIGNED_CHANGE_RATE)
                    val tradeVolume = getDouble(obj, KEY_TRADE_VOLUME)
                    val accTradePrice = getDouble(obj, KEY_ACC_TRADE_PRICE)
                    val accTradePrice24H = getDouble(obj, KEY_ACC_TRADE_PRICE_24H)
                    val accTradeVolume = getDouble(obj, KEY_ACC_TRADE_VOLUME)
                    val accTradeVolume24H = getDouble(obj, KEY_ACC_TRADE_VOLUME_24H)
                    val highest52WeekPrice = getDouble(obj, KEY_HIGHEST_52_WEEK_PRICE)
                    val highest52WeekDate = getString(obj, KEY_HIGHEST_52_WEEK_DATE)
                    val lowest52WeekPrice = getDouble(obj, KEY_LOWEST_52_WEEK_PRICE)
                    val lowest52WeekDate = getString(obj, KEY_LOWEST_52_WEEK_DATE)
                    val timestamp = getLong(obj, KEY_TIMESTAMP)

                    if (market.isNotEmpty()) {
                        ret.add(
                            CoinSnapshotData(
                                market,
                                marketCode,
                                nameKor,
                                nameEng,
                                tradeDate,
                                tradeTime,
                                tradeDateKST,
                                tradeTimeKST,
                                tradeTimestamp,
                                openingPrice,
                                highPrice,
                                lowPrice,
                                tradePrice,
                                prevClosingPrice,
                                change,
                                changePrice,
                                changeRate,
                                signedChangePrice,
                                signedChangeRate,
                                tradeVolume,
                                accTradePrice,
                                accTradePrice24H,
                                accTradeVolume,
                                accTradeVolume24H,
                                highest52WeekPrice,
                                highest52WeekDate,
                                lowest52WeekPrice,
                                lowest52WeekDate,
                                timestamp
                            )
                        )
                    }
                }
            }
        }

        return ret
    }

    fun getOrderBookData(
        jsonArray: JSONArray,
        openingPrice: Double
    ): OrderBookData? {
        if (jsonArray.length() == 0) {
            return null
        }

        return if (!jsonArray.isNull(0)) {
            val obj = jsonArray.getJSONObject(0)

            if (obj != null) {
                val market = getString(obj, KEY_MARKET)
                val timestamp = getLong(obj, KEY_TIMESTAMP)
                val totalAskSize = getDouble(obj, KEY_TOTAL_ASK_SIZE)
                val totalBidSize = getDouble(obj, KEY_TOTAL_BID_SIZE)
                val orderBookUnitDataList = arrayListOf<OrderBookUnitData>()
                val bidUnitDataList = arrayListOf<OrderBookUnitData>()

                val orderBookUnits = getJSONArray(obj, KEY_ORDERBOOK_UNITS)
                if (orderBookUnits != null) {
                    for (idx in 0 until orderBookUnits.length()) {
                        val unitObj = orderBookUnits.getJSONObject(idx)

                        if (unitObj != null) {
                            val askPrice = getDouble(unitObj, KEY_ASK_PRICE)
                            val bidPrice = getDouble(unitObj, KEY_BID_PRICE)
                            val askSize = getDouble(unitObj, KEY_ASK_SIZE)
                            val bidSize = getDouble(unitObj, KEY_BID_SIZE)

                            val askChange = when {
                                askPrice < openingPrice -> PriceChangeType.FALL
                                askPrice > openingPrice -> PriceChangeType.RISE
                                else -> PriceChangeType.EVEN
                            }
                            val askChangeRate = (askPrice - openingPrice) / openingPrice
                            val bidChange = when {
                                bidPrice < openingPrice -> PriceChangeType.FALL
                                bidPrice > openingPrice -> PriceChangeType.RISE
                                else -> PriceChangeType.EVEN
                            }
                            val bidChangeRate = (bidPrice - openingPrice) / openingPrice

                            orderBookUnitDataList.add(
                                index = 0,
                                OrderBookUnitData(
                                    TransactionType.ASK,
                                    askPrice,
                                    askSize,
                                    askChange,
                                    askChangeRate
                                )
                            )
                            bidUnitDataList.add(
                                OrderBookUnitData(
                                    TransactionType.BID,
                                    bidPrice,
                                    bidSize,
                                    bidChange,
                                    bidChangeRate
                                )
                            )
                        }
                    }
                }

                orderBookUnitDataList.addAll(bidUnitDataList)
                OrderBookData(
                    market = market,
                    timestamp = timestamp,
                    totalAskSize = totalAskSize,
                    totalBidSize = totalBidSize,
                    unitDataList = orderBookUnitDataList
                )
            } else {
                null
            }
        } else {
            null
        }
    }

    fun getChartDataWrapper(jsonArray: JSONArray): ChartDataWrapper {
        val candleEntries: ArrayList<CandleEntry> = arrayListOf()
        val transactionVolumeEntries: ArrayList<BarEntry> = arrayListOf()
        val transactionVolumeColors: ArrayList<Int> = arrayListOf()

        var transactionVolumeAvg5: Float = 0f
        var transactionVolumeAvg10: Float = 0f
        var transactionVolumeAvg20: Float = 0f
        val transactionVolumeAvg5Entries: ArrayList<Entry> = arrayListOf()
        val transactionVolumeAvg10Entries: ArrayList<Entry> = arrayListOf()
        val transactionVolumeAvg20Entries: ArrayList<Entry> = arrayListOf()

        val length = jsonArray.length()
        for (idx in 1..length) {
            if (!jsonArray.isNull(length - idx)) {
                val obj = jsonArray.getJSONObject(length - idx)

                if (obj != null) {
                    val openingPrice = getDouble(obj, KEY_OPENING_PRICE)
                    val highPrice = getDouble(obj, KEY_HIGH_PRICE)
                    val lowPrice = getDouble(obj, KEY_LOW_PRICE)
                    val tradePrice = getDouble(obj, KEY_TRADE_PRICE)
                    val timestamp = getLong(obj, KEY_TIMESTAMP)
                    val candleAccTradePrice = getDouble(obj, KEY_CANDLE_ACC_TRADE_PRICE)
                    val candleAccTradeVolume = getDouble(obj, KEY_CANDLE_ACC_TRADE_VOLUME)

                    candleEntries.add(
                        CandleEntry(
                            idx.toFloat(),          // x
                            highPrice.toFloat(),    // shadowH
                            lowPrice.toFloat(),     // shadowL
                            openingPrice.toFloat(), // open
                            tradePrice.toFloat()    // close
                        )
                    )

                    transactionVolumeEntries.add(
                        BarEntry(
                            idx.toFloat(),                  // x
                            candleAccTradeVolume.toFloat()  // y
                        )
                    )

                    transactionVolumeColors.add(
                        if (openingPrice <= tradePrice) R.color.coin_red
                        else R.color.coin_blue
                    )

                    transactionVolumeAvg5 += candleAccTradeVolume.toFloat()
                    transactionVolumeAvg10 += candleAccTradeVolume.toFloat()
                    transactionVolumeAvg20 += candleAccTradeVolume.toFloat()

                    if (idx >= 5) {
                        transactionVolumeAvg5Entries.add(
                            Entry(
                                idx.toFloat(),
                                transactionVolumeAvg5 / 5f
                            )
                        )
                        transactionVolumeAvg5 -= transactionVolumeEntries[idx - 4].y
                    }
                    if (idx >= 10) {
                        transactionVolumeAvg10Entries.add(
                            Entry(
                                idx.toFloat(),
                                transactionVolumeAvg10 / 10f
                            )
                        )
                        transactionVolumeAvg10 -= transactionVolumeEntries[idx - 9].y
                    }
                    if (idx >= 20) {
                        transactionVolumeAvg20Entries.add(
                            Entry(
                                idx.toFloat(),
                                transactionVolumeAvg20 / 20f
                            )
                        )
                        transactionVolumeAvg20 -= transactionVolumeEntries[idx - 19].y
                    }
                }
            }
        }

        return ChartDataWrapper(
            candleEntries,
            transactionVolumeEntries,
            transactionVolumeColors,
            transactionVolumeAvg5Entries,
            transactionVolumeAvg10Entries,
            transactionVolumeAvg20Entries
        )
    }

    fun getLoginSessionData(jsonRoot: JSONObject): NetworkResult<LoginSessionData> {
        val resultCode = getInt(jsonRoot, KEY_RESULT_CODE)
        val resultMsg = getString(jsonRoot, KEY_RESULT_MSG)

        if (resultCode != 200) {
            return NetworkResult.Fail(resultMsg)
        }

        val detailObj = getJsonObject(jsonRoot, KEY_DETAIL)
        return if (detailObj != null) {
            val tokenInfoObj = getJsonObject(detailObj, KEY_TOKEN_INFO)

            if (tokenInfoObj != null) {
                val grantType = getString(tokenInfoObj, KEY_GRANT_TYPE)
                val accessToken = getString(tokenInfoObj, KEY_ACCESS_TOKEN)
                val refreshToken = getString(tokenInfoObj, KEY_REFRESH_TOKEN)
                val userName = getString(detailObj, KEY_USERNAME)

                NetworkResult.Success(
                    LoginSessionData(
                        userName, grantType, accessToken, refreshToken
                    )
                )
            } else {
                NetworkResult.Fail(resultMsg)
            }
        } else {
            NetworkResult.Fail(resultMsg)
        }
    }

    fun getWalletOverallData(jsonRoot: JSONObject): KubitNetworkResult<WalletOverall> {
        val resultCode = getInt(jsonRoot, KEY_RESULT_CODE)
        val resultMsg = getString(jsonRoot, KEY_RESULT_MSG)

        // Token 유효기간 만료
        if (resultCode == 403) {
            return KubitNetworkResult.Refresh(KubitSession.refreshToken)
        }
        // 그 외의 오류
        else if (resultCode != 200) {
            return KubitNetworkResult.Fail(resultMsg)
        }

        val detailObj = getJsonObject(jsonRoot, KEY_DETAIL)
        return if (detailObj != null) {
            val krw = getDouble(detailObj, KEY_MONEY)
            val walletArray = getJSONArray(detailObj, KEY_WALLET)

            val walletList: ArrayList<WalletData> = arrayListOf()
            if (walletArray != null) {
                for (idx in 0 until walletArray.length()) {
                    if (!walletArray.isNull(idx)) {
                        val obj = walletArray.getJSONObject(idx)

                        if (obj != null) {
                            val market = getString(obj, KEY_MARKET_CODE)
                            val nameKor = getString(obj, KEY_KOREAN_NAME)
                            val nameEng = getString(obj, KEY_ENGLISH_NAME)
                            val quantityAvailable = getDouble(obj, KEY_QUANTITY_AVAILABLE)
                            val quantity = getDouble(obj, KEY_QUANTITY)
                            val totalPrice = getDouble(obj, KEY_TOTAL_PRICE)

                            walletList.add(
                                WalletData(
                                    market,
                                    nameKor,
                                    nameEng,
                                    quantityAvailable,
                                    quantity,
                                    totalPrice
                                )
                            )
                        }
                    }
                }
            }

            KubitNetworkResult.Success(WalletOverall(krw, walletList))
        } else {
            KubitNetworkResult.Fail(resultMsg)
        }
    }

    fun getTransactionCompletesResponse(jsonRoot: JSONObject): KubitNetworkResult<InvestmentRecordData> {
        val resultCode = getInt(jsonRoot, KEY_RESULT_CODE)
        val resultMsg = getString(jsonRoot, KEY_RESULT_MSG)

        // Token 유효기간 만료
        if (resultCode == 403) {
            return KubitNetworkResult.Refresh(KubitSession.refreshToken)
        }
        // 그 외의 오류
        else if (resultCode != 200) {
            return KubitNetworkResult.Fail(resultMsg)
        }

        val detailObj = getJsonObject(jsonRoot, KEY_DETAIL)
        return if (detailObj != null) {
            val transactionList = getJSONArray(detailObj, KEY_TRANSACTION_LIST)

            val recordList: ArrayList<RecordData> = arrayListOf()
            if (transactionList != null) {
                for (idx in 0 until transactionList.length()) {
                    if (!transactionList.isNull(idx)) {
                        val obj = transactionList.getJSONObject(idx)

                        if (obj != null) {
                            val transactionID = getInt(obj, KEY_TRANSACTION_ID)
                            val market = getString(obj, KEY_MARKET_CODE)
                            val nameKor = getString(obj, KEY_KOREAN_NAME)
                            val nameEng = getString(obj, KEY_ENGLISH_NAME)
                            val quantity = getDouble(obj, KEY_QUANTITY)
                            val transactionType = getString(obj, KEY_TRANSACTION_TYPE)
                            val requestTime = getString(obj, KEY_REQUEST_TIME)
                            val completeTime = getString(obj, KEY_COMPLETE_TIME)
                            val resultType = getString(obj, KEY_RESULT_TYPE)
                            val fee = getDouble(obj, KEY_CHARGE)
                            val requestPrice = getDouble(obj, KEY_REQUEST_PRICE)
                            val completePrice = getDouble(obj, KEY_COMPLETE_PRICE)

                            /**
                             * 거래금액
                             */
                            val transactionPrice = quantity * completePrice

                            if (resultType == "SUCCESS") {
                                // 매도
                                if (transactionType == "ASK") {
                                    recordList.add(
                                        RecordData(
                                            transactionID = transactionID,
                                            coinCode = market.split('-').getOrNull(1) ?: "",
                                            nameKor = nameKor,
                                            nameEng = nameEng,
                                            transactionType = TransactionType.ASK,
                                            time = completeTime,
                                            transactionPrice = transactionPrice,
                                            quantity = quantity,
                                            transactionUnitPrice = completePrice,
                                            fee = fee,
                                            returnPrice = transactionPrice - fee
                                        )
                                    )
                                }
                                // 매수
                                else if (transactionType == "BID") {
                                    recordList.add(
                                        RecordData(
                                            transactionID = transactionID,
                                            coinCode = market.split('-').getOrNull(1) ?: "",
                                            nameKor = nameKor,
                                            nameEng = nameEng,
                                            transactionType = TransactionType.BID,
                                            time = completeTime,
                                            transactionPrice = transactionPrice,
                                            quantity = quantity,
                                            transactionUnitPrice = completePrice,
                                            fee = fee,
                                            returnPrice = transactionPrice + fee
                                        )
                                    )
                                }
                                // error
                                else {
                                    DLog.e(TAG, "Unrecognized TransactionType! $obj")
                                }
                            }
                        }
                    }
                }
            }

            KubitNetworkResult.Success(InvestmentRecordData(recordList))
        } else {
            KubitNetworkResult.Fail(resultMsg)
        }
    }

    fun getTransactionWaitResponse(jsonRoot: JSONObject): KubitNetworkResult<InvestmentNotYetData> {
        val resultCode = getInt(jsonRoot, KEY_RESULT_CODE)
        val resultMsg = getString(jsonRoot, KEY_RESULT_MSG)

        // Token 유효기간 만료
        if (resultCode == 403) {
            return KubitNetworkResult.Refresh(KubitSession.refreshToken)
        }
        // 그 외의 오류
        else if (resultCode != 200) {
            return KubitNetworkResult.Fail(resultMsg)
        }

        val detailObj = getJsonObject(jsonRoot, KEY_DETAIL)
        return if (detailObj != null) {
            val transactionList = getJSONArray(detailObj, KEY_TRANSACTION_LIST)

            val notYetList: ArrayList<NotYetData> = arrayListOf()
            if (transactionList != null) {
                for (idx in 0 until transactionList.length()) {
                    if (!transactionList.isNull(idx)) {
                        val obj = transactionList.getJSONObject(idx)

                        if (obj != null) {
                            val transactionID = getInt(obj, KEY_TRANSACTION_ID)
                            val market = getString(obj, KEY_MARKET_CODE)
                            val nameKor = getString(obj, KEY_KOREAN_NAME)
                            val nameEng = getString(obj, KEY_ENGLISH_NAME)
                            val quantity = getDouble(obj, KEY_QUANTITY)
                            val transactionType = getString(obj, KEY_TRANSACTION_TYPE)
                            val requestTime = getString(obj, KEY_REQUEST_TIME)
                            val completeTime = getString(obj, KEY_COMPLETE_TIME)
                            val resultType = getString(obj, KEY_RESULT_TYPE)
                            val fee = getDouble(obj, KEY_CHARGE)
                            val requestPrice = getDouble(obj, KEY_REQUEST_PRICE)
                            val completePrice = getDouble(obj, KEY_COMPLETE_PRICE)

                            if (resultType == "WAIT") {
                                // 매도
                                if (transactionType == "ASK") {
                                    notYetList.add(
                                        NotYetData(
                                            transactionID = transactionID,
                                            coinCode = market.split('-').getOrNull(1) ?: "",
                                            nameKor = nameKor,
                                            nameEng = nameEng,
                                            transactionType = TransactionType.ASK,
                                            time = requestTime,
                                            quantity = quantity,
                                            price = requestPrice,
                                            notYetQuantity = quantity
                                        )
                                    )
                                }
                                // 매수
                                else if (transactionType == "BID") {
                                    notYetList.add(
                                        NotYetData(
                                            transactionID = transactionID,
                                            coinCode = market.split('-').getOrNull(1) ?: "",
                                            nameKor = nameKor,
                                            nameEng = nameEng,
                                            transactionType = TransactionType.BID,
                                            time = requestTime,
                                            quantity = quantity,
                                            price = requestPrice,
                                            notYetQuantity = quantity
                                        )
                                    )
                                }
                                // error
                                else {
                                    DLog.e(TAG, "Unrecognized TransactionType! $obj")
                                }
                            }
                        }
                    }
                }
            }

            KubitNetworkResult.Success(InvestmentNotYetData(notYetList))
        } else {
            KubitNetworkResult.Fail(resultMsg)
        }
    }

    fun getTransactionResponse(jsonRoot: JSONObject): KubitNetworkResult<WalletOverall> {
        val resultCode = getInt(jsonRoot, KEY_RESULT_CODE)
        val resultMsg = getString(jsonRoot, KEY_RESULT_MSG)

        // Token 유효기간 만료
        if (resultCode == 403) {
            return KubitNetworkResult.Refresh(KubitSession.refreshToken)
        }
        // 그 외의 오류
        else if (resultCode != 200) {
            return KubitNetworkResult.Fail(resultMsg)
        }

        val detailObj = getJsonObject(jsonRoot, KEY_DETAIL)
        return if (detailObj != null) {
            val krw = getDouble(detailObj, KEY_MONEY)
            val walletArray = getJSONArray(detailObj, KEY_WALLET)

            val walletList: ArrayList<WalletData> = arrayListOf()
            if (walletArray != null) {
                for (idx in 0 until walletArray.length()) {
                    if (!walletArray.isNull(idx)) {
                        val obj = walletArray.getJSONObject(idx)

                        if (obj != null) {
                            val market = getString(obj, KEY_MARKET_CODE)
                            val nameKor = getString(obj, KEY_KOREAN_NAME)
                            val nameEng = getString(obj, KEY_ENGLISH_NAME)
                            val quantityAvailable = getDouble(obj, KEY_QUANTITY_AVAILABLE)
                            val quantity = getDouble(obj, KEY_QUANTITY)
                            val totalPrice = getDouble(obj, KEY_TOTAL_PRICE)

                            walletList.add(
                                WalletData(
                                    market,
                                    nameKor,
                                    nameEng,
                                    quantityAvailable,
                                    quantity,
                                    totalPrice
                                )
                            )
                        }
                    }
                }
            }

            KubitNetworkResult.Success(WalletOverall(krw, walletList))
        } else {
            KubitNetworkResult.Fail(resultMsg)
        }
    }

    fun getBankResponse(jsonRoot: JSONObject): KubitNetworkResult<List<ExchangeRecordData>> {
        val resultCode = getInt(jsonRoot, KEY_RESULT_CODE)
        val resultMsg = getString(jsonRoot, KEY_RESULT_MSG)

        // Token 유효기간 만료
        if (resultCode == 403) {
            return KubitNetworkResult.Refresh(KubitSession.refreshToken)
        }
        // 그 외의 오류
        else if (resultCode != 200) {
            return KubitNetworkResult.Fail(resultMsg)
        }

        val detailObj = getJsonObject(jsonRoot, KEY_DETAIL)
        return if (detailObj != null) {
            val bankList = getJSONArray(detailObj, KEY_BANK_LIST)

            val exchangeRecordList: ArrayList<ExchangeRecordData> = arrayListOf()
            if (bankList != null) {
                for (idx in 0 until bankList.length()) {
                    if (!bankList.isNull(idx)) {
                        val obj = bankList.getJSONObject(idx)

                        if (obj != null) {
                            val bankType = getString(obj, KEY_BANK_TYPE)
                            val krw = getDouble(obj, KEY_MONEY)
                            val time = getString(obj, KEY_TRADE_AT)

                            when (bankType) {
                                "DEPOSIT" -> {
                                    exchangeRecordList.add(
                                        ExchangeRecordData(
                                            exchangeType = ExchangeType.DEPOSIT,
                                            krw = krw,
                                            time = time
                                        )
                                    )
                                }

                                "WITHDRAW" -> {
                                    exchangeRecordList.add(
                                        ExchangeRecordData(
                                            exchangeType = ExchangeType.WITHDRAWAL,
                                            krw = krw,
                                            time = time
                                        )
                                    )
                                }

                                else -> {
                                    DLog.e(TAG, "Unrecognized ExchangeType, $obj")
                                }
                            }
                        }
                    }
                }
            }
            KubitNetworkResult.Success(exchangeRecordList)
        } else {
            KubitNetworkResult.Fail(resultMsg)
        }
    }

    fun getExchangeRecordData(jsonRoot: JSONObject): KubitNetworkResult<ExchangeRecordData> {
        val resultCode = getInt(jsonRoot, KEY_RESULT_CODE)
        val resultMsg = getString(jsonRoot, KEY_RESULT_MSG)

        // Token 유효기간 만료
        if (resultCode == 403) {
            return KubitNetworkResult.Refresh(KubitSession.refreshToken)
        }
        // 그 외의 오류
        else if (resultCode != 200) {
            return KubitNetworkResult.Fail(resultMsg)
        }

        val detailObj = getJsonObject(jsonRoot, KEY_DETAIL)
        return if (detailObj != null) {
            val bankObj = getJsonObject(detailObj, KEY_BANK)

            if (bankObj != null) {
                val bankType = getString(bankObj, KEY_BANK_TYPE)
                val krw = getDouble(bankObj, KEY_MONEY)
                val time = getString(bankObj, KEY_TRADE_AT)

                when (bankType) {
                    "DEPOSIT" -> {
                        KubitNetworkResult.Success(
                            ExchangeRecordData(
                                exchangeType = ExchangeType.DEPOSIT,
                                krw = krw,
                                time = time
                            )
                        )
                    }

                    "WITHDRAW" -> {
                        KubitNetworkResult.Success(
                            ExchangeRecordData(
                                exchangeType = ExchangeType.WITHDRAWAL,
                                krw = krw,
                                time = time
                            )
                        )
                    }

                    else -> {
                        KubitNetworkResult.Fail(resultMsg)
                    }
                }
            } else {
                KubitNetworkResult.Fail(resultMsg)
            }
        } else {
            KubitNetworkResult.Fail(resultMsg)
        }
    }

    fun getWalletOverallFromResetRequest(jsonRoot: JSONObject): KubitNetworkResult<WalletOverall> {
        val resultCode = getInt(jsonRoot, KEY_RESULT_CODE)
        val resultMsg = getString(jsonRoot, KEY_RESULT_MSG)

        // Token 유효기간 만료
        if (resultCode == 403) {
            return KubitNetworkResult.Refresh(KubitSession.refreshToken)
        }
        // 그 외의 오류
        else if (resultCode != 200) {
            return KubitNetworkResult.Fail(resultMsg)
        }

        val detailObj = getJsonObject(jsonRoot, KEY_DETAIL)
        return if (detailObj != null) {
            val userObj = getJsonObject(detailObj, KEY_USER)

            if (userObj != null) {
                val krw = getDouble(userObj, KEY_MONEY)
                val wallets = getJSONArray(userObj, KEY_WALLETS)

                val walletList = arrayListOf<WalletData>()
                if (wallets != null) {
                    for (idx in 0 until wallets.length()) {
                        if (!wallets.isNull(idx)) {
                            val obj = wallets.getJSONObject(idx)

                            if (obj != null) {
                                val market = getString(obj, KEY_MARKET_CODE)
                                val nameKor = getString(obj, KEY_KOREAN_NAME)
                                val nameEng = getString(obj, KEY_ENGLISH_NAME)
                                val quantityAvailable = getDouble(obj, KEY_QUANTITY_AVAILABLE)
                                val quantity = getDouble(obj, KEY_QUANTITY)
                                val totalPrice = getDouble(obj, KEY_TOTAL_PRICE)

                                walletList.add(
                                    WalletData(
                                        market,
                                        nameKor,
                                        nameEng,
                                        quantityAvailable,
                                        quantity,
                                        totalPrice
                                    )
                                )
                            }
                        }
                    }
                }

                KubitNetworkResult.Success(
                    WalletOverall(
                        KRW = krw,
                        walletList = walletList
                    )
                )
            } else {
                KubitNetworkResult.Fail(resultMsg)
            }
        } else {
            KubitNetworkResult.Fail(resultMsg)
        }
    }

    fun getRemoveTransactionWaitResponse(jsonRoot: JSONObject): KubitNetworkResult<Triple<WalletOverall, InvestmentRecordData, InvestmentNotYetData>> {
        val resultCode = getInt(jsonRoot, KEY_RESULT_CODE)
        val resultMsg = getString(jsonRoot, KEY_RESULT_MSG)

        // Token 유효기간 만료
        if (resultCode == 403) {
            return KubitNetworkResult.Refresh(KubitSession.refreshToken)
        }
        // 그 외의 오류
        else if (resultCode != 200) {
            return KubitNetworkResult.Fail(resultMsg)
        }

        val detailObj = getJsonObject(jsonRoot, KEY_DETAIL)
        return if (detailObj != null) {
            val transactionList = getJSONArray(detailObj, KEY_TRANSACTION_LIST)

            val recordList: ArrayList<RecordData> = arrayListOf()
            val notYetList: ArrayList<NotYetData> = arrayListOf()
            if (transactionList != null) {
                for (idx in 0 until transactionList.length()) {
                    if (!transactionList.isNull(idx)) {
                        val obj = transactionList.getJSONObject(idx)

                        if (obj != null) {
                            val transactionID = getInt(obj, KEY_TRANSACTION_ID)
                            val market = getString(obj, KEY_MARKET_CODE)
                            val nameKor = getString(obj, KEY_KOREAN_NAME)
                            val nameEng = getString(obj, KEY_ENGLISH_NAME)
                            val quantity = getDouble(obj, KEY_QUANTITY)
                            val transactionType = getString(obj, KEY_TRANSACTION_TYPE)
                            val completeTime =
                                getString(obj, KEY_COMPLETE_TIME)
                            val resultType = getString(obj, KEY_RESULT_TYPE)
                            val fee = getDouble(obj, KEY_CHARGE)
                            val requestPrice = getDouble(obj, KEY_REQUEST_PRICE)
                            val completePrice = getDouble(obj, KEY_COMPLETE_PRICE)

                            /**
                             * 거래금액
                             */
                            val transactionPrice = quantity * completePrice

                            // 거래내역
                            if (resultType == "SUCCESS") {
                                // 매도
                                if (transactionType == "ASK") {
                                    recordList.add(
                                        RecordData(
                                            transactionID = transactionID,
                                            coinCode = market.split('-').getOrNull(1) ?: "",
                                            nameKor = nameKor,
                                            nameEng = nameEng,
                                            transactionType = TransactionType.ASK,
                                            time = completeTime,
                                            transactionPrice = transactionPrice,
                                            quantity = quantity,
                                            transactionUnitPrice = completePrice,
                                            fee = fee,
                                            returnPrice = transactionPrice - fee
                                        )
                                    )
                                }
                                // 매수
                                else if (transactionType == "BID") {
                                    recordList.add(
                                        RecordData(
                                            transactionID = transactionID,
                                            coinCode = market.split('-').getOrNull(1) ?: "",
                                            nameKor = nameKor,
                                            nameEng = nameEng,
                                            transactionType = TransactionType.BID,
                                            time = completeTime,
                                            transactionPrice = transactionPrice,
                                            quantity = quantity,
                                            transactionUnitPrice = completePrice,
                                            fee = fee,
                                            returnPrice = transactionPrice + fee
                                        )
                                    )
                                }
                                // error
                                else {
                                    DLog.e(TAG, "Unrecognized TransactionType! $obj")
                                }
                            }
                            // 미체결내역
                            else if (resultType == "WAIT") {
                                // 매도
                                if (transactionType == "ASK") {
                                    notYetList.add(
                                        NotYetData(
                                            transactionID = transactionID,
                                            coinCode = market.split('-').getOrNull(1) ?: "",
                                            nameKor = nameKor,
                                            nameEng = nameEng,
                                            transactionType = TransactionType.ASK,
                                            time = "",
                                            quantity = quantity,
                                            price = requestPrice,
                                            notYetQuantity = quantity
                                        )
                                    )
                                }
                                // 매수
                                else if (transactionType == "BID") {
                                    notYetList.add(
                                        NotYetData(
                                            transactionID = transactionID,
                                            coinCode = market.split('-').getOrNull(1) ?: "",
                                            nameKor = nameKor,
                                            nameEng = nameEng,
                                            transactionType = TransactionType.BID,
                                            time = "",
                                            quantity = quantity,
                                            price = requestPrice,
                                            notYetQuantity = quantity
                                        )
                                    )
                                }
                                // error
                                else {
                                    DLog.e(TAG, "Unrecognized TransactionType! $obj")
                                }
                            }
                        }
                    }
                }
            }

            val krw = getDouble(detailObj, KEY_MONEY)
            val wallets = getJSONArray(detailObj, KEY_WALLETS)

            val walletList = arrayListOf<WalletData>()
            if (wallets != null) {
                for (idx in 0 until wallets.length()) {
                    if (!wallets.isNull(idx)) {
                        val obj = wallets.getJSONObject(idx)

                        if (obj != null) {
                            val market = getString(obj, KEY_MARKET_CODE)
                            val nameKor = getString(obj, KEY_KOREAN_NAME)
                            val nameEng = getString(obj, KEY_ENGLISH_NAME)
                            val quantityAvailable = getDouble(obj, KEY_QUANTITY_AVAILABLE)
                            val quantity = getDouble(obj, KEY_QUANTITY)
                            val totalPrice = getDouble(obj, KEY_TOTAL_PRICE)

                            walletList.add(
                                WalletData(
                                    market,
                                    nameKor,
                                    nameEng,
                                    quantityAvailable,
                                    quantity,
                                    totalPrice
                                )
                            )
                        }
                    }
                }
            }

            val walletOverall = WalletOverall(KRW = krw, walletList = walletList)
            val recordData = InvestmentRecordData(recordList = recordList)
            val notYetData = InvestmentNotYetData(notYetList = notYetList)
            KubitNetworkResult.Success(Triple(walletOverall, recordData, notYetData))
        } else {
            KubitNetworkResult.Fail(resultMsg)
        }
    }

    companion object {
        private const val TAG: String = "JsonParserUtil"

        private const val KEY_MARKET: String = "market"
        private const val KEY_KOR_NAME: String = "korean_name"
        private const val KEY_ENG_NAME: String = "english_name"
        private const val KEY_MARKET_WARNING: String = "market_warning"

        private const val KEY_TRADE_DATE: String = "trade_date"
        private const val KEY_TRADE_TIME: String = "trade_time"
        private const val KEY_TRADE_DATE_KST: String = "trade_date_kst"
        private const val KEY_TRADE_TIME_KST: String = "trade_time_kst"
        private const val KEY_TRADE_TIMESTAMP: String = "trade_timestamp"
        private const val KEY_OPENING_PRICE: String = "opening_price"
        private const val KEY_HIGH_PRICE: String = "high_price"
        private const val KEY_LOW_PRICE: String = "low_price"
        private const val KEY_TRADE_PRICE: String = "trade_price"
        private const val KEY_PREV_CLOSING_PRICE: String = "prev_closing_price"
        private const val KEY_CHANGE: String = "change"
        private const val KEY_CHANGE_PRICE: String = "change_price"
        private const val KEY_CHANGE_RATE: String = "change_rate"
        private const val KEY_SIGNED_CHANGE_PRICE: String = "signed_change_price"
        private const val KEY_SIGNED_CHANGE_RATE: String = "signed_change_rate"
        private const val KEY_TRADE_VOLUME: String = "trade_volume"
        private const val KEY_ACC_TRADE_PRICE: String = "acc_trade_price"
        private const val KEY_ACC_TRADE_PRICE_24H: String = "acc_trade_price_24h"
        private const val KEY_ACC_TRADE_VOLUME: String = "acc_trade_volume"
        private const val KEY_ACC_TRADE_VOLUME_24H: String = "acc_trade_volume_24h"
        private const val KEY_HIGHEST_52_WEEK_PRICE: String = "highest_52_week_price"
        private const val KEY_HIGHEST_52_WEEK_DATE: String = "highest_52_week_date"
        private const val KEY_LOWEST_52_WEEK_PRICE: String = "lowest_52_week_price"
        private const val KEY_LOWEST_52_WEEK_DATE: String = "lowest_52_week_date"
        private const val KEY_TIMESTAMP: String = "timestamp"

        private const val KEY_TOTAL_ASK_SIZE: String = "total_ask_size"
        private const val KEY_TOTAL_BID_SIZE: String = "total_bid_size"
        private const val KEY_ORDERBOOK_UNITS: String = "orderbook_units"
        private const val KEY_ASK_PRICE: String = "ask_price"
        private const val KEY_BID_PRICE: String = "bid_price"
        private const val KEY_ASK_SIZE: String = "ask_size"
        private const val KEY_BID_SIZE: String = "bid_size"

        private const val KEY_CANDLE_DATE_TIME_UTC: String = "candle_date_time_utc"
        private const val KEY_CANDLE_DATE_TIME_KST: String = "candle_date_time_kst"
        private const val KEY_CANDLE_ACC_TRADE_PRICE: String = "candle_acc_trade_price"
        private const val KEY_CANDLE_ACC_TRADE_VOLUME: String = "candle_acc_trade_volume"

        private const val KEY_RESULT_CODE: String = "result_code"
        private const val KEY_RESULT_MSG: String = "result_msg"
        private const val KEY_DETAIL: String = "detail"
        private const val KEY_TOKEN_INFO: String = "tokenInfo"
        private const val KEY_GRANT_TYPE: String = "grantType"
        private const val KEY_ACCESS_TOKEN: String = "accessToken"
        private const val KEY_REFRESH_TOKEN: String = "refreshToken"
        private const val KEY_USERNAME: String = "username"

        private const val KEY_MONEY: String = "money"
        private const val KEY_WALLET: String = "wallet"
        private const val KEY_MARKET_CODE: String = "marketCode"
        private const val KEY_KOREAN_NAME: String = "koreanName"
        private const val KEY_ENGLISH_NAME: String = "englishName"
        private const val KEY_QUANTITY_AVAILABLE: String = "quantityAvailable"
        private const val KEY_QUANTITY: String = "quantity"
        private const val KEY_TOTAL_PRICE: String = "totalPrice"

        private const val KEY_TRANSACTION_LIST: String = "transactionList"
        private const val KEY_TRANSACTION_ID: String = "transactionId"
        private const val KEY_TRANSACTION_TYPE: String = "transactionType"
        private const val KEY_REQUEST_TIME: String = "requestTime"
        private const val KEY_COMPLETE_TIME: String = "completeTime"
        private const val KEY_RESULT_TYPE: String = "resultType"
        private const val KEY_CHARGE: String = "charge"
        private const val KEY_REQUEST_PRICE: String = "requestPrice"
        private const val KEY_COMPLETE_PRICE: String = "completePrice"

        private const val KEY_BANK_LIST: String = "bankList"
        private const val KEY_BANK_TYPE: String = "bankType"
        private const val KEY_TRADE_AT: String = "tradeAt"
        private const val KEY_BANK: String = "bank"

        private const val KEY_USER: String = "user"
        private const val KEY_WALLETS: String = "wallets"
    }

}