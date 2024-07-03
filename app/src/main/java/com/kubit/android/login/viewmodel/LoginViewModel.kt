package com.kubit.android.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kubit.android.base.BaseViewModel
import com.kubit.android.common.session.KubitSession
import com.kubit.android.common.util.DLog
import com.kubit.android.model.data.login.LoginResult
import com.kubit.android.model.data.login.LoginSessionData
import com.kubit.android.model.data.network.NetworkResult
import com.kubit.android.model.repository.KubitRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val kubitRepository: KubitRepository
) : BaseViewModel() {

    private val _loginResult: MutableLiveData<LoginResult?> = MutableLiveData(null)
    val loginResult: LiveData<LoginResult?> get() = _loginResult

    private var _userID: String = ""
    private val userID: String get() = _userID
    private var _userPW: String = ""
    private val userPW: String get() = _userPW
    val enableLogin: Boolean
        get() = userID.isNotEmpty() && userPW.isNotEmpty()

    fun setUserID(pUserID: String) {
        _userID = pUserID
    }

    fun setUserPW(pUserPW: String) {
        _userPW = pUserPW
    }

    fun requestLogin() {
        setProgressFlag(true)
        viewModelScope.launch {
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

    companion object {
        private const val TAG: String = "LoginViewModel"
    }

}