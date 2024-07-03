package com.kubit.android.model.repository.thread

import com.kubit.android.common.util.JsonParserUtil
import com.kubit.android.model.data.chart.ChartDataWrapper
import com.kubit.android.model.data.chart.ChartUnit
import org.json.JSONArray
import org.json.JSONException

class KubitChartThread(
    private val market: String,
    initChartUnit: ChartUnit,
    private val onSuccessListener: (chartDataWrapper: ChartDataWrapper) -> Unit,
    private val onFailListener: (failMsg: String) -> Unit,
    private val onErrorListener: (e: Exception) -> Unit
) : BaseNetworkThread(TAG) {

    private var _chartUnit: ChartUnit = initChartUnit
    private val chartUnit: ChartUnit get() = _chartUnit

    private val jsonParserUtil: JsonParserUtil = JsonParserUtil()

    private var _isActive: Boolean = true
    val isActive: Boolean get() = _isActive

    fun setChartUnit(pChartUnit: ChartUnit) {
        _chartUnit = pChartUnit
    }

    fun kill() {
        _isActive = false
    }

    override fun run() {
        val hsParams = HashMap<String, String>().apply {
            put("market", market)
            put("count", "200")
        }

        while (isActive) {
            val data = when (chartUnit) {
                ChartUnit.MINUTE_1 -> {
                    hsParams["unit"] = "1"
                    sendRequest("$UPBIT_API_CANDLE_URL/$PATH_MINUTES/1", hsParams, GET)
                }

                ChartUnit.MINUTE_3 -> {
                    hsParams["unit"] = "3"
                    sendRequest("$UPBIT_API_CANDLE_URL/$PATH_MINUTES/3", hsParams, GET)
                }

                ChartUnit.MINUTE_5 -> {
                    hsParams["unit"] = "5"
                    sendRequest("$UPBIT_API_CANDLE_URL/$PATH_MINUTES/5", hsParams, GET)
                }

                ChartUnit.MINUTE_10 -> {
                    hsParams["unit"] = "10"
                    sendRequest("$UPBIT_API_CANDLE_URL/$PATH_MINUTES/10", hsParams, GET)
                }

                ChartUnit.MINUTE_15 -> {
                    hsParams["unit"] = "15"
                    sendRequest("$UPBIT_API_CANDLE_URL/$PATH_MINUTES/15", hsParams, GET)
                }

                ChartUnit.MINUTE_30 -> {
                    hsParams["unit"] = "30"
                    sendRequest("$UPBIT_API_CANDLE_URL/$PATH_MINUTES/30", hsParams, GET)
                }

                ChartUnit.MINUTE_60 -> {
                    hsParams["unit"] = "60"
                    sendRequest("$UPBIT_API_CANDLE_URL/$PATH_MINUTES/60", hsParams, GET)
                }

                ChartUnit.MINUTE_240 -> {
                    hsParams["unit"] = "240"
                    sendRequest("$UPBIT_API_CANDLE_URL/$PATH_MINUTES/240", hsParams, GET)
                }

                ChartUnit.DAY -> {
                    hsParams.remove("unit")
                    sendRequest("$UPBIT_API_CANDLE_URL/$PATH_DAY", hsParams, GET)
                }

                ChartUnit.WEEK -> {
                    hsParams.remove("unit")
                    sendRequest("$UPBIT_API_CANDLE_URL/$PATH_WEEK", hsParams, GET)
                }

                ChartUnit.MONTH -> {
                    hsParams.remove("unit")
                    sendRequest("$UPBIT_API_CANDLE_URL/$PATH_MONTH", hsParams, GET)
                }
            }

            try {
                val jsonRoot = JSONArray(data)
                val result = jsonParserUtil.getChartDataWrapper(jsonRoot)

                if (result != null) {
                    onSuccessListener(result)
                } else {
                    onFailListener("Candle Data is Empty!")
                }
            } catch (e: JSONException) {
                onErrorListener(e)
            }

            sleep(SLEEP_TIME)
        }
    }

    companion object {
        private const val TAG: String = "KubitChartThread"

        private const val SLEEP_TIME: Long = 500

        private const val UPBIT_API_CANDLE_URL = "${UPBIT_API_HOST_URL}candles"
        private const val PATH_MINUTES = "/minutes"
        private const val PATH_DAY = "/days"
        private const val PATH_WEEK = "/weeks"
        private const val PATH_MONTH = "/months"
    }

}