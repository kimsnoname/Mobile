package com.kubit.android.intro.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kubit.android.base.BaseViewModel
import com.kubit.android.common.session.KubitSession
import com.kubit.android.common.util.DLog
import com.kubit.android.login.viewmodel.LoginViewModel
import com.kubit.android.model.data.login.LoginResult
import com.kubit.android.model.data.login.LoginSessionData
import com.kubit.android.model.data.market.KubitMarketData
import com.kubit.android.model.data.network.KubitNetworkResult
import com.kubit.android.model.data.network.NetworkResult
import com.kubit.android.model.data.wallet.WalletOverall
import com.kubit.android.model.repository.IntroRepository
import com.kubit.android.model.repository.KubitRepository
import kotlinx.coroutines.launch

class IntroViewModel(
    private val introRepository: IntroRepository,
    private val kubitRepository: KubitRepository
) : BaseViewModel() {

    private var _marketData: MutableLiveData<KubitMarketData?> = MutableLiveData(null)
    val marketData: LiveData<KubitMarketData?> get() = _marketData

    private val _loginResult: MutableLiveData<LoginResult?> = MutableLiveData(null)
    val loginResult: LiveData<LoginResult?> get() = _loginResult

    private val _walletRequestResult: MutableLiveData<Boolean?> = MutableLiveData(null)
    val walletRequestResult: LiveData<Boolean?> get() = _walletRequestResult

    fun requestMarketCode() {
        DLog.d("${TAG}_requestMarketCode", "requestMarketCode() is called!")
        viewModelScope.launch {
            when (val result = introRepository.makeMarketCodeRequest()) {
                is NetworkResult.Success<KubitMarketData> -> {
                    DLog.d("${TAG}_requestMarketCode", "marketData=$result")
                    _marketData.value = result.data
                }

                is NetworkResult.Fail -> {
                    setApiFailMsg(result.failMsg)
                }

                is NetworkResult.Error -> {
                    setExceptionData(result.exception)
                }
            }
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

    fun requestLogin() {
        viewModelScope.launch {
            val userID = KubitSession.userID
            val userPW = KubitSession.userPW
            when (val result = kubitRepository.makeLoginRequest(userID, userPW)) {
                is NetworkResult.Success<LoginSessionData> -> {
                    val data = result.data
                    DLog.d(TAG, "loginSessionData=$data")

                    KubitSession.createLoginSession(userID, userPW, data)
                    _loginResult.postValue(LoginResult.SUCCESS)
                }

                is NetworkResult.Fail -> {
                    _loginResult.postValue(LoginResult.FAIL)
                }

                is NetworkResult.Error -> {
                    DLog.e(TAG, result.exception.message, result.exception)
                    _loginResult.postValue(LoginResult.ERROR)
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
                    _walletRequestResult.postValue(true)
                }

                is KubitNetworkResult.Refresh -> {
                    requestRefreshToken { newGrantType, newAccessToken ->
                        requestWalletOverall()
                    }
                }

                is KubitNetworkResult.Fail -> {
                    DLog.e(TAG, "failMsg=${result.failMsg}")
                    _walletRequestResult.postValue(false)
                }

                is KubitNetworkResult.Error -> {
                    DLog.e(TAG, result.exception.message, result.exception)
                    _walletRequestResult.postValue(false)
                }
            }
        }
    }

    companion object {
        private const val TAG: String = "IntroViewModel"
    }

}