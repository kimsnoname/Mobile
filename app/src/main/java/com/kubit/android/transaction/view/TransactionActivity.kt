package com.kubit.android.transaction.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kubit.android.R
import com.kubit.android.base.BaseActivity
import com.kubit.android.base.BaseViewModel
import com.kubit.android.chart.view.ChartFragment
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.common.util.DLog
import com.kubit.android.databinding.ActivityTransactionBinding
import com.kubit.android.model.data.coin.KubitCoinInfoData
import com.kubit.android.model.data.coin.PriceChangeType
import com.kubit.android.model.data.route.TransactionTabRouter
import com.kubit.android.order.view.OrderBookFragment
import com.kubit.android.transaction.viewmodel.TransactionViewModel

class TransactionActivity : BaseActivity() {

    private val model: TransactionViewModel by lazy {
        ViewModelProvider(
            this,
            BaseViewModel.Factory(application)
        )[TransactionViewModel::class.java]
    }
    private val binding: ActivityTransactionBinding by lazy {
        ActivityTransactionBinding.inflate(layoutInflater)
    }

    private val coinRedColor: Int by lazy {
        ContextCompat.getColor(this, R.color.coin_red)
    }
    private val coinBlueColor: Int by lazy {
        ContextCompat.getColor(this, R.color.coin_blue)
    }
    private val textColor: Int by lazy {
        ContextCompat.getColor(this, R.color.text)
    }
    private val whiteColor: Int by lazy {
        ContextCompat.getColor(this, R.color.white)
    }
    private val grayColor: Int by lazy {
        ContextCompat.getColor(this, R.color.gray)
    }

    // region Activity LifeCycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val selectedCoinData: KubitCoinInfoData
        // 백그라운드 강제 종료된 경우
        if (savedInstanceState != null) {
            selectedCoinData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                savedInstanceState.getSerializable(
                    "selected_coin_data",
                    KubitCoinInfoData::class.java
                )
            } else {
                savedInstanceState.getSerializable("selected_coin_data")
            } as KubitCoinInfoData
        }
        // 그 외의 일반적인 경우
        else {
            selectedCoinData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra(
                    "selected_coin_data",
                    KubitCoinInfoData::class.java
                )
            } else {
                intent.getSerializableExtra("selected_coin_data")
            } as KubitCoinInfoData
        }

        setObserver()
        init(selectedCoinData)
    }

    override fun onResume() {
        super.onResume()
        model.requestTickerData()
    }

    override fun onStop() {
        super.onStop()
        model.stopTickerData()
    }

    override fun onDestroy() {
        super.onDestroy()
        OrderBookFragment.clearInstance()
        ChartFragment.clearInstance()
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
                TransactionTabRouter.ORDER_BOOK -> {
                    binding.tvTransactionTabOrder.setTextColor(whiteColor)
                    binding.tvTransactionTabChart.setTextColor(grayColor)
                    setFragment(
                        R.id.fl_transaction,
                        OrderBookFragment.getInstance(),
                        OrderBookFragment.TAG
                    )
                }

                TransactionTabRouter.CHART -> {
                    binding.tvTransactionTabOrder.setTextColor(grayColor)
                    binding.tvTransactionTabChart.setTextColor(whiteColor)
                    setFragment(
                        R.id.fl_transaction,
                        ChartFragment.getInstance(),
                        ChartFragment.TAG
                    )
                }

                else -> {
                    DLog.e(TAG, "Unrecognized TabRouter=$router")
                }
            }
        })

        model.coinSnapshotData.observe(this, Observer { coinSnapshotData ->
            binding.apply {
                tvTransactionTradePrice.text =
                    ConvertUtil.tradePrice2string(coinSnapshotData.tradePrice)
                tvTransactionSignedChangeRate.text =
                    ConvertUtil.changeRate2string(coinSnapshotData.signedChangeRate)
                tvTransactionChangePrice.text =
                    ConvertUtil.tradePrice2string(coinSnapshotData.changePrice)
            }

            when (coinSnapshotData.change) {
                PriceChangeType.EVEN -> {
                    binding.apply {
                        tvTransactionTradePrice.setTextColor(textColor)
                        tvTransactionSignedChangeRate.setTextColor(textColor)
                        tvTransactionChangePrice.setTextColor(textColor)
                        ivTransactionChange.visibility = View.INVISIBLE
                    }
                }

                PriceChangeType.RISE -> {
                    binding.apply {
                        tvTransactionTradePrice.setTextColor(coinRedColor)
                        tvTransactionSignedChangeRate.setTextColor(coinRedColor)
                        tvTransactionChangePrice.setTextColor(coinRedColor)
                        ivTransactionChange.setImageResource(R.drawable.icon_price_rise)
                        ivTransactionChange.visibility = View.VISIBLE
                    }
                }

                PriceChangeType.FALL -> {
                    binding.apply {
                        tvTransactionTradePrice.setTextColor(coinBlueColor)
                        tvTransactionSignedChangeRate.setTextColor(coinBlueColor)
                        tvTransactionChangePrice.setTextColor(coinBlueColor)
                        ivTransactionChange.setImageResource(R.drawable.icon_price_fall)
                        ivTransactionChange.visibility = View.VISIBLE
                    }
                }
            }
        })

        model.coinOpeningPrice.observe(this, Observer { openingPrice ->
            if (openingPrice != null) {
                DLog.d(TAG, "openingPrice=$openingPrice")
                model.setTabRouter(TransactionTabRouter.ORDER_BOOK)
            }
        })
    }

    private fun init(pSelectedCoinData: KubitCoinInfoData) {
        model.initSelectedCoinData(pSelectedCoinData)

        binding.apply {
            tvTransactionName.text = "${pSelectedCoinData.nameKor}(${pSelectedCoinData.market})"
            ivTransactionBack.setOnClickListener {
                finish()
            }

            tvTransactionTabOrder.setOnClickListener {
                model.setTabRouter(TransactionTabRouter.ORDER_BOOK)
            }
            tvTransactionTabChart.setOnClickListener {
                model.setTabRouter(TransactionTabRouter.CHART)
            }
        }
    }

    override fun finish() {
        val bidResult = model.bidTransactionResult.value
        val askResult = model.askTransactionResult.value
        if (bidResult != null || askResult != null) {
            setResult(RESULT_OK)
        }

        super.finish()
    }

    companion object {
        private const val TAG: String = "TransactionActivity"
    }

}