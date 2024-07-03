package com.kubit.android.intro.view

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kubit.android.R
import com.kubit.android.base.BaseActivity
import com.kubit.android.base.BaseViewModel
import com.kubit.android.common.dialog.MessageDialog
import com.kubit.android.common.session.KubitSession
import com.kubit.android.common.util.DLog
import com.kubit.android.common.util.NetworkUtil
import com.kubit.android.databinding.ActivityIntroBinding
import com.kubit.android.intro.viewmodel.IntroViewModel
import com.kubit.android.main.view.MainActivity
import com.kubit.android.model.data.login.LoginResult

class IntroActivity : BaseActivity() {

    private val model: IntroViewModel by lazy {
        ViewModelProvider(
            this,
            BaseViewModel.Factory(application)
        )[IntroViewModel::class.java]
    }
    private val binding: ActivityIntroBinding by lazy {
        ActivityIntroBinding.inflate(layoutInflater)
    }

    // region Activity LifeCycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setObserver()
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissProgress()
    }
    // endregion Activity LifeCycle

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

        model.marketData.observe(this, Observer { marketData ->
            if (marketData != null) {
                if (KubitSession.isLogin()) {
                    model.requestLogin()
                } else {
                    finish(pChangeToMainActivity = true)
                }
            }
        })

        model.loginResult.observe(this, Observer { loginResult ->
            if (loginResult != null) {
                when (loginResult) {
                    LoginResult.SUCCESS -> {
                        model.requestWalletOverall()
                    }

                    LoginResult.FAIL,
                    LoginResult.ERROR -> {
                        model.setProgressFlag(false)
                        KubitSession.logout()
                        finish(pChangeToMainActivity = true)
                    }
                }
            }
        })

        model.walletRequestResult.observe(this, Observer { result ->
            if (result != null) {
                if (result) {
                    finish(pChangeToMainActivity = true)
                } else {
                    showWalletOverallFailDialog()
                }
            }
        })
    }

    private fun init() {
        if (NetworkUtil.checkNetworkEnable(this)) {
            model.requestMarketCode()
        } else {
            showNetworkDialog()
        }
    }

    private fun finish(pChangeToMainActivity: Boolean) {
        if (pChangeToMainActivity) {
            model.marketData.value?.let { marketData ->
                val mainIntent = Intent(this, MainActivity::class.java).apply {
                    putExtra("market_data", marketData)
                }
                startActivity(mainIntent)
            }
        }
        super.finish()
    }

    // region Dialog
    private fun showNetworkDialog() {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment is MessageDialog) {
                return
            }
        }

        MessageDialog(resources.getString(R.string.dialog_msg_002)) {
            finish(pChangeToMainActivity = false)
        }.show(supportFragmentManager, MessageDialog.TAG)
    }

    private fun showWalletOverallFailDialog() {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment is MessageDialog) {
                return
            }
        }

        MessageDialog(getString(R.string.toast_msg_error_001)) {
            finish(pChangeToMainActivity = false)
        }.show(supportFragmentManager, MessageDialog.TAG)
    }
    // endregion Dialog

    companion object {
        private const val TAG: String = "IntroActivity"
    }

}