package com.kubit.android.base

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kubit.android.intro.viewmodel.IntroViewModel
import com.kubit.android.login.viewmodel.LoginViewModel
import com.kubit.android.main.viewmodel.MainViewModel
import com.kubit.android.model.repository.IntroRepository
import com.kubit.android.model.repository.KubitRepository
import com.kubit.android.model.repository.TransactionRepository
import com.kubit.android.model.repository.UpbitRepository
import com.kubit.android.transaction.viewmodel.TransactionViewModel

open class BaseViewModel : ViewModel() {

    /**
     * ProgressBar on/off flag
     */
    private val _progressFlag: MutableLiveData<Boolean> = MutableLiveData(false)
    val progressFlag: LiveData<Boolean> get() = _progressFlag

    /**
     * Exception Data
     *
     * Exception이 발생한 경우, 일시적인 오류임을 알리는 Toast 메시지를 출력함
     */
    private val _exceptionData: MutableLiveData<Exception> = MutableLiveData()
    val exceptionData: LiveData<Exception> get() = _exceptionData

    /**
     * API Fail Message
     *
     * API Request 실패 시, Fail Message를 Toast 메시지로 출력해야 함
     */
    private val _apiFailMsg: MutableLiveData<String> = MutableLiveData()
    val apiFailMsg: LiveData<String> get() = _apiFailMsg

    fun setProgressFlag(pProgressFlag: Boolean) {
        if (progressFlag.value != pProgressFlag)
            _progressFlag.value = pProgressFlag
    }

    fun setExceptionData(pException: Exception) {
        _exceptionData.postValue(pException)
    }

    fun setApiFailMsg(pMsg: String) {
        _apiFailMsg.postValue(pMsg)
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // Intro Activity ViewModel
            if (modelClass.isAssignableFrom(IntroViewModel::class.java)) {
                return IntroViewModel(
                    IntroRepository(application),
                    KubitRepository(application)
                ) as T
            }
            // Main Activity ViewModel
            else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(
                    UpbitRepository(application),
                    KubitRepository(application)
                ) as T
            }
            // Transaction Activity ViewModel
            else if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
                return TransactionViewModel(
                    TransactionRepository(application),
                    KubitRepository(application)
                ) as T
            }
            // Login Activity ViewModel
            else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(
                    KubitRepository(application)
                ) as T
            }
            // 식별되지 않은 ViewModel
            else {
                throw IllegalArgumentException("Unknown ViewModel Class!")
            }
        }
    }


    companion object {
        private const val TAG: String = "BaseViewModel"
    }
}
