package com.kubit.android.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.PieEntry
import com.kubit.android.base.BaseViewModel
import com.kubit.android.common.session.KubitSession
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.common.util.DLog
import com.kubit.android.intro.viewmodel.IntroViewModel
import com.kubit.android.model.data.coin.CoinSnapshotData
import com.kubit.android.model.data.coin.KubitCoinInfoData
import com.kubit.android.model.data.exchange.ExchangeRecordData
import com.kubit.android.model.data.investment.InvestmentAssetData
import com.kubit.android.model.data.investment.InvestmentData
import com.kubit.android.model.data.investment.InvestmentNotYetData
import com.kubit.android.model.data.investment.InvestmentRecordData
import com.kubit.android.model.data.investment.InvestmentWalletData
import com.kubit.android.model.data.investment.NotYetData
import com.kubit.android.model.data.login.LoginSessionData
import com.kubit.android.model.data.market.KubitMarketCode
import com.kubit.android.model.data.market.KubitMarketData
import com.kubit.android.model.data.network.KubitNetworkResult
import com.kubit.android.model.data.network.NetworkResult
import com.kubit.android.model.data.route.KubitTabRouter
import com.kubit.android.model.data.wallet.WalletOverall
import com.kubit.android.model.repository.KubitRepository
import com.kubit.android.model.repository.UpbitRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val upbitRepository: UpbitRepository,
    private val kubitRepository: KubitRepository
) : BaseViewModel() {

    /**
     * 현재 선택된 탭이 어떤 탭인지
     */
    private val _tabRouter: MutableLiveData<KubitTabRouter> =
        MutableLiveData(KubitTabRouter.COIN_LIST)
    val tabRouter: LiveData<KubitTabRouter> get() = _tabRouter

    private lateinit var marketData: KubitMarketData

    private var _selectedCoinData: MutableLiveData<KubitCoinInfoData?> = MutableLiveData(null)
    val selectedCoinData: LiveData<KubitCoinInfoData?> get() = _selectedCoinData

    private var _searchQuery: String = ""
    private val searchQuery: String get() = _searchQuery

    /**
     * 코인 스냅샷 데이터 리스트
     */
    private val _coinSnapshotDataList: MutableLiveData<List<CoinSnapshotData>> =
        MutableLiveData(listOf())
    private val coinSnapshotDataList: LiveData<List<CoinSnapshotData>> = _coinSnapshotDataList

    /**
     * 검색어에 의해 필터링된 코인 스냅샷 데이터 리스트
     */
    private val _filteredCoinSnapshotDataList: MutableLiveData<List<CoinSnapshotData>> =
        MutableLiveData(listOf())
    val filteredCoinSnapshotDataList: LiveData<List<CoinSnapshotData>> get() = _filteredCoinSnapshotDataList

    // region 투자내역 화면 관련 변수
    /**
     * 보유자산 화면에 보여줄 데이터
     */
    private val _investmentAssetData: MutableLiveData<InvestmentData?> = MutableLiveData(null)
    val investmentAssetData: LiveData<InvestmentData?> get() = _investmentAssetData

    /**
     * 거래내역 화면에 보여줄 데이터
     */
    private val _investmentRecordData: MutableLiveData<InvestmentData?> = MutableLiveData(null)
    val investmentRecordData: LiveData<InvestmentData?> get() = _investmentRecordData

    /**
     * 미체결 내역 화면에 보여줄 데이터
     */
    private val _investmentNotYetData: MutableLiveData<InvestmentData?> = MutableLiveData(null)
    val investmentNotYetData: LiveData<InvestmentData?> get() = _investmentNotYetData

    private val selectedNotYetData: ArrayList<NotYetData> = arrayListOf()
    val enableRemvoeNotYetData: Boolean get() = selectedNotYetData.isNotEmpty()
    // endregion 투자내역 화면 관련 변수

    // region 입출금 화면 관련 변수
    private val _exchangeRecordData: MutableLiveData<List<ExchangeRecordData>> =
        MutableLiveData(listOf())
    val exchangeRecordData: LiveData<List<ExchangeRecordData>> get() = _exchangeRecordData
    // endregion 입출금 화면 관련 변수

    // region 프로필 화면 관련 변수
    private val _resetResult: MutableLiveData<WalletOverall?> = MutableLiveData(null)
    val resetResult: LiveData<WalletOverall?> get() = _resetResult
    // endregion 프로필 화면 관련 변수

    fun initMarketData(pMarketData: KubitMarketData) {
        marketData = pMarketData
    }

    fun setTabRouter(pTabRouter: KubitTabRouter) {
        if (tabRouter.value != pTabRouter) {
            _tabRouter.value = pTabRouter
        }
    }

    fun setSelectedCoinData(pSelectedCoinData: KubitCoinInfoData?) {
        _selectedCoinData.value = pSelectedCoinData
    }

    fun setSearchQuery(pQuery: String) {
        _searchQuery = pQuery
        coinSnapshotDataList.value?.let { coinSnapshotData ->
            setFilteredCoinSnapshotDataList(coinSnapshotData)
        }
    }

    fun clearResetResult() {
        _resetResult.value = null
    }

    private fun setFilteredCoinSnapshotDataList(
        pSnapshotDataList: List<CoinSnapshotData>
    ) {
        val filteredList = arrayListOf<CoinSnapshotData>()
        val query = searchQuery.lowercase()
        for (snapshot in pSnapshotDataList) {
            if (snapshot.contain(query)) {
                filteredList.add(snapshot)
            }
        }
        _filteredCoinSnapshotDataList.postValue(filteredList)
    }

    fun requestTickerData(
        pMarketCode: KubitMarketCode = KubitMarketCode.KRW
    ) {
        DLog.d(TAG, "requestTickerData() is called!")
        viewModelScope.launch {
            upbitRepository.makeCoinTickerThread(
                pCoinInfoDataList = marketData.getKubitCoinInfoDataList(pMarketCode),
                onSuccessListener = { snapshotDataList ->
                    DLog.d(TAG, "snapshotDataList=$snapshotDataList")
                    _coinSnapshotDataList.postValue(snapshotDataList)
                    setFilteredCoinSnapshotDataList(snapshotDataList)
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
        DLog.d(TAG, "stopTickerData() is called!")
        viewModelScope.launch {
            upbitRepository.stopCoinTickerThread()
        }
    }

    /**
     * 보유자산 데이터를 요청하는 함수
     */
    fun requestInvestmentTickerData() {
        DLog.d(TAG, "requestInvestmentTickerData() is called!")
        viewModelScope.launch {
            upbitRepository.makeInvestmentTickerThread(
                pWalletDataList = KubitSession.walletList,
                onSuccessListener = { snapshotDataList ->
                    DLog.d(TAG, "walletSnapshotDataList=$snapshotDataList")
                    val krw = KubitSession.KRW
                    val walletList = KubitSession.walletList

                    var totalAsset: Double = krw
                    var totalBidPrice: Double = 0.0
                    var totalValuation: Double = 0.0

                    val userWalletList: ArrayList<InvestmentWalletData> = arrayListOf()
                    for (idx in walletList.indices) {
                        if (idx in snapshotDataList.indices) {
                            val wallet = walletList[idx]
                            val snapshotData = snapshotDataList[idx]

                            val valuationPrice = wallet.quantity * snapshotData.tradePrice
                            val changeValuation = valuationPrice - wallet.totalPrice
                            val earningRate =
                                if (wallet.totalPrice > 0) changeValuation / wallet.totalPrice else 0.0

                            totalAsset += valuationPrice
                            totalValuation += valuationPrice
                            totalBidPrice += wallet.totalPrice

                            userWalletList.add(
                                InvestmentWalletData(
                                    market = snapshotData.market,
                                    nameKor = snapshotData.nameKor,
                                    nameEng = snapshotData.nameEng,
                                    changeValuation = changeValuation,
                                    earningRate = earningRate,
                                    quantity = wallet.quantity,
                                    bidAvgPrice = wallet.bidAvgPrice,
                                    valuationPrice = valuationPrice,
                                    askTotalPrice = wallet.totalPrice
                                )
                            )
                        }
                    }

                    val sortedUserWalletList = arrayListOf<InvestmentWalletData>().apply {
                        addAll(userWalletList)
                        add(
                            InvestmentWalletData(
                                market = "KRW",
                                nameKor = "",
                                nameEng = "",
                                changeValuation = 0.0,
                                earningRate = 0.0,
                                quantity = 0.0,
                                bidAvgPrice = 0.0,
                                valuationPrice = krw,
                                askTotalPrice = krw
                            )
                        )
                    }
                    sortedUserWalletList.sortWith { wallet1, wallet2 ->
                        wallet2.valuationPrice.compareTo(wallet1.valuationPrice)
                    }
                    val portfolioList: ArrayList<PieEntry> = arrayListOf()
                    var lastRatio: Double = -1.0
                    var lastLabel: String = "etc"
                    for (idx in sortedUserWalletList.indices) {
                        val wallet = sortedUserWalletList[idx]
                        val valuationPrice = wallet.valuationPrice
                        val ratio = valuationPrice / (totalValuation + krw)

                        if (portfolioList.size < 8) {
                            portfolioList.add(
                                PieEntry(
                                    ratio.toFloat(),
                                    ConvertUtil.ratio2pieChartLabel(ratio),
                                    wallet.market.split('-').getOrNull(1) ?: "KRW"
                                )
                            )
                        } else if (portfolioList.size < 9) {
                            lastRatio = ratio
                            lastLabel = wallet.market.split('-').getOrNull(1) ?: "KRW"
                        } else {
                            lastRatio += ratio
                            lastLabel = "etc"
                        }
                    }
                    if (lastRatio != -1.0) {
                        portfolioList.add(
                            PieEntry(
                                lastRatio.toFloat(),
                                ConvertUtil.ratio2pieChartLabel(lastRatio),
                                lastLabel
                            )
                        )
                    }

                    val changeValuation = totalValuation - totalBidPrice
                    val earningRate =
                        if (totalBidPrice > 0) changeValuation / totalBidPrice else 0.0

                    val assetData = InvestmentAssetData(
                        KRW = krw,
                        totalAsset = totalAsset,
                        totalBidPrice = totalBidPrice,
                        changeValuation = changeValuation,
                        totalValuation = totalValuation,
                        earningRate = earningRate,
                        userWalletList = userWalletList,
                        portfolioList = portfolioList
                    )
                    _investmentAssetData.postValue(assetData)
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
     * 거래내역 데이터를 요청하는 함수
     */
    fun requestInvestmentRecordData() {
        DLog.d(TAG, "requestInvestmentRecordData() is called!")
        setProgressFlag(true)
        viewModelScope.launch {
            val result = kubitRepository.makeTransactionCompletesRequest(
                pGrantType = KubitSession.grantType,
                pAccessToken = KubitSession.accessToken
            )
            when (result) {
                is KubitNetworkResult.Success<InvestmentRecordData> -> {
                    DLog.d(TAG, "recordData=${result.data}")
                    _investmentRecordData.postValue(result.data)
                }

                is KubitNetworkResult.Refresh -> {
                    requestRefreshToken { newGrantType, newAccessToken ->
                        requestInvestmentRecordData()
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
    }

    /**
     * 미체결 내역 데이터를 요청하는 함수
     */
    fun requestInvestmentNotYetData() {
        DLog.d(TAG, "requestInvestmentNotYetData() is called!")
        setProgressFlag(true)
        viewModelScope.launch {
            val result = kubitRepository.makeTransactionWaitRequest(
                pGrantType = KubitSession.grantType,
                pAccessToken = KubitSession.accessToken
            )
            when (result) {
                is KubitNetworkResult.Success<InvestmentNotYetData> -> {
                    DLog.d(TAG, "notYetData=${result.data}")
                    _investmentNotYetData.postValue(result.data)
                }

                is KubitNetworkResult.Refresh -> {
                    requestRefreshToken { newGrantType, newAccessToken ->
                        requestInvestmentNotYetData()
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
    }

    /**
     * 미체결된 거래를 취소 요청하는 함수
     */
    fun requestRemoveNotYetData(): Boolean {
        return if (enableRemvoeNotYetData) {
            DLog.d(TAG, "requestRemoveNotYetData() is called!")
            setProgressFlag(true)
            viewModelScope.launch {
                val result = kubitRepository.makeRemoveTransactionWaitRequest(
                    pNotYetList = selectedNotYetData,
                    pGrantType = KubitSession.grantType,
                    pAccessToken = KubitSession.accessToken
                )

                when (result) {
                    is KubitNetworkResult.Success<Triple<WalletOverall, InvestmentRecordData, InvestmentNotYetData>> -> {
                        val walletOverall = result.data.first
                        val recordData = result.data.second
                        val notYetData = result.data.third

                        KubitSession.setWalletOverall(walletOverall.KRW, walletOverall.walletList)
                        _investmentRecordData.postValue(recordData)
                        _investmentNotYetData.postValue(notYetData)
                    }

                    is KubitNetworkResult.Refresh -> {
                        requestRefreshToken { newGrantType, newAccessToken ->
                            requestRemoveNotYetData()
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
    }

    fun addNotYetData(pNotYetData: NotYetData) {
        val idx = selectedNotYetData.indexOfFirst {
            it.transactionID == pNotYetData.transactionID
        }
        if (idx == -1) {
            selectedNotYetData.add(pNotYetData)
        }
    }

    fun removeNotYetData(pNotYetData: NotYetData) {
        val idx = selectedNotYetData.indexOfFirst {
            it.transactionID == pNotYetData.transactionID
        }
        if (idx != -1) {
            selectedNotYetData.removeAt(idx)
        }
    }

    /**
     * 매수 또는 매도 거래 이후에 거래내역 및 미체결 내역을 조회하는 경우,
     *
     * 데이터 갱신이 필요하기 때문에 이를 초기화 해줘야 함
     */
    fun requestClearInvestmentData() {
        _investmentRecordData.value = null
        _investmentNotYetData.value = null
    }

    /**
     * 입출금 내역 데이터를 요청하는 함수
     */
    fun requestExchangeRecordData() {
        viewModelScope.launch {
            val result = kubitRepository.makeBankRequest(
                pGrantType = KubitSession.grantType,
                pAccessToken = KubitSession.accessToken
            )
            when (result) {
                is KubitNetworkResult.Success<List<ExchangeRecordData>> -> {
                    DLog.d(TAG, "exchangeRecordList=${result.data}")
                    val exchangeRecordList = result.data
                    _exchangeRecordData.postValue(exchangeRecordList)
                }

                is KubitNetworkResult.Refresh -> {
                    requestRefreshToken { newGrantType, newAccessToken ->
                        requestRemoveNotYetData()
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
    }

    /**
     * 서버에 입금을 요청하는 함수
     *
     * @param pDepositPrice     입금 금액
     */
    fun requestDeposit(pDepositPrice: Double): Boolean {
        return if (pDepositPrice > 10000) {
            setProgressFlag(true)
            viewModelScope.launch {
                val realDepositPrice = pDepositPrice.div(10).toInt().times(10).toDouble()
                val result = kubitRepository.makeDepositRequest(
                    pDepositPrice = realDepositPrice,
                    pGrantType = KubitSession.grantType,
                    pAccessToken = KubitSession.accessToken
                )
                when (result) {
                    is KubitNetworkResult.Success<ExchangeRecordData> -> {
                        KubitSession.depositKRW(realDepositPrice)

                        val exchangeRecordList = exchangeRecordData.value
                        if (exchangeRecordList != null) {
                            val newList = arrayListOf<ExchangeRecordData>()
                            newList.addAll(exchangeRecordList)
                            newList.add(result.data)
                            _exchangeRecordData.postValue(newList)
                        } else {
                            _exchangeRecordData.postValue(listOf(result.data))
                        }
                    }

                    is KubitNetworkResult.Refresh -> {
                        requestRefreshToken { newGrantType, newAccessToken ->
                            requestDeposit(pDepositPrice)
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
    }

    /**
     * 서버에 출금을 요청하는 함수
     *
     * @param pWithdrawalPrice  출금 금액
     */
    fun requestWithdrawal(pWithdrawalPrice: Double): Boolean {
        val krw = KubitSession.KRW
        return if (pWithdrawalPrice in 10000.0..krw) {
            setProgressFlag(true)
            viewModelScope.launch {
                val realWithdrawalPrice = pWithdrawalPrice.div(10).toInt().times(10).toDouble()
                val result = kubitRepository.makeWithdrawalRequest(
                    pWithdrawalPrice = realWithdrawalPrice,
                    pGrantType = KubitSession.grantType,
                    pAccessToken = KubitSession.accessToken
                )
                when (result) {
                    is KubitNetworkResult.Success<ExchangeRecordData> -> {
                        KubitSession.withdrawalKRW(realWithdrawalPrice)

                        val exchangeRecordList = exchangeRecordData.value
                        if (exchangeRecordList != null) {
                            val newList = arrayListOf<ExchangeRecordData>()
                            newList.addAll(exchangeRecordList)
                            newList.add(result.data)
                            _exchangeRecordData.postValue(newList)
                        } else {
                            _exchangeRecordData.postValue(listOf(result.data))
                        }
                    }

                    is KubitNetworkResult.Refresh -> {
                        requestRefreshToken { newGrantType, newAccessToken ->
                            requestWithdrawal(pWithdrawalPrice)
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
    }

    fun requestReset() {
        DLog.d(TAG, "requestReset() is called!")
        viewModelScope.launch {
            val result = kubitRepository.makeResetRequest(
                pGrantType = KubitSession.grantType,
                pAccessToken = KubitSession.accessToken
            )
            when (result) {
                is KubitNetworkResult.Success<WalletOverall> -> {
                    DLog.d("${TAG}_requestReset", "walletOverall=${result.data}")
                    val walletOverall = result.data
                    KubitSession.setWalletOverall(
                        pKRW = walletOverall.KRW,
                        pWalletList = walletOverall.walletList
                    )
                    _investmentAssetData.value = null
                    _investmentRecordData.value = null
                    _investmentNotYetData.value = null
                    _exchangeRecordData.value = listOf()
                    _resetResult.value = walletOverall
                }

                is KubitNetworkResult.Refresh -> {
                    requestRefreshToken { newGrantType, newAccessToken ->
                        requestReset()
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
    }

    fun requestWalletOverall() {
        viewModelScope.launch {
            val grantType = KubitSession.grantType
            val accessToken = KubitSession.accessToken
            when (val result = kubitRepository.makeWalletOverallRequest(grantType, accessToken)) {
                is KubitNetworkResult.Success<WalletOverall> -> {
                    val data = result.data
                    DLog.d(TAG, "walletOverall=$data")
                    KubitSession.setWalletOverall(data.KRW, data.walletList)
                    setProgressFlag(false)
                }

                is KubitNetworkResult.Refresh -> {
                    requestRefreshToken { newGrantType, newAccessToken ->
                        requestWalletOverall()
                    }
                }

                is KubitNetworkResult.Fail -> {
                    DLog.e(TAG, "failMsg=${result.failMsg}")
                    setApiFailMsg(result.failMsg)
                }

                is KubitNetworkResult.Error -> {
                    DLog.e(TAG, result.exception.message, result.exception)
                    setExceptionData(result.exception)
                }
            }
        }
    }

    fun requestLogout() {
        DLog.d(TAG, "requestLogout() is called!")
        KubitSession.logout()
        _investmentAssetData.value = null
        _investmentRecordData.value = null
        _investmentNotYetData.value = null
        _exchangeRecordData.value = listOf()
    }

    companion object {
        private const val TAG: String = "MainViewModel"
    }

}