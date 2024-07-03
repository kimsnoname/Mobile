package com.kubit.android.model.data.network

sealed class KubitNetworkResult<out T> {
    /**
     * 네트워크 통신에 성공했을 때 사용하는 데이터 클래스
     */
    data class Success<out T>(val data: T) : KubitNetworkResult<T>()

    /**
     * Token Refresh가 필요할 때 사용하는 데이터 클래스
     */
    data class Refresh(val refreshToken: String) : KubitNetworkResult<Nothing>()

    /**
     * 네트워크 통신에 실패했을 때 사용하는 데이터 클래스
     */
    data class Fail(val failMsg: String) : KubitNetworkResult<Nothing>()

    /**
     * 네트워크 통신 과정에서 에러가 발생했을 때 사용하는 데이터 클래스
     */
    data class Error(val exception: Exception) : KubitNetworkResult<Nothing>()
}