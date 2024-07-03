package com.kubit.android.common.util

import android.util.Log
import com.kubit.android.BuildConfig

/**
 * Debug 모드로 빌드된 앱에서만 로그가 찍히도록 하기 위한 오브젝트
 *
 * @see Log
 */
object DLog {

    fun d(pTag: String, pMessage: String?) {
        if (BuildConfig.DEBUG && !pMessage.isNullOrEmpty()) {
            Log.d(pTag, pMessage)
        }
    }

    fun d(pTag: String, pMessage: String?, pException: Exception) {
        if (BuildConfig.DEBUG && !pMessage.isNullOrEmpty()) {
            Log.d(pTag, pMessage, pException)
        }
    }

    fun w(pTag: String, pMessage: String?) {
        if (BuildConfig.DEBUG && !pMessage.isNullOrEmpty()) {
            Log.w(pTag, pMessage)
        }
    }

    fun w(pTag: String, pMessage: String?, pException: Exception) {
        if (BuildConfig.DEBUG && !pMessage.isNullOrEmpty()) {
            Log.w(pTag, pMessage, pException)
        }
    }

    fun e(pTag: String, pMessage: String?) {
        if (BuildConfig.DEBUG && !pMessage.isNullOrEmpty()) {
            Log.e(pTag, pMessage)
        }
    }

    fun e(pTag: String, pMessage: String?, pException: Exception) {
        if (BuildConfig.DEBUG && !pMessage.isNullOrEmpty()) {
            Log.e(pTag, pMessage, pException)
        }
    }

    fun i(pTag: String, pMessage: String?) {
        if (BuildConfig.DEBUG && !pMessage.isNullOrEmpty()) {
            Log.i(pTag, pMessage)
        }
    }

    fun i(pTag: String, pMessage: String?, pException: Exception) {
        if (BuildConfig.DEBUG && !pMessage.isNullOrEmpty()) {
            Log.i(pTag, pMessage, pException)
        }
    }

}