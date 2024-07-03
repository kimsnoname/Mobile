package com.kubit.android.common.session

import android.content.Context
import android.content.SharedPreferences
import com.kubit.android.common.util.DLog
import com.kubit.android.model.data.login.LoginSessionData
import com.kubit.android.model.data.wallet.WalletData

object KubitSession {

    private var _sharedPreferences: SharedPreferences? = null
    private val sharedPreferences: SharedPreferences get() = _sharedPreferences!!

    private var _editor: SharedPreferences.Editor? = null
    private val editor: SharedPreferences.Editor get() = _editor!!

    /**
     * 원화 금액
     */
    private var _KRW: Double = 0.0
    val KRW: Double get() = _KRW

    /**
     * 보유 코인 리스트
     */
    private var _walletList: ArrayList<WalletData> = arrayListOf()
    val walletList: List<WalletData> get() = _walletList

    fun init(pContext: Context) {
        DLog.d(TAG, "init KubitSession!")
        _sharedPreferences = pContext.getSharedPreferences(LOGIN_PREF_NAME, Context.MODE_PRIVATE)
        _editor = sharedPreferences.edit()
    }

    fun createLoginSession(
        pUserID: String,
        pUserPW: String,
        pLoginSessionData: LoginSessionData
    ) {
        createLoginSession(
            pUserID,
            pUserPW,
            pLoginSessionData.userName,
            pLoginSessionData.grantType,
            pLoginSessionData.accessToken,
            pLoginSessionData.refreshToken
        )
    }

    /**
     * 로그인 세션을 만드는 함수
     *
     * 새로 로그인을 수행했을 때 반드시 호출해야 함!
     *
     * @param pUserID           사용자 계정 아이디
     * @param pUserPW           사용자 계정 비밀번호
     * @param pUserName         사용자 계정 닉네임
     * @param pGrantType        ?
     * @param pAccessToken      AWS Access Token
     * @param pRefreshToken     AWS Refresh Token
     */
    fun createLoginSession(
        pUserID: String,
        pUserPW: String,
        pUserName: String,
        pGrantType: String,
        pAccessToken: String,
        pRefreshToken: String
    ) {
        DLog.d(TAG, "pUserName=$pUserName")
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(USER_ID, pUserID)
        editor.putString(USER_PW, pUserPW)
        editor.putString(USER_NAME, pUserName)
        editor.putString(GRANT_TYPE, pGrantType)
        editor.putString(ACCESS_TOKEN, pAccessToken)
        editor.putString(REFRESH_TOKEN, pRefreshToken)
        editor.commit()
    }

    /**
     * AccessToken이 만료되었을 때, 새로운 Token을 받아온 후에 저장할 때 사용하는 함수
     *
     * @param pGrantType        ?
     * @param pAccessToken      AWS Access Token
     * @param pRefreshToken     AWS Refresh Token
     */
    fun updateLoginSession(
        pGrantType: String,
        pAccessToken: String,
        pRefreshToken: String
    ) {
        editor.putString(GRANT_TYPE, pGrantType)
        editor.putString(ACCESS_TOKEN, pAccessToken)
        editor.putString(REFRESH_TOKEN, pRefreshToken)
        editor.commit()
    }

    /**
     * 로그아웃 시, 호출하는 함수
     */
    fun logout() {
        editor.clear()
        editor.commit()
        _KRW = 0.0
        _walletList.clear()
    }

    /**
     * 로그인 후, 보유 자산 데이터를 서버로부터 가져온 후에 호출하는 함수
     *
     * @param pKRW          원화 금액
     * @param pWalletList   보유 코인 리스트
     */
    fun setWalletOverall(pKRW: Double, pWalletList: List<WalletData>) {
        _KRW = pKRW
        _walletList.clear()
        _walletList.addAll(pWalletList)
    }

    fun depositKRW(pDepositPrice: Double) {
        _KRW += pDepositPrice
    }

    fun withdrawalKRW(pWithdrawalPrice: Double) {
        _KRW -= pWithdrawalPrice
    }

    /**
     * 로그인 했는지 여부를 반환하는 함수
     */
    fun isLogin(): Boolean = sharedPreferences.getBoolean(IS_LOGIN, false)

    val userID: String get() = sharedPreferences.getString(USER_ID, "") ?: ""

    val userPW: String get() = sharedPreferences.getString(USER_PW, "") ?: ""

    val userName: String get() = sharedPreferences.getString(USER_NAME, "") ?: ""

    val grantType: String get() = sharedPreferences.getString(GRANT_TYPE, "") ?: ""

    val accessToken: String get() = sharedPreferences.getString(ACCESS_TOKEN, "") ?: ""

    val refreshToken: String get() = sharedPreferences.getString(REFRESH_TOKEN, "") ?: ""

    private const val TAG: String = "KubitSession"
    private const val LOGIN_PREF_NAME: String = "kubit"

    private const val IS_LOGIN: String = "is_login"
    private const val USER_ID: String = "user_id"
    private const val USER_PW: String = "user_pw"
    private const val USER_NAME: String = "user_name"
    private const val GRANT_TYPE: String = "grant_type"
    private const val ACCESS_TOKEN: String = "access_token"
    private const val REFRESH_TOKEN: String = "refresh_token"

}