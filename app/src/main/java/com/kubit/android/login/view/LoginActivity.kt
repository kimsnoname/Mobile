package com.kubit.android.login.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kubit.android.R
import com.kubit.android.base.BaseActivity
import com.kubit.android.base.BaseViewModel
import com.kubit.android.databinding.ActivityLoginBinding
import com.kubit.android.login.viewmodel.LoginViewModel
import com.kubit.android.model.data.login.LoginResult

class LoginActivity : BaseActivity() {

    private val model: LoginViewModel by lazy {
        ViewModelProvider(
            this,
            BaseViewModel.Factory(application)
        )[LoginViewModel::class.java]
    }
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setObserver()
        init()
    }

    private fun setObserver() {
        model.progressFlag.observe(this, Observer { progressFlag ->
            if (progressFlag) {
                showProgress()
            } else {
                dismissProgress()
            }
        })

        model.apiFailMsg.observe(this, Observer { failMsg ->
            if (failMsg.isNotEmpty()) {
                model.setProgressFlag(false)
                showToastMsg(failMsg)
            }
        })

        model.exceptionData.observe(this, Observer { exception ->
            model.setProgressFlag(false)
            showErrorMsg()
        })

        model.loginResult.observe(this, Observer { loginResult ->
            if (loginResult != null) {
                when (loginResult) {
                    LoginResult.SUCCESS -> {
                        model.setProgressFlag(false)
                        showToastMsg(getString(R.string.toast_msg_login_success))

                        val resultIntent = Intent()
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }

                    LoginResult.FAIL -> {
                        model.setProgressFlag(false)
                        showToastMsg(getString(R.string.toast_msg_login_fail))
                    }

                    LoginResult.ERROR -> {
                        model.setProgressFlag(false)
                        showErrorMsg()
                    }
                }
            }
        })
    }

    private fun init() {
        binding.apply {
            etLoginId.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s != null) {
                        val input = s.toString()
                        model.setUserID(input)
                        binding.btnLoginConfirm.isEnabled = model.enableLogin
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
            etLoginId.setOnEditorActionListener { view, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_NEXT -> {
                        etLoginPw.focus()
                        true
                    }

                    else -> {
                        false
                    }
                }
            }

            etLoginPw.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s != null) {
                        val input = s.toString()
                        model.setUserPW(input)
                        binding.btnLoginConfirm.isEnabled = model.enableLogin
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
            etLoginPw.setOnEditorActionListener { view, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        etLoginPw.clearFocus(isKeyboardHide = true)
                        requestLogin()
                        true
                    }

                    else -> {
                        false
                    }
                }
            }

            btnLoginConfirm.setOnClickListener {
                requestLogin()
            }
        }
    }

    private fun requestLogin() {
        if (model.enableLogin) {
            model.requestLogin()
        } else {
            showToastMsg(getString(R.string.login_checkInputMsg))
        }
    }

    companion object {
        private const val TAG: String = "LoginActivity"
    }

}