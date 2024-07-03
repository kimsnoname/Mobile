package com.kubit.android.order.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.kubit.android.R
import com.kubit.android.base.BaseFragment
import com.kubit.android.common.dialog.EnterPriceDialog
import com.kubit.android.common.session.KubitSession
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.common.util.DLog
import com.kubit.android.databinding.FragmentOrderBookBinding
import com.kubit.android.model.data.transaction.TransactionMethod
import com.kubit.android.model.data.transaction.TransactionType
import com.kubit.android.transaction.viewmodel.TransactionViewModel

class OrderBookFragment : BaseFragment() {

    private val model: TransactionViewModel by activityViewModels()
    private var _binding: FragmentOrderBookBinding? = null
    private val binding: FragmentOrderBookBinding get() = _binding!!

    private val coinRedColor: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.coin_red)
    }
    private val coinBlueColor: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.coin_blue)
    }
    private val textColor: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.text)
    }

    // region Fragment LifeCycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBookBinding.inflate(inflater, container, false)

        setObserver()
        init()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        model.requestCoinOrderBook()
    }

    override fun onStop() {
        super.onStop()
        model.stopCoinOrderBook()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // endregion Fragment LifeCycle

    private fun setObserver() {
        model.orderBookData.observe(viewLifecycleOwner, Observer { orderBookData ->
            if (orderBookData != null) {
                binding.cvOrderBook.update(orderBookData.unitDataList)
            }
        })

        model.transactionType.observe(viewLifecycleOwner, Observer { transactionType ->
            when (transactionType) {
                TransactionType.ASK -> {
                    setAskLayout()
                }

                TransactionType.BID -> {
                    setBidLayout()
                }

                else -> {
                    DLog.e(TAG, "Unrecognized TransactionType is $transactionType")
                }
            }
        })

        model.bidTransactionMethod.observe(viewLifecycleOwner, Observer { transactionMethod ->
            when (transactionMethod) {
                TransactionMethod.DESIGNATED_PRICE -> {
                    setDesignatedPriceLayout(TransactionType.BID)
                }

                TransactionMethod.MARKET_PRICE -> {
                    setMarketPriceLayout(TransactionType.BID)
                }

                else -> {
                    DLog.e(TAG, "Unrecognized TransactionMethod is $transactionMethod")
                }
            }
        })

        model.askTransactionMethod.observe(viewLifecycleOwner, Observer { transactionMethod ->
            when (transactionMethod) {
                TransactionMethod.DESIGNATED_PRICE -> {
                    setDesignatedPriceLayout(TransactionType.ASK)
                }

                TransactionMethod.MARKET_PRICE -> {
                    setMarketPriceLayout(TransactionType.ASK)
                }

                else -> {
                    DLog.e(TAG, "Unrecognized TransactionMethod is $transactionMethod")
                }
            }
        })

        model.coinTradePrice.observe(viewLifecycleOwner, Observer { tradePrice ->
            if (tradePrice != null) {
                model.setBidOrderUnitPrice(tradePrice)
                model.setAskOrderUnitPrice(tradePrice)
            }
        })

        model.bidOrderQuantity.observe(viewLifecycleOwner, Observer { quantity ->
            binding.tvOrderBookBidQuantity.text = ConvertUtil.coinQuantity2string(quantity)
        })

        model.askOrderQuantity.observe(viewLifecycleOwner, Observer { quantity ->
            binding.tvOrderBookAskQuantity.text = ConvertUtil.coinQuantity2string(quantity)
        })

        model.bidOrderUnitPrice.observe(viewLifecycleOwner, Observer { unitPrice ->
            val withDecimalPoint = model.coinTradePrice.value?.let { it < 100 } ?: false
            binding.tvOrderBookBidUnitPrice.text =
                ConvertUtil.tradePrice2string(unitPrice, pWithDecimalPoint = withDecimalPoint)
        })

        model.askOrderUnitPrice.observe(viewLifecycleOwner, Observer { unitPrice ->
            val withDecimalPoint = model.coinTradePrice.value?.let { it < 100 } ?: false
            binding.tvOrderBookAskUnitPrice.text =
                ConvertUtil.tradePrice2string(unitPrice, pWithDecimalPoint = withDecimalPoint)
        })

        model.bidOrderTotalPrice.observe(viewLifecycleOwner, Observer { totalPrice ->
            binding.tvOrderBookBidTotalPrice.text =
                ConvertUtil.price2krwString(totalPrice, pWithKRW = true)
        })

        model.askOrderTotalPrice.observe(viewLifecycleOwner, Observer { totalPrice ->
            binding.tvOrderBookAskTotalPrice.text =
                ConvertUtil.price2krwString(totalPrice, pWithKRW = true)
        })

        model.bidTransactionResult.observe(viewLifecycleOwner, Observer { result ->
            if (result != null) {
                model.setProgressFlag(false)

                binding.tvOrderBookBidCanOrder.text =
                    ConvertUtil.price2krwString(result.KRW, pWithKRW = true)
                showToastMsg(getString(R.string.toast_msg_bid_order_success))
            }
        })

        model.askTransactionResult.observe(viewLifecycleOwner, Observer { result ->
            if (result != null) {
                model.setProgressFlag(false)

                val wallet = model.selectedWallet
                binding.tvOrderBookAskCanOrder.text =
                    ConvertUtil.coinQuantity2string(wallet.quantity, wallet.market)
                showToastMsg(getString(R.string.toast_msg_ask_order_success))
            }
        })
    }

    private fun init() {
        binding.apply {
            cvOrderBook.post {
                cvOrderBook.fitCenter()
            }
            tvOrderBookBidCanOrder.text =
                ConvertUtil.price2krwString(KubitSession.KRW, pWithKRW = true)

            tvOrderBookTabAsk.setOnClickListener {
                model.setTransactionType(TransactionType.ASK)
            }
            tvOrderBookTabBid.setOnClickListener {
                model.setTransactionType(TransactionType.BID)
            }
            ivOrderBookBidDesignatedPrice.setOnClickListener {
                model.setBidTransactionMethod(TransactionMethod.DESIGNATED_PRICE)
            }
            tvOrderBookBidDesignatedPrice.setOnClickListener {
                model.setBidTransactionMethod(TransactionMethod.DESIGNATED_PRICE)
            }
            ivOrderBookBidMarketPrice.setOnClickListener {
                model.setBidTransactionMethod(TransactionMethod.MARKET_PRICE)
            }
            tvOrderBookBidMarketPrice.setOnClickListener {
                model.setBidTransactionMethod(TransactionMethod.MARKET_PRICE)
            }
            ivOrderBookAskDesignatedPrice.setOnClickListener {
                model.setAskTransactionMethod(TransactionMethod.DESIGNATED_PRICE)
            }
            tvOrderBookAskDesignatedPrice.setOnClickListener {
                model.setAskTransactionMethod(TransactionMethod.DESIGNATED_PRICE)
            }
            ivOrderBookAskMarketPrice.setOnClickListener {
                model.setAskTransactionMethod(TransactionMethod.MARKET_PRICE)
            }
            tvOrderBookAskMarketPrice.setOnClickListener {
                model.setAskTransactionMethod(TransactionMethod.MARKET_PRICE)
            }

            clOrderBookBidQuantity.setOnClickListener {
                EnterPriceDialog(
                    priceType = EnterPriceDialog.Type.QUANTITY,
                    initUnitPrice = model.bidOrderUnitPrice.value ?: 0.0,
                    initQuantity = model.bidOrderQuantity.value ?: 0.0,
                    initTotalPrice = model.bidOrderTotalPrice.value ?: 0.0
                ) { priceType, value ->
                    model.setBidOrderQuantity(value)
                }.show(childFragmentManager, null)
            }
            clOrderBookAskQuantity.setOnClickListener {
                EnterPriceDialog(
                    priceType = EnterPriceDialog.Type.QUANTITY,
                    initUnitPrice = model.askOrderUnitPrice.value ?: 0.0,
                    initQuantity = model.askOrderQuantity.value ?: 0.0,
                    initTotalPrice = model.askOrderTotalPrice.value ?: 0.0
                ) { priceType, value ->
                    model.setAskOrderQuantity(value)
                }.show(childFragmentManager, null)
            }
            clOrderBookBidUnitPrice.setOnClickListener {
                EnterPriceDialog(
                    priceType = EnterPriceDialog.Type.UNIT_PRICE,
                    initUnitPrice = model.bidOrderUnitPrice.value ?: 0.0,
                    initQuantity = model.bidOrderQuantity.value ?: 0.0,
                    initTotalPrice = model.bidOrderTotalPrice.value ?: 0.0
                ) { priceType, value ->
                    model.setBidOrderUnitPrice(value)
                }.show(childFragmentManager, null)
            }
            clOrderBookAskUnitPrice.setOnClickListener {
                EnterPriceDialog(
                    priceType = EnterPriceDialog.Type.UNIT_PRICE,
                    initUnitPrice = model.askOrderUnitPrice.value ?: 0.0,
                    initQuantity = model.askOrderQuantity.value ?: 0.0,
                    initTotalPrice = model.askOrderTotalPrice.value ?: 0.0
                ) { priceType, value ->
                    model.setAskOrderUnitPrice(value)
                }.show(childFragmentManager, null)
            }
            clOrderBookBidTotalPrice.setOnClickListener {
                EnterPriceDialog(
                    priceType = EnterPriceDialog.Type.TOTAL_PRICE,
                    initUnitPrice = model.bidOrderUnitPrice.value ?: 0.0,
                    initQuantity = model.bidOrderQuantity.value ?: 0.0,
                    initTotalPrice = model.bidOrderTotalPrice.value ?: 0.0
                ) { priceType, value ->
                    model.setBidOrderTotalPrice(value)
                }.show(childFragmentManager, null)
            }
            clOrderBookAskTotalPrice.setOnClickListener {
                EnterPriceDialog(
                    priceType = EnterPriceDialog.Type.TOTAL_PRICE,
                    initUnitPrice = model.askOrderUnitPrice.value ?: 0.0,
                    initQuantity = model.askOrderQuantity.value ?: 0.0,
                    initTotalPrice = model.askOrderTotalPrice.value ?: 0.0
                ) { priceType, value ->
                    model.setAskOrderTotalPrice(value)
                }.show(childFragmentManager, null)
            }

            tvOrderBookBidClear.setOnClickListener {
                model.clearBidPriceAndQuantity()
            }
            tvOrderBookAskClear.setOnClickListener {
                model.clearAskPriceAndQuantity()
            }
            tvOrderBookBidConfirm.setOnClickListener {
                when (model.bidTransactionMethod.value) {
                    // 지정가
                    TransactionMethod.DESIGNATED_PRICE -> {
                        if (!model.requestDesignatedBid()) {
                            showToastMsg(getString(R.string.toast_msg_krw_short))
                        }
                    }
                    // 시장가
                    TransactionMethod.MARKET_PRICE -> {
                        if (!model.requestMarketBid()) {
                            showToastMsg(getString(R.string.toast_msg_krw_short))
                        }
                    }
                    // error
                    else -> {
                    }
                }
            }
            tvOrderBookAskConfirm.setOnClickListener {
                when (model.askTransactionMethod.value) {
                    // 지정가
                    TransactionMethod.DESIGNATED_PRICE -> {
                        if (!model.requestDesignatedAsk()) {
                            showToastMsg(getString(R.string.toast_msg_coin_quantity_short))
                        }
                    }
                    // 시장가
                    TransactionMethod.MARKET_PRICE -> {
                        if (!model.requestMarketAsk()) {
                            showToastMsg(getString(R.string.toast_msg_coin_quantity_short))
                        }
                    }
                    // error
                    else -> {
                    }
                }
            }
        }
    }

    /**
     * 매도 화면으로 전환
     */
    private fun setAskLayout() {
        binding.apply {
            tvOrderBookTabBid.setTextColor(textColor)
            tvOrderBookTabBid.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.border
                )
            )
            tvOrderBookTabAsk.setTextColor(coinBlueColor)
            tvOrderBookTabAsk.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.background
                )
            )

            clOrderBookBidLayout.visibility = View.GONE
            clOrderBookAskLayout.visibility = View.VISIBLE

            val wallet = model.selectedWallet
            tvOrderBookAskCanOrder.text =
                ConvertUtil.coinQuantity2string(wallet.quantityAvailable, wallet.market)
        }
    }

    /**
     * 매수 화면으로 전환
     */
    private fun setBidLayout() {
        binding.apply {
            tvOrderBookTabBid.setTextColor(coinRedColor)
            tvOrderBookTabBid.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.background
                )
            )
            tvOrderBookTabAsk.setTextColor(textColor)
            tvOrderBookTabAsk.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.border
                )
            )

            clOrderBookBidLayout.visibility = View.VISIBLE
            clOrderBookAskLayout.visibility = View.GONE

            tvOrderBookBidCanOrder.text =
                ConvertUtil.price2krwString(KubitSession.KRW, pWithKRW = true)
        }
    }

    /**
     * 지정가 거래 화면으로 전환
     */
    private fun setDesignatedPriceLayout(pTransactionType: TransactionType) {
        when (pTransactionType) {
            TransactionType.BID -> {
                binding.apply {
                    ivOrderBookBidDesignatedPrice.setImageResource(R.drawable.icon_radio_selected)
                    ivOrderBookBidMarketPrice.setImageResource(R.drawable.icon_radio_unselected)

                    clOrderBookBidQuantity.visibility = View.VISIBLE
                    clOrderBookBidUnitPrice.visibility = View.VISIBLE
                }
            }

            TransactionType.ASK -> {
                binding.apply {
                    ivOrderBookAskDesignatedPrice.setImageResource(R.drawable.icon_radio_selected)
                    ivOrderBookAskMarketPrice.setImageResource(R.drawable.icon_radio_unselected)

                    clOrderBookAskUnitPrice.visibility = View.VISIBLE
                    clOrderBookAskTotalPrice.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * 시장가 거래 화면으로 전환
     */
    private fun setMarketPriceLayout(pTransactionType: TransactionType) {
        when (pTransactionType) {
            TransactionType.BID -> {
                binding.apply {
                    ivOrderBookBidDesignatedPrice.setImageResource(R.drawable.icon_radio_unselected)
                    ivOrderBookBidMarketPrice.setImageResource(R.drawable.icon_radio_selected)

                    clOrderBookBidQuantity.visibility = View.GONE
                    clOrderBookBidUnitPrice.visibility = View.GONE
                }
            }

            TransactionType.ASK -> {
                binding.apply {
                    ivOrderBookAskDesignatedPrice.setImageResource(R.drawable.icon_radio_unselected)
                    ivOrderBookAskMarketPrice.setImageResource(R.drawable.icon_radio_selected)

                    clOrderBookAskUnitPrice.visibility = View.GONE
                    clOrderBookAskTotalPrice.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        const val TAG: String = "OrderBookFragment"

        private var instance: OrderBookFragment? = null

        @JvmStatic
        fun getInstance(): OrderBookFragment {
            if (instance == null) {
                instance = OrderBookFragment()
            }

            return instance!!
        }

        @JvmStatic
        fun clearInstance() {
            instance = null
        }
    }
}