package com.kubit.android.model.repository

import android.app.Application
import com.github.mikephil.charting.data.CandleEntry
import com.kubit.android.base.BaseNetworkRepository
import com.kubit.android.common.util.DLog
import com.kubit.android.common.util.JsonParserUtil
import com.kubit.android.model.data.chart.ChartDataWrapper
import com.kubit.android.model.data.chart.ChartUnit
import com.kubit.android.model.data.coin.CoinSnapshotData
import com.kubit.android.model.data.coin.KubitCoinInfoData
import com.kubit.android.model.data.orderbook.OrderBookData
import com.kubit.android.model.repository.thread.KubitChartThread
import com.kubit.android.model.repository.thread.KubitOrderBookThread
import com.kubit.android.model.repository.thread.KubitTickerThread

class TransactionRepository(
    private val application: Application
) : BaseNetworkRepository(application, TAG) {

    private val jsonParserUtil: JsonParserUtil = JsonParserUtil()

    private var kubitTickerThread: KubitTickerThread? = null
    private var kubitOrderBookThread: KubitOrderBookThread? = null
    private var kubitChartThread: KubitChartThread? = null

    /**
     * 코인 시세를 주기적으로 가져오는 Thread를 생성하는 함수
     *
     * @param pSelectedCoinData     선택된 코인 정보 데이터
     * @param onSuccessListener     데이터를 성공적으로 가져왔을 때, 호출되는 콜백 함수
     * @param onFailListener        API 호출에 실패했을 때, 호출되는 콜백 함수
     * @param onErrorListener       에러가 발생했을 때, 호출되는 콜백 함수
     */
    fun makeCoinTickerThread(
        pSelectedCoinData: KubitCoinInfoData,
        onSuccessListener: (snapshotDataList: List<CoinSnapshotData>) -> Unit,
        onFailListener: (failMsg: String) -> Unit,
        onErrorListener: (e: Exception) -> Unit
    ) {
        if (kubitTickerThread?.isActive == true) {
            DLog.d(TAG, "tickerThread is already working!")
            return
        }

        DLog.d(
            TAG,
            "makeCoinTickerThread() is called, pSelectedCoinData=$pSelectedCoinData"
        )
        kubitTickerThread =
            KubitTickerThread(pSelectedCoinData, onSuccessListener, onFailListener, onErrorListener)
        kubitTickerThread?.start()
    }

    /**
     * 코인 시세를 주기적으로 가져오는 Thread를 중단하는 함수
     */
    fun stopCoinTickerThread() {
        kubitTickerThread?.kill()
        kubitTickerThread = null
    }

    /**
     * 코인 호가 데이터를 주기적으로 가져오는 Thread를 생성하는 함수
     *
     * @param pSelectedCoinData     선택된 코인 정보 데이터
     * @param pOpeningPrice         코인 시가
     * @param onSuccessListener     데이터를 성공적으로 가져왔을 때, 호출되는 콜백 함수
     * @param onFailListener        API 호출에 실패했을 때, 호출되는 콜백 함수
     * @param onErrorListener       에러가 발생했을 때, 호출되는 콜백 함수
     */
    fun makeCoinOrderBookThread(
        pSelectedCoinData: KubitCoinInfoData,
        pOpeningPrice: Double,
        onSuccessListener: (orderBookData: OrderBookData) -> Unit,
        onFailListener: (failMsg: String) -> Unit,
        onErrorListener: (e: Exception) -> Unit
    ) {
        if (kubitOrderBookThread?.isActive == true) {
            DLog.d(TAG, "orderBookThread is already working!")
            return
        }

        DLog.d(
            TAG,
            "makeCoinOrderBookThread() is called, pSelectedCoinData=$pSelectedCoinData, pOpeningPrice=$pOpeningPrice"
        )
        kubitOrderBookThread = KubitOrderBookThread(
            pSelectedCoinData,
            pOpeningPrice,
            onSuccessListener,
            onFailListener,
            onErrorListener
        )
        kubitOrderBookThread?.start()
    }

    fun stopCoinOrderBookThread() {
        kubitOrderBookThread?.kill()
        kubitOrderBookThread = null
    }

    /**
     * 코인 차트 데이터를 주기적으로 가져오는 Thread를 생성하는 함수
     *
     * @param pSelectedCoinData     선택된 코인 정보 데이터
     * @param pChartUnit            차트 단위
     * @param onSuccessListener     데이터를 성공적으로 가져왔을 때, 호출되는 콜백 함수
     * @param onFailListener        API 호출에 실패했을 때, 호출되는 콜백 함수
     * @param onErrorListener       에러가 발생했을 때, 호출되는 콜백 함수
     */
    fun makeCoinChartThread(
        pSelectedCoinData: KubitCoinInfoData,
        pChartUnit: ChartUnit,
        onSuccessListener: (chartDataWrapper: ChartDataWrapper) -> Unit,
        onFailListener: (failMsg: String) -> Unit,
        onErrorListener: (e: Exception) -> Unit
    ) {
        if (kubitChartThread?.isActive == true) {
            DLog.d(TAG, "chartThread is already working!")
            return
        }

        DLog.d(
            TAG,
            "makeCoinChartThread() is called, pSelectedCoinData=$pSelectedCoinData, pChartUnit=$pChartUnit"
        )
        kubitChartThread = KubitChartThread(
            market = pSelectedCoinData.market,
            initChartUnit = pChartUnit,
            onSuccessListener = onSuccessListener,
            onFailListener = onFailListener,
            onErrorListener = onErrorListener
        )
        kubitChartThread?.start()
    }

    fun stopCoinChartThread() {
        kubitChartThread?.kill()
        kubitChartThread = null
    }

    fun changeCoinChartUnit(pChartUnit: ChartUnit) {
        kubitChartThread?.setChartUnit(pChartUnit)
    }

    companion object {
        private const val TAG: String = "TransactionRepository"
    }

}