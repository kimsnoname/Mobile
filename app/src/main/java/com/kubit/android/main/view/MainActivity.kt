package com.kubit.android.main.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kubit.android.R
import com.kubit.android.base.BaseActivity
import com.kubit.android.base.BaseViewModel
import com.kubit.android.coinlist.view.CoinListFragment
import com.kubit.android.common.session.KubitSession
import com.kubit.android.common.util.DLog
import com.kubit.android.databinding.ActivityMainBinding
import com.kubit.android.exchange.view.ExchangeFragment
import com.kubit.android.investment.view.InvestmentFragment
import com.kubit.android.login.view.LoginActivity
import com.kubit.android.main.viewmodel.MainViewModel
import com.kubit.android.model.data.market.KubitMarketData
import com.kubit.android.model.data.route.KubitTabRouter
import com.kubit.android.profile.view.ProfileFragment
import com.kubit.android.transaction.view.TransactionActivity

class MainActivity : BaseActivity() {

    private val model: MainViewModel by lazy {
        ViewModelProvider(
            this,
            BaseViewModel.Factory(application)
        )[MainViewModel::class.java]
    }
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // region Activity LifeCycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val marketData = if (savedInstanceState != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                savedInstanceState.getSerializable("market_data", KubitMarketData::class.java)
            } else {
                savedInstanceState.getSerializable("market_data")
            } as KubitMarketData
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("market_data", KubitMarketData::class.java)
            } else {
                intent.getSerializableExtra("market_data")
            } as KubitMarketData
        }
        model.initMarketData(marketData)

        setObserver()
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        CoinListFragment.clearInstance()
        InvestmentFragment.clearInstance()
        ExchangeFragment.clearInstance()
        ProfileFragment.clearInstance()
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

        model.tabRouter.observe(this, Observer { router ->
            DLog.d(TAG, "tabRouter=$router")
            when (router) {
                KubitTabRouter.COIN_LIST -> {
                    setFragment(
                        R.id.fl_main,
                        CoinListFragment.getInstance(),
                        CoinListFragment.TAG
                    )
                }

                KubitTabRouter.INVESTMENT -> {
                    setFragment(
                        R.id.fl_main,
                        InvestmentFragment.getInstance(),
                        InvestmentFragment.TAG
                    )
                }

                KubitTabRouter.EXCHANGE -> {
                    setFragment(
                        R.id.fl_main,
                        ExchangeFragment.getInstance(),
                        ExchangeFragment.TAG
                    )
                }

                KubitTabRouter.PROFILE -> {
                    setFragment(
                        R.id.fl_main,
                        ProfileFragment.getInstance(),
                        ProfileFragment.TAG
                    )
                }

                else -> {
                    DLog.e(TAG, "Unrecognized TabRouter=$router")
                }
            }
        })

        model.selectedCoinData.observe(this, Observer { selectedCoinData ->
            DLog.d(TAG, "selectedCoinData=$selectedCoinData")
            if (selectedCoinData != null) {
                // 로그인한 경우 -> 거래 화면으로 전환
                if (KubitSession.isLogin()) {
                    val transactionIntent = Intent(this, TransactionActivity::class.java).apply {
                        putExtra("selected_coin_data", selectedCoinData)
                    }
                    transactionIntentForResult.launch(transactionIntent)
                }
                // 로그인하지 않은 경우 -> 로그인 화면으로 전환
                else {
                    launchLoginActivity()
                }
            }
        })
    }

    private fun init() {
        binding.apply {
            navMain.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_home -> {
                        model.setTabRouter(KubitTabRouter.COIN_LIST)
                        true
                    }

                    R.id.menu_investment -> {
                        if (KubitSession.isLogin()) {
                            model.setTabRouter(KubitTabRouter.INVESTMENT)
                            true
                        } else {
                            launchLoginActivity()
                            false
                        }
                    }

                    R.id.menu_exchange -> {
                        if (KubitSession.isLogin()) {
                            model.setTabRouter(KubitTabRouter.EXCHANGE)
                            true
                        } else {
                            launchLoginActivity()
                            false
                        }
                    }

                    R.id.menu_profile -> {
                        model.setTabRouter(KubitTabRouter.PROFILE)
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
        }
    }

    private fun launchLoginActivity() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntentForResult.launch(loginIntent)
    }

    override fun onBackPressed() {
        val tempTime: Long = System.currentTimeMillis()
        val intervalTime = tempTime - backPressedTime

        if (intervalTime in 0..FINISH_INTERVAL_TIME) {
            super.onBackPressed()
        } else {
            backPressedTime = tempTime
            val msg: String = "종료하시려면 뒤로 버튼을 한 번 더 눌러주세요."
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private val transactionIntentForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            model.setSelectedCoinData(null)

            when (result.resultCode) {
                RESULT_OK -> {
                    DLog.d("${TAG}_transactionIntentForResult", "Successful Transaction!")
                    model.requestClearInvestmentData()
                }

                else -> {
                    DLog.d("${TAG}_transactionIntentForResult", "No Transaction!")
                }
            }
        }

    private val loginIntentForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    model.requestWalletOverall()
                }

                else -> {

                }
            }
        }

    companion object {
        private const val TAG: String = "MainActivity"

        private const val FINISH_INTERVAL_TIME: Long = 2000
        private var backPressedTime: Long = 0
    }

}