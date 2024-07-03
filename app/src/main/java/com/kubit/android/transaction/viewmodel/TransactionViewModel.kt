package com.kubit.android.transaction.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kubit.android.base.BaseViewModel
import com.kubit.android.common.session.KubitSession
import com.kubit.android.common.util.DLog
import com.kubit.android.intro.viewmodel.IntroViewModel
import com.kubit.android.model.data.chart.ChartDataWrapper
import com.kubit.android.model.data.chart.ChartMainIndicator
import com.kubit.android.model.data.chart.ChartUnit
import com.kubit.android.model.data.coin.CoinSnapshotData
import com.kubit.android.model.data.coin.KubitCoinInfoData
import com.kubit.android.model.data.login.LoginSessionData
import com.kubit.android.model.data.network.KubitNetworkResult
import com.kubit.android.model.data.network.NetworkResult
import com.kubit.android.model.data.orderbook.OrderBookData
import com.kubit.android.model.data.route.TransactionTabRouter
import com.kubit.android.model.data.transaction.TransactionMethod
import com.kubit.android.model.data.transaction.TransactionType
import com.kubit.android.model.data.wallet.WalletData
import com.kubit.android.model.data.wallet.WalletOverall
import com.kubit.android.model.repository.KubitRepository
import com.kubit.android.model.repository.TransactionRepository
import kotlinx.coroutines.launch
import kotlin.math.abs

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val kubitRepository: KubitRepository
) : BaseViewModel() {

    private lateinit var selectedCoinData: KubitCoinInfoData

    private lateinit var _selectedWallet: WalletData
    val selectedWallet: WalletData get() = _selectedWallet

    private val _tabRouter: MutableLiveData<TransactionTabRouter> = MutableLiveData()
    val tabRouter: LiveData<TransactionTabRouter> get() = _tabRouter

    // region 호가창 화면 관련 변수
    private val _coinSnapshotData: MutableLiveData<CoinSnapshotData> = MutableLiveData()
    val coinSnapshotData: LiveData<CoinSnapshotData> get() = _coinSnapshotData

    /**
     * 코인 시가
     */
    private val _coinOpeningPrice: MutableLiveData<Double?> = MutableLiveData(null)
    val coinOpeningPrice: LiveData<Double?> get() = _coinOpeningPrice

    /**
     * 코인 현재가 -> 호가창 화면 초기화 목적으로 사용
     */
    private val _coinTradePrice: MutableLiveData<Double?> = MutableLiveData(null)
    val coinTradePrice: LiveData<Double?> get() = _coinTradePrice

    /**
     * 사용자가 입력한 코인의 매수 주문 수량
     */
    private val _bidOrderQuantity: MutableLiveData<Double> = MutableLiveData(0.0)
    val bidOrderQuantity: LiveData<Double> get() = _bidOrderQuantity

    /**
     * 사용자가 입력한 코인의 매도 주문 수량
     */
    private val _askOrderQuantity: MutableLiveData<Double> = MutableLiveData(0.0)
    val askOrderQuantity: LiveData<Double> get() = _askOrderQuantity

    /**
     * 매수 주문에서의 코인 1개당 가격
     */
    private val _bidOrderUnitPrice: MutableLiveData<Double> = MutableLiveData(0.0)
    val bidOrderUnitPrice: LiveData<Double> get() = _bidOrderUnitPrice

    /**
     * 매도 주문에서의 코인 1개당 가격
     */
    private val _askOrderUnitPrice: MutableLiveData<Double> = MutableLiveData(0.0)
    val askOrderUnitPrice: LiveData<Double> get() = _askOrderUnitPrice

    /**
     * 매수 지정가 거래에서의 총액
     */
    private val _bidOrderTotalPrice: MutableLiveData<Double> = MutableLiveData(0.0)
    val bidOrderTotalPrice: LiveData<Double> get() = _bidOrderTotalPrice

    /**
     * 매도 지정가 거래에서의 총액
     */
    private val _askOrderTotalPrice: MutableLiveData<Double> = MutableLiveData(0.0)
    val askOrderTotalPrice: LiveData<Double> get() = _askOrderTotalPrice

    /**
     * 코인 호가 데이터
     */
    private val _orderBookData: MutableLiveData<OrderBookData?> = MutableLiveData(null)
    val orderBookData: LiveData<OrderBookData?> get() = _orderBookData

    /**
     * 매수 매도 여부
     */
    private val _transactionType: MutableLiveData<TransactionType> =
        MutableLiveData(TransactionType.BID)
    val transactionType: LiveData<TransactionType> get() = _transactionType

    /**
     * 매수 - 지정가 거래인지 시장가 거래인지
     */
    private val _bidTransactionMethod: MutableLiveData<TransactionMethod> =
        MutableLiveData(TransactionMethod.DESIGNATED_PRICE)
    val bidTransactionMethod: LiveData<TransactionMethod> get() = _bidTransactionMethod

    /**
     * 매도 - 지정가 거래인지 시장가 거래인지
     */
    private val _askTransactionMethod: MutableLiveData<TransactionMethod> =
        MutableLiveData(TransactionMethod.DESIGNATED_PRICE)
    val askTransactionMethod: LiveData<TransactionMethod> get() = _askTransactionMethod

    /**
     * 매수 거래 요청 이후의 KRW 및 Wallet 데이터
     */
    private val _bidTransactionResult: MutableLiveData<WalletOverall?> = MutableLiveData(null)
    val bidTransactionResult: LiveData<WalletOverall?> get() = _bidTransactionResult

    /**
     * 매도 거래 요청 이후의 KRW 및 Wallet 데이터
     */
    private val _askTransactionResult: MutableLiveData<WalletOverall?> = MutableLiveData(null)
    val askTransactionResult: LiveData<WalletOverall?> get() = _askTransactionResult
    // endregion 호가창 화면 관련 변수

    // region 차트 화면 관련 변수
    /**
     * 가격 차트 메인 지표
     */
    private val _chartMainIndicator: MutableLiveData<ChartMainIndicator> =
        MutableLiveData(ChartMainIndicator.MOVING_AVERAGE)
    val chartMainIndicator: LiveData<ChartMainIndicator> get() = _chartMainIndicator

    /**
     * 차트 단위
     */
    private val _chartUnit: MutableLiveData<ChartUnit> = MutableLiveData(unitMinute)
    val chartUnit: LiveData<ChartUnit> get() = _chartUnit

    /**
     * 분 단위
     */
    private var _unitMinute: ChartUnit = ChartUnit.MINUTE_3
    private val unitMinute: ChartUnit get() = _unitMinute

    /**
     * 차트 데이터 Wrapper
     */
    private val _chartDataWrapper: MutableLiveData<ChartDataWrapper?> = MutableLiveData(null)
    val chartDataWrapper: LiveData<ChartDataWrapper?> get() = _chartDataWrapper
    // endregion 차트 화면 관련 변수

    fun initSelectedCoinData(pSelectedCoinData: KubitCoinInfoData) {
        selectedCoinData = pSelectedCoinData

        val walletList = KubitSession.walletList
        for (idx in walletList.indices) {
            val wallet = walletList[idx]
            if (wallet.market == selectedCoinData.market) {
                _selectedWallet = wallet
                break
            }
        }

        if (!this::_selectedWallet.isInitialized) {
            _selectedWallet = WalletData(
                market = selectedCoinData.market,
                nameKor = selectedCoinData.nameKor,
                nameEng = selectedCoinData.nameEng,
                quantityAvailable = 0.0,
                quantity = 0.0,
                totalPrice = 0.0
            )
        }

        requestTickerData()
    }

    fun setTabRouter(pTabRouter: TransactionTabRouter) {
        if (tabRouter.value != pTabRouter) {
            _tabRouter.value = pTabRouter
        }
    }

    fun setTransactionType(pTransactionType: TransactionType) {
        if (transactionType.value != pTransactionType) {
            _transactionType.value = pTransactionType
        }
    }

    fun setBidTransactionMethod(pTransactionMethod: TransactionMethod) {
        if (bidTransactionMethod.value != pTransactionMethod) {
            _bidTransactionMethod.value = pTransactionMethod
        }
    }

    fun setAskTransactionMethod(pTransactionMethod: TransactionMethod) {
        if (askTransactionMethod.value != pTransactionMethod) {
            _askTransactionMethod.value = pTransactionMethod
        }
    }

    /**
     * 매수 지정가 거래에서의 주문 수량을 설정하는 함수
     *
     * @param pOrderQuantity    주문 수량
     */
    fun setBidOrderQuantity(pOrderQuantity: Double) {
        DLog.d(TAG, "setBidOrderQuantity(), pOrderQuantity=$pOrderQuantity")
        _bidOrderQuantity.postValue(pOrderQuantity)
        _bidOrderTotalPrice.postValue(pOrderQuantity * (bidOrderUnitPrice.value ?: 0.0))
    }

    /**
     * 매도 지정가 거래에서의 주문 수량을 설정하는 함수
     *
     * @param pOrderQuantity    주문 수량
     */
    fun setAskOrderQuantity(pOrderQuantity: Double) {
        DLog.d(TAG, "setAskOrderQuantity(), pOrderQuantity=$pOrderQuantity")
        _askOrderQuantity.postValue(pOrderQuantity)
        _askOrderTotalPrice.postValue(pOrderQuantity * (askOrderUnitPrice.value ?: 0.0))
    }

    /**
     * 매수 - 지정가 거래에서의 코인 1개당 가격을 설정하는 함수
     *
     * @param pOrderUnitPrice   코인 1개당 가격
     */
    fun setBidOrderUnitPrice(pOrderUnitPrice: Double) {
        DLog.d(TAG, "setBidOrderUnitPrice(), pOrderUnitPrice=$pOrderUnitPrice")
        _bidOrderUnitPrice.postValue(pOrderUnitPrice)
        _bidOrderTotalPrice.postValue((bidOrderQuantity.value ?: 0.0) * pOrderUnitPrice)
    }

    /**
     * 매도 - 지정가 거래에서의 코인 1개당 가격을 설정하는 함수
     *
     * @param pOrderUnitPrice   코인 1개당 가격
     */
    fun setAskOrderUnitPrice(pOrderUnitPrice: Double) {
        DLog.d(TAG, "setAskOrderUnitPrice(), pOrderUnitPrice=$pOrderUnitPrice")
        _askOrderUnitPrice.postValue(pOrderUnitPrice)
        _askOrderTotalPrice.postValue((askOrderQuantity.value ?: 0.0) * pOrderUnitPrice)
    }

    /**
     * 매수 총 주문 금액을 설정하는 함수
     *
     * @param pOrderTotalPrice  총 주문 금액
     */
    fun setBidOrderTotalPrice(pOrderTotalPrice: Double) {
        DLog.d(TAG, "setBidOrderTotalPrice(), pOrderTotalPrice=$pOrderTotalPrice")
        _bidOrderTotalPrice.postValue(pOrderTotalPrice)

        bidOrderUnitPrice.value?.let { unitPrice ->
            if (unitPrice > 0) {
                val quantity = (pOrderTotalPrice / unitPrice).toInt().toDouble()
                _bidOrderQuantity.postValue(quantity)
            } else {
                val newUnitPrice = coinTradePrice.value!!
                _bidOrderUnitPrice.postValue(newUnitPrice)
                val quantity = (pOrderTotalPrice / newUnitPrice).toInt().toDouble()
                _bidOrderQuantity.postValue(quantity)
            }
        }
    }

    /**
     * 매도 총 주문 금액을 설정하는 함수
     *
     * @param pOrderTotalPrice  총 주문 금액
     */
    fun setAskOrderTotalPrice(pOrderTotalPrice: Double) {
        DLog.d(TAG, "setAskOrderTotalPrice(), pOrderTotalPrice=$pOrderTotalPrice")
        _askOrderTotalPrice.postValue(pOrderTotalPrice)

        askOrderUnitPrice.value?.let { unitPrice ->
            if (unitPrice > 0) {
                val quantity = (pOrderTotalPrice / unitPrice).toInt().toDouble()
                _askOrderQuantity.postValue(quantity)
            } else {
                val newUnitPrice = coinTradePrice.value!!
                _askOrderUnitPrice.postValue(newUnitPrice)
                val quantity = (pOrderTotalPrice / newUnitPrice).toInt().toDouble()
                _askOrderQuantity.postValue(quantity)
            }
        }
    }

    /**
     * 사용자가 설정한 매수 금액 및 수량을 초기화하는 함수
     */
    fun clearBidPriceAndQuantity() {
        _bidOrderQuantity.postValue(0.0)
        _bidOrderTotalPrice.postValue(0.0)
        coinTradePrice.value?.let { tradePrice ->
            _bidOrderUnitPrice.postValue(tradePrice)
        }
    }

    /**
     * 사용자가 설정한 매도 금액 및 수량을 초기화하는 함수
     */
    fun clearAskPriceAndQuantity() {
        _askOrderQuantity.postValue(0.0)
        _askOrderTotalPrice.postValue(0.0)
        coinTradePrice.value?.let { tradePrice ->
            _askOrderUnitPrice.postValue(tradePrice)
        }
    }

    fun setChartMainIndicator(pChartMainIndicator: ChartMainIndicator) {
        if (chartMainIndicator.value != pChartMainIndicator) {
            _chartMainIndicator.value = pChartMainIndicator
        }
    }

    fun setChartUnitToMinute() {
        if (chartUnit.value != unitMinute) {
            _chartUnit.value = unitMinute
            transactionRepository.changeCoinChartUnit(unitMinute)
        }
    }

    fun setChartUnitToMinute(pUnitMinute: ChartUnit) {
        when (pUnitMinute) {
            ChartUnit.MINUTE_1,
            ChartUnit.MINUTE_3,
            ChartUnit.MINUTE_5,
            ChartUnit.MINUTE_10,
            ChartUnit.MINUTE_15,
            ChartUnit.MINUTE_30,
            ChartUnit.MINUTE_60,
            ChartUnit.MINUTE_240 -> {
                if (unitMinute != pUnitMinute) {
                    _unitMinute = pUnitMinute
                    _chartUnit.value = pUnitMinute
                    transactionRepository.changeCoinChartUnit(pUnitMinute)
                }
            }

            else -> {
                DLog.e(TAG, "$pUnitMinute is not Minute Unit!")
            }
        }
    }

    fun setChartUnitToDay() {
        if (chartUnit.value != ChartUnit.DAY) {
            _chartUnit.value = ChartUnit.DAY
            transactionRepository.changeCoinChartUnit(ChartUnit.DAY)
        }
    }

    fun setChartUnitToWeek() {
        if (chartUnit.value != ChartUnit.WEEK) {
            _chartUnit.value = ChartUnit.WEEK
            transactionRepository.changeCoinChartUnit(ChartUnit.WEEK)
        }
    }

    fun setChartUnitToMonth() {
        if (chartUnit.value != ChartUnit.MONTH) {
            _chartUnit.value = ChartUnit.MONTH
            transactionRepository.changeCoinChartUnit(ChartUnit.MONTH)
        }
    }

    fun requestTickerData() {
        DLog.d(TAG, "requestTickerData() is called!")
        viewModelScope.launch {
            transactionRepository.makeCoinTickerThread(
                pSelectedCoinData = selectedCoinData,
                onSuccessListener = { snapshotDataList ->
                    snapshotDataList.firstOrNull()?.let { snapshotData ->
                        _coinSnapshotData.postValue(snapshotData)

                        if (coinOpeningPrice.value == null) {
                            _coinOpeningPrice.postValue(snapshotData.openingPrice)
                        }
                        if (coinTradePrice.value == null) {
                            _coinTradePrice.postValue(snapshotData.tradePrice)
                        }
                    }
                },
                onFailListener = { failMsg ->
                    DLog.e(TAG, failMsg)
                    setApiFailMsg(failMsg)
                },
                onErrorListener = { e ->
                    DLog.e(TAG, e.message, e)
                    setExceptionData(e)
                }
            )
        }
    }

    fun stopTickerData() {
        DLog.d(TAG, "stopTickerData() is called")
        viewModelScope.launch {
            transactionRepository.stopCoinTickerThread()
        }
    }

    fun requestCoinOrderBook() {
        DLog.d(TAG, "requestCoinOrderBook() is called!")
        viewModelScope.launch {
            transactionRepository.makeCoinOrderBookThread(
                pSelectedCoinData = selectedCoinData,
                pOpeningPrice = coinOpeningPrice.value ?: 0.0,
                onSuccessListener = { orderBookData ->
                    _orderBookData.postValue(orderBookData)
                },
                onFailListener = { failMsg ->
                    DLog.e(TAG, failMsg)
                    setApiFailMsg(failMsg)
                },
                onErrorListener = { e ->
                    DLog.e(TAG, e.message, e)
                    setExceptionData(e)
                }
            )
        }
    }

    fun stopCoinOrderBook() {
        DLog.d(TAG, "stopCoinOrderBook() is called")
        viewModelScope.launch {
            transactionRepository.stopCoinOrderBookThread()
        }
    }

    private fun requestRefreshToken(
        onSuccessListener: (newGrantType: String, newAccessToken: String) -> Unit
    ) {
        viewModelScope.launch {
            when (val refreshTokenResult =
                kubitRepository.makeRefreshTokenRequest(KubitSession.refreshToken)) {
                is NetworkResult.Success<LoginSessionData> -> {
                    val loginSession = refreshTokenResult.data
                    DLog.d(TAG, "new LoginSession is $loginSession")
                    KubitSession.updateLoginSession(
                        pGrantType = loginSession.grantType,
                        pAccessToken = loginSession.accessToken,
                        pRefreshToken = loginSession.refreshToken
                    )
                    onSuccessListener(loginSession.grantType, loginSession.accessToken)
                }

                is NetworkResult.Fail -> {
                    DLog.e(TAG, refreshTokenResult.failMsg)
                }

                is NetworkResult.Error -> {
                    DLog.e(TAG, refreshTokenResult.exception.message, refreshTokenResult.exception)
                }
            }
        }
    }

    /**
     * 지정가 매수 거래를 요청하는 함수
     */
    fun requestDesignatedBid(): Boolean {
        val krw = KubitSession.KRW
        val marketCode = coinSnapshotData.value?.market
        val quantity = bidOrderQuantity.value
        val unitPrice = bidOrderUnitPrice.value
        val totalPrice = bidOrderTotalPrice.value

        return if (marketCode != null && quantity != null && unitPrice != null && totalPrice != null) {
            val fee = totalPrice * FEE_RATIO
            DLog.d("${TAG}_requestBidTransaction", "krw=$krw, totalPrice=$totalPrice, fee=$fee")

            if ((totalPrice + fee) <= krw) {
                setProgressFlag(true)
                viewModelScope.launch {
                    val result = kubitRepository.makeDesignatedRequest(
                        pTransactionType = "BID",
                        pMarketCode = marketCode,
                        pRequestPrice = unitPrice,
                        pQuantity = quantity,
                        pGrantType = KubitSession.grantType,
                        pAccessToken = KubitSession.accessToken
                    )

                    when (result) {
                        is KubitNetworkResult.Success<WalletOverall> -> {
                            DLog.d("${TAG}_requestDesignatedBid", result.data.toString())
                            val walletOverall = result.data
                            KubitSession.setWalletOverall(
                                walletOverall.KRW,
                                walletOverall.walletList
                            )
                            for (idx in walletOverall.walletList.indices) {
                                val wallet = walletOverall.walletList[idx]

                                if (wallet.market == selectedWallet.market) {
                                    _selectedWallet = wallet
                                    break
                                }
                            }
                            _bidTransactionResult.postValue(walletOverall)
                        }

                        is KubitNetworkResult.Refresh -> {
                            requestRefreshToken { newGrantType, newAccessToken ->
                                requestDesignatedBid()
                            }
                        }

                        is KubitNetworkResult.Fail -> {
                            DLog.e(TAG, result.failMsg)
                            setApiFailMsg(result.failMsg)
                        }

                        is KubitNetworkResult.Error -> {
                            DLog.e(TAG, result.exception.message, result.exception)
                            setExceptionData(result.exception)
                        }
                    }
                }
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    /**
     * 지정가 매도 거래를 요청하는 함수
     */
    fun requestDesignatedAsk(): Boolean {
        val hasQuantity = selectedWallet.quantityAvailable
        val marketCode = coinSnapshotData.value?.market
        val quantity = askOrderQuantity.value
        val unitPrice = askOrderUnitPrice.value
        val totalPrice = askOrderTotalPrice.value

        return if (marketCode != null && quantity != null && unitPrice != null && totalPrice != null) {
            if (quantity <= hasQuantity) {
                setProgressFlag(true)
                viewModelScope.launch {
                    val result = kubitRepository.makeDesignatedRequest(
                        pTransactionType = "ASK",
                        pMarketCode = marketCode,
                        pRequestPrice = unitPrice,
                        pQuantity = quantity,
                        pGrantType = KubitSession.grantType,
                        pAccessToken = KubitSession.accessToken
                    )

                    when (result) {
                        is KubitNetworkResult.Success<WalletOverall> -> {
                            DLog.d("${TAG}_requestDesignatedAsk", result.data.toString())
                            val walletOverall = result.data
                            KubitSession.setWalletOverall(
                                walletOverall.KRW,
                                walletOverall.walletList
                            )
                            for (idx in walletOverall.walletList.indices) {
                                val wallet = walletOverall.walletList[idx]

                                if (wallet.market == selectedWallet.market) {
                                    _selectedWallet = wallet
                                    break
                                }
                            }
                            _askTransactionResult.postValue(walletOverall)
                        }

                        is KubitNetworkResult.Refresh -> {
                            requestRefreshToken { newGrantType, newAccessToken ->
                                requestDesignatedBid()
                            }
                        }

                        is KubitNetworkResult.Fail -> {
                            DLog.e(TAG, result.failMsg)
                            setApiFailMsg(result.failMsg)
                        }

                        is KubitNetworkResult.Error -> {
                            DLog.e(TAG, result.exception.message, result.exception)
                            setExceptionData(result.exception)
                        }
                    }
                }
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    /**
     * 시장가 매수 거래를 요청하는 함수
     */
    fun requestMarketBid(): Boolean {
        val krw = KubitSession.KRW
        val tradePrice = coinSnapshotData.value?.tradePrice
        val marketCode = coinSnapshotData.value?.market
        val totalPrice = bidOrderTotalPrice.value

        return if (marketCode != null && tradePrice != null && totalPrice != null) {
            val fee = totalPrice * FEE_RATIO
            DLog.d("${TAG}_requestBidTransaction", "krw=$krw, totalPrice=$totalPrice, fee=$fee")

            if ((totalPrice + fee) <= krw) {
                setProgressFlag(true)
                viewModelScope.launch {
                    val result = kubitRepository.makeMarketBidRequest(
                        pMarketCode = marketCode,
                        pCurrentPrice = tradePrice,
                        pTotalPrice = totalPrice,
                        pGrantType = KubitSession.grantType,
                        pAccessToken = KubitSession.accessToken
                    )

                    when (result) {
                        is KubitNetworkResult.Success<WalletOverall> -> {
                            DLog.d("${TAG}_requestMarketBid", result.data.toString())
                            val walletOverall = result.data
                            KubitSession.setWalletOverall(
                                walletOverall.KRW,
                                walletOverall.walletList
                            )
                            for (idx in walletOverall.walletList.indices) {
                                val wallet = walletOverall.walletList[idx]

                                if (wallet.market == selectedWallet.market) {
                                    _selectedWallet = wallet
                                    break
                                }
                            }
                            _bidTransactionResult.postValue(walletOverall)
                        }

                        is KubitNetworkResult.Refresh -> {
                            requestRefreshToken { newGrantType, newAccessToken ->
                                requestMarketBid()
                            }
                        }

                        is KubitNetworkResult.Fail -> {
                            DLog.e(TAG, result.failMsg)
                            setApiFailMsg(result.failMsg)
                        }

                        is KubitNetworkResult.Error -> {
                            DLog.e(TAG, result.exception.message, result.exception)
                            setExceptionData(result.exception)
                        }
                    }
                }
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    /**
     * 시장가 매도 거래를 요청하는 함수
     */
    fun requestMarketAsk(): Boolean {
        val hasQuantity = selectedWallet.quantityAvailable
        val tradePrice = coinSnapshotData.value?.tradePrice
        val marketCode = coinSnapshotData.value?.market
        val quantity = askOrderQuantity.value

        return if (tradePrice != null && marketCode != null && quantity != null) {
            if (quantity <= hasQuantity) {
                setProgressFlag(true)
                viewModelScope.launch {
                    val result = kubitRepository.makeMarketAskRequest(
                        pMarketCode = marketCode,
                        pCurrentPrice = tradePrice,
                        pQuantity = quantity,
                        pGrantType = KubitSession.grantType,
                        pAccessToken = KubitSession.accessToken
                    )

                    when (result) {
                        is KubitNetworkResult.Success<WalletOverall> -> {
                            DLog.d("${TAG}_requestMarketBid", result.data.toString())
                            val walletOverall = result.data
                            KubitSession.setWalletOverall(
                                walletOverall.KRW,
                                walletOverall.walletList
                            )
                            for (idx in walletOverall.walletList.indices) {
                                val wallet = walletOverall.walletList[idx]

                                if (wallet.market == selectedWallet.market) {
                                    _selectedWallet = wallet
                                    break
                                }
                            }
                            _askTransactionResult.postValue(walletOverall)
                        }

                        is KubitNetworkResult.Refresh -> {
                            requestRefreshToken { newGrantType, newAccessToken ->
                                requestMarketBid()
                            }
                        }

                        is KubitNetworkResult.Fail -> {
                            DLog.e(TAG, result.failMsg)
                            setApiFailMsg(result.failMsg)
                        }

                        is KubitNetworkResult.Error -> {
                            DLog.e(TAG, result.exception.message, result.exception)
                            setExceptionData(result.exception)
                        }
                    }
                }
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    fun requestCoinChart() {
        DLog.d(TAG, "requestCoinChart() is called!")
        viewModelScope.launch {
            transactionRepository.makeCoinChartThread(
                pSelectedCoinData = selectedCoinData,
                pChartUnit = chartUnit.value ?: ChartUnit.MINUTE_3,
                onSuccessListener = { chartDataWrapper ->
                    DLog.d(TAG, "chartDataWrapper=$chartDataWrapper")
                    _chartDataWrapper.postValue(chartDataWrapper)
                },
                onFailListener = { failMsg ->
                    DLog.e(TAG, failMsg)
                    setApiFailMsg(failMsg)
                },
                onErrorListener = { e ->
                    DLog.e(TAG, e.message, e)
                    setExceptionData(e)
                }
            )
        }
    }

    fun stopCoinChart() {
        DLog.d(TAG, "stopCoinChart() is called")
        viewModelScope.launch {
            transactionRepository.stopCoinChartThread()
        }
    }

    companion object {
        private const val TAG: String = "TransactionViewModel"

        private const val FEE_RATIO: Double = 0.0005
    }

}