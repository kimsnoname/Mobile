package com.kubit.android.exchange.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kubit.android.R
import com.kubit.android.base.BaseFragment
import com.kubit.android.coinlist.view.CoinListFragment
import com.kubit.android.common.deco.BorderItemDecoration
import com.kubit.android.common.dialog.EnterExchangePriceDialog
import com.kubit.android.common.dialog.EnterPriceDialog
import com.kubit.android.common.session.KubitSession
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.common.util.DLog
import com.kubit.android.databinding.FragmentExchangeBinding
import com.kubit.android.exchange.adapter.ExchangeAdapter
import com.kubit.android.main.viewmodel.MainViewModel
import com.kubit.android.model.data.exchange.ExchangeType

class ExchangeFragment : BaseFragment() {

    private val model: MainViewModel by activityViewModels()
    private var _binding: FragmentExchangeBinding? = null
    private val binding: FragmentExchangeBinding get() = _binding!!

    private lateinit var exchangeAdapter: ExchangeAdapter

    // region Fragment LifeCycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExchangeBinding.inflate(inflater, container, false)

        setObserver()
        init()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        model.requestExchangeRecordData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // endregion Fragment LifeCycle

    private fun setObserver() {
        model.exchangeRecordData.observe(viewLifecycleOwner, Observer { exchangeRecordList ->
            model.setProgressFlag(false)
            exchangeAdapter.update(exchangeRecordList)

            binding.apply {
                val strKRW = ConvertUtil.price2krwString(KubitSession.KRW, pWithKRW = true)
                tvExchangeTotalKRW.text = strKRW
                tvExchangeCanWithdrawal.text = strKRW
            }
        })
    }

    private fun init() {
        exchangeAdapter = ExchangeAdapter(listOf())

        binding.apply {
            val strKRW = ConvertUtil.price2krwString(KubitSession.KRW, pWithKRW = true)
            tvExchangeTotalKRW.text = strKRW
            tvExchangeCanWithdrawal.text = strKRW

            clExchangeDeposit.setOnClickListener {
                EnterExchangePriceDialog(
                    exchangeType = ExchangeType.DEPOSIT
                ) { exchangeType, value ->
                    DLog.d(TAG, "exchangeType=$exchangeType, value=$value")
                    if (!model.requestDeposit(value)) {
                        showToastMsg(getString(R.string.toast_msg_deposit_price_condition))
                    }
                }.show(childFragmentManager, null)
            }
            clExchangeWithdrawal.setOnClickListener {
                EnterExchangePriceDialog(
                    exchangeType = ExchangeType.WITHDRAWAL
                ) { exchangeType, value ->
                    DLog.d(TAG, "exchangeType=$exchangeType, value=$value")
                    if (!model.requestWithdrawal(value)) {
                        showToastMsg(getString(R.string.toast_msg_withdrawal_condition))
                    }
                }.show(childFragmentManager, null)
            }

            rvExchangeRecord.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = exchangeAdapter
                itemAnimator = null
                addItemDecoration(
                    BorderItemDecoration(
                        borderPos = listOf(BorderItemDecoration.BorderPos.BOTTOM),
                        borderWidth = ConvertUtil.dp2px(requireContext(), 2),
                        borderColor = ContextCompat.getColor(requireContext(), R.color.border)
                    )
                )
            }
        }
    }

    companion object {
        const val TAG: String = "ExchangeFragment"

        private var instance: ExchangeFragment? = null

        @JvmStatic
        fun getInstance(): ExchangeFragment {
            if (instance == null) {
                instance = ExchangeFragment()
            }

            return instance!!
        }

        @JvmStatic
        fun clearInstance() {
            instance = null
        }
    }
}