package com.kubit.android.model.repository

import android.app.Application
import com.kubit.android.R
import com.kubit.android.base.BaseNetworkRepository
import com.kubit.android.common.util.JsonParserUtil
import com.kubit.android.model.data.exchange.ExchangeRecordData
import com.kubit.android.model.data.investment.InvestmentNotYetData
import com.kubit.android.model.data.investment.InvestmentRecordData
import com.kubit.android.model.data.investment.NotYetData
import com.kubit.android.model.data.investment.RecordData
import com.kubit.android.model.data.login.LoginSessionData
import com.kubit.android.model.data.network.KubitNetworkResult
import com.kubit.android.model.data.network.NetworkResult
import com.kubit.android.model.data.wallet.WalletOverall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class KubitRepository(
    private val application: Application
) : BaseNetworkRepository(application, TAG) {

    private val jsonParserUtil: JsonParserUtil = JsonParserUtil()

    suspend fun makeLoginRequest(
        pUserID: String,
        pUserPW: String
    ): NetworkResult<LoginSessionData> {
        return withContext(Dispatchers.IO) {
            val hsParams = HashMap<String, Any>().apply {
                put("userId", pUserID)
                put("password", pUserPW)
            }
            val message = sendRequestToKubitServer(KUBIT_API_LOGIN_URL, hsParams, POST)

            if (message.isNotEmpty()) {
                try {
                    val jsonRoot = JSONObject(message)
                    jsonParserUtil.getLoginSessionData(jsonRoot)
                } catch (e: JSONException) {
                    NetworkResult.Error(e)
                }
            } else {
                NetworkResult.Fail(
                    application.getString(
                        R.string.api_connection_fail_msg
                    )
                )
            }
        }
    }

    suspend fun makeRefreshTokenRequest(
        pRefreshToken: String
    ): NetworkResult<LoginSessionData> {
        return withContext(Dispatchers.IO) {
            val hsParams = HashMap<String, Any>().apply {
                put("refreshToken", pRefreshToken)
            }
            val message = sendRequestToKubitServer(KUBIT_API_REFRESH_TOKEN_URL, hsParams, POST)

            if (message.isNotEmpty()) {
                try {
                    val jsonRoot = JSONObject(message)
                    jsonParserUtil.getLoginSessionData(jsonRoot)
                } catch (e: JSONException) {
                    NetworkResult.Error(e)
                }
            } else {
                NetworkResult.Fail(
                    application.getString(
                        R.string.api_connection_fail_msg
                    )
                )
            }
        }
    }

    suspend fun makeWalletOverallRequest(
        pGrantType: String,
        pAccessToken: String
    ): KubitNetworkResult<WalletOverall> {
        return withContext(Dispatchers.IO) {
            val message = sendRequestToKubitServer(
                KUBIT_API_WALLET_OVERALL_URL,
                hashMapOf(),
                GET,
                "$pGrantType $pAccessToken"
            )

            if (message.isNotEmpty()) {
                try {
                    val jsonRoot = JSONObject(message)
                    jsonParserUtil.getWalletOverallData(jsonRoot)
                } catch (e: JSONException) {
                    KubitNetworkResult.Error(e)
                }
            } else {
                KubitNetworkResult.Fail(
                    application.getString(
                        R.string.api_connection_fail_msg
                    )
                )
            }
        }
    }

    /**
     * 보유자산 > 거래내역 데이터를 요청하는 API를 호출하는 함수
     *
     * @param pGrantType    ?
     * @param pAccessToken  엑세스 토큰
     */
    suspend fun makeTransactionCompletesRequest(
        pGrantType: String,
        pAccessToken: String
    ): KubitNetworkResult<InvestmentRecordData> {
        return withContext(Dispatchers.IO) {
            val message = sendRequestToKubitServer(
                KUBIT_API_TRANSACTION_COMPLETES_URL,
                hashMapOf(),
                GET,
                "$pGrantType $pAccessToken"
            )

            if (message.isNotEmpty()) {
                try {
                    val jsonRoot = JSONObject(message)
                    jsonParserUtil.getTransactionCompletesResponse(jsonRoot)
                } catch (e: JSONException) {
                    KubitNetworkResult.Error(e)
                }
            } else {
                KubitNetworkResult.Fail(
                    application.getString(
                        R.string.api_connection_fail_msg
                    )
                )
            }
        }
    }

    /**
     * 투자내역 > 미체결내역 데이터를 요청하는 API를 호출하는 함수
     *
     * @param pGrantType    ?
     * @param pAccessToken  엑세스 토큰
     */
    suspend fun makeTransactionWaitRequest(
        pGrantType: String,
        pAccessToken: String
    ): KubitNetworkResult<InvestmentNotYetData> {
        return withContext(Dispatchers.IO) {
            val message = sendRequestToKubitServer(
                KUBIT_API_TRANSACTION_WAIT_URL,
                hashMapOf(),
                GET,
                "$pGrantType $pAccessToken"
            )

            if (message.isNotEmpty()) {
                try {
                    val jsonRoot = JSONObject(message)
                    jsonParserUtil.getTransactionWaitResponse(jsonRoot)
                } catch (e: JSONException) {
                    KubitNetworkResult.Error(e)
                }
            } else {
                KubitNetworkResult.Fail(
                    application.getString(
                        R.string.api_connection_fail_msg
                    )
                )
            }
        }
    }

    suspend fun makeRemoveTransactionWaitRequest(
        pNotYetList: List<NotYetData>,
        pGrantType: String,
        pAccessToken: String
    ): KubitNetworkResult<Triple<WalletOverall, InvestmentRecordData, InvestmentNotYetData>> {
        return withContext(Dispatchers.IO) {
            val transactionIdList = JSONArray()
            for (notYet in pNotYetList) {
                transactionIdList.put(notYet.transactionID)
            }
            val hsParams = HashMap<String, Any>().apply {
                put("transactionIdList", transactionIdList)
            }
            val message = sendRequestToKubitServer(
                KUBIT_API_TRANSACTION_WAIT_URL,
                hsParams,
                PUT,
                "$pGrantType $pAccessToken"
            )

            if (message.isNotEmpty()) {
                try {
                    val jsonRoot = JSONObject(message)
                    jsonParserUtil.getRemoveTransactionWaitResponse(jsonRoot)
                } catch (e: JSONException) {
                    KubitNetworkResult.Error(e)
                }
            } else {
                KubitNetworkResult.Fail(
                    application.getString(
                        R.string.api_connection_fail_msg
                    )
                )
            }
        }
    }

    /**
     * 입출금 내역 데이터를 요청하는 API를 호출하는 함수
     *
     * @param pGrantType    ?
     * @param pAccessToken  엑세스 토큰
     */
    suspend fun makeBankRequest(
        pGrantType: String,
        pAccessToken: String
    ): KubitNetworkResult<List<ExchangeRecordData>> {
        return withContext(Dispatchers.IO) {
            val message = sendRequestToKubitServer(
                KUBIT_API_USER_BANK,
                hashMapOf(),
                GET,
                "$pGrantType $pAccessToken"
            )

            if (message.isNotEmpty()) {
                try {
                    val jsonRoot = JSONObject(message)
                    jsonParserUtil.getBankResponse(jsonRoot)
                } catch (e: JSONException) {
                    KubitNetworkResult.Error(e)
                }
            } else {
                KubitNetworkResult.Fail(
                    application.getString(
                        R.string.api_connection_fail_msg
                    )
                )
            }
        }
    }

    /**
     * 입금을 요청하는 API를 호출하는 함수
     *
     * @param pDepositPrice     입금 금액
     * @param pGrantType        ?
     * @param pAccessToken      엑세스 토큰
     */
    suspend fun makeDepositRequest(
        pDepositPrice: Double,
        pGrantType: String,
        pAccessToken: String
    ): KubitNetworkResult<ExchangeRecordData> {
        return withContext(Dispatchers.IO) {
            val hsParams = HashMap<String, Any>().apply {
                put("requestType", "DEPOSIT")
                put("money", pDepositPrice.toInt())
            }
            val message = sendRequestToKubitServer(
                KUBIT_API_USER_BANK,
                hsParams,
                PUT,
                "$pGrantType $pAccessToken"
            )

            if (message.isNotEmpty()) {
                try {
                    val jsonRoot = JSONObject(message)
                    jsonParserUtil.getExchangeRecordData(jsonRoot)
                } catch (e: JSONException) {
                    KubitNetworkResult.Error(e)
                }
            } else {
                KubitNetworkResult.Fail(
                    application.getString(
                        R.string.api_connection_fail_msg
                    )
                )
            }
        }
    }

    /**
     * 출금을 요청하는 API를 호출하는 함수
     *
     * @param pWithdrawalPrice  출금 금액
     * @param pGrantType        ?
     * @param pAccessToken      엑세스 토큰
     */
    suspend fun makeWithdrawalRequest(
        pWithdrawalPrice: Double,
        pGrantType: String,
        pAccessToken: String
    ): KubitNetworkResult<ExchangeRecordData> {
        return withContext(Dispatchers.IO) {
            val hsParams = HashMap<String, Any>().apply {
                put("requestType", "WITHDRAW")
                put("money", pWithdrawalPrice.toInt())
            }
            val message = sendRequestToKubitServer(
                KUBIT_API_USER_BANK,
                hsParams,
                PUT,
                "$pGrantType $pAccessToken"
            )

            if (message.isNotEmpty()) {
                try {
                    val jsonRoot = JSONObject(message)
                    jsonParserUtil.getExchangeRecordData(jsonRoot)
                } catch (e: JSONException) {
                    KubitNetworkResult.Error(e)
                }
            } else {
                KubitNetworkResult.Fail(
                    application.getString(
                        R.string.api_connection_fail_msg
                    )
                )
            }
        }
    }

    /**
     * 사용자 정보 초기화를 요청하는 함수
     */
    suspend fun makeResetRequest(
        pGrantType: String,
        pAccessToken: String
    ): KubitNetworkResult<WalletOverall> {
        return withContext(Dispatchers.IO) {
            val message = sendRequestToKubitServer(
                KUBIT_API_USER_RESET,
                hashMapOf(),
                DELETE,
                "$pGrantType $pAccessToken"
            )

            if (message.isNotEmpty()) {
                try {
                    val jsonRoot = JSONObject(message)
                    jsonParserUtil.getWalletOverallFromResetRequest(jsonRoot)
                } catch (e: JSONException) {
                    KubitNetworkResult.Error(e)
                }
            } else {
                KubitNetworkResult.Fail(
                    application.getString(
                        R.string.api_connection_fail_msg
                    )
                )
            }
        }
    }

    /**
     * 지정가 거래를 요청하는 함수
     *
     * @param pTransactionType  BID or ASK
     * @param pMarketCode       마켓 코드
     * @param pRequestPrice     지정가, 1코인당 가격
     * @param pQuantity         주문 수량
     * @param pGrantType        ?
     * @param pAccessToken      엑세스 토큰
     */
    suspend fun makeDesignatedRequest(
        pTransactionType: String,
        pMarketCode: String,
        pRequestPrice: Double,
        pQuantity: Double,
        pGrantType: String,
        pAccessToken: String
    ): KubitNetworkResult<WalletOverall> {
        return withContext(Dispatchers.IO) {
            val hsParams = HashMap<String, Any>().apply {
                put("transactionType", pTransactionType)
                put("marketCode", pMarketCode)
                put("requestPrice", pRequestPrice)
                put("quantity", pQuantity)
            }
            val message = sendRequestToKubitServer(
                KUBIT_API_TRANSACTION_FIXED_URL,
                hsParams,
                POST,
                authorization = "$pGrantType $pAccessToken"
            )

            if (message.isNotEmpty()) {
                try {
                    val jsonRoot = JSONObject(message)
                    jsonParserUtil.getTransactionResponse(jsonRoot)
                } catch (e: JSONException) {
                    KubitNetworkResult.Error(e)
                }
            } else {
                KubitNetworkResult.Fail(
                    application.getString(
                        R.string.api_connection_fail_msg
                    )
                )
            }
        }
    }

    /**
     * 시장가 매수 거래를 요청하는 함수
     *
     * @param pMarketCode       마켓 코드
     * @param pCurrentPrice     시장가 거래를 요청할 당시의 가격
     * @param pTotalPrice       주문 총액
     * @param pGrantType        ?
     * @param pAccessToken      엑세스 토큰
     */
    suspend fun makeMarketBidRequest(
        pMarketCode: String,
        pCurrentPrice: Double,
        pTotalPrice: Double,
        pGrantType: String,
        pAccessToken: String
    ): KubitNetworkResult<WalletOverall> {
        return withContext(Dispatchers.IO) {
            val hsParams = HashMap<String, Any>().apply {
                put("marketCode", pMarketCode)
                put("currentPrice", pCurrentPrice)
                put("totalPrice", pTotalPrice)
            }
            val message = sendRequestToKubitServer(
                KUBIT_API_TRANSACTION_MARKET_BID_URL,
                hsParams,
                POST,
                "$pGrantType $pAccessToken"
            )

            if (message.isNotEmpty()) {
                try {
                    val jsonRoot = JSONObject(message)
                    jsonParserUtil.getTransactionResponse(jsonRoot)
                } catch (e: JSONException) {
                    KubitNetworkResult.Error(e)
                }
            } else {
                KubitNetworkResult.Fail(
                    application.getString(
                        R.string.api_connection_fail_msg
                    )
                )
            }
        }
    }

    /**
     * 시장가 매도 거래를 요청하는 함수
     *
     * @param pMarketCode       마켓 코드
     * @param pCurrentPrice     시장가 거래를 요청할 당시의 가격
     * @param pQuantity         매도 수량
     * @param pGrantType        ?
     * @param pAccessToken      엑세스 토큰
     */
    suspend fun makeMarketAskRequest(
        pMarketCode: String,
        pCurrentPrice: Double,
        pQuantity: Double,
        pGrantType: String,
        pAccessToken: String
    ): KubitNetworkResult<WalletOverall> {
        return withContext(Dispatchers.IO) {
            val hsParams = HashMap<String, Any>().apply {
                put("marketCode", pMarketCode)
                put("currentPrice", pCurrentPrice)
                put("quantity", pQuantity)
            }
            val message = sendRequestToKubitServer(
                KUBIT_API_TRANSACTION_MARKET_ASK_URL,
                hsParams,
                POST,
                "$pGrantType $pAccessToken"
            )

            if (message.isNotEmpty()) {
                try {
                    val jsonRoot = JSONObject(message)
                    jsonParserUtil.getTransactionResponse(jsonRoot)
                } catch (e: JSONException) {
                    KubitNetworkResult.Error(e)
                }
            } else {
                KubitNetworkResult.Fail(
                    application.getString(
                        R.string.api_connection_fail_msg
                    )
                )
            }
        }
    }

    companion object {
        private const val TAG: String = "KubitRepository"

        private const val KUBIT_API_LOGIN_URL: String = "${KUBIT_API_HOST_URL}user/login/"
        private const val KUBIT_API_REFRESH_TOKEN_URL: String = "${KUBIT_API_HOST_URL}user/refresh/"
        private const val KUBIT_API_WALLET_OVERALL_URL: String =
            "${KUBIT_API_HOST_URL}user/wallet_overall/"

        private const val KUBIT_API_TRANSACTION_COMPLETES_URL: String =
            "${KUBIT_API_HOST_URL}transaction/completes/"
        private const val KUBIT_API_TRANSACTION_WAIT_URL: String =
            "${KUBIT_API_HOST_URL}transaction/requests/"
        private const val KUBIT_API_TRANSACTION_FIXED_URL: String =
            "${KUBIT_API_HOST_URL}transaction/fixed/"
        private const val KUBIT_API_TRANSACTION_MARKET_BID_URL: String =
            "${KUBIT_API_HOST_URL}transaction/market/bid/"
        private const val KUBIT_API_TRANSACTION_MARKET_ASK_URL: String =
            "${KUBIT_API_HOST_URL}transaction/market/ask/"

        private const val KUBIT_API_USER_BANK: String = "${KUBIT_API_HOST_URL}bank/"
        private const val KUBIT_API_USER_RESET: String = "${KUBIT_API_HOST_URL}user/reset/"
    }

}