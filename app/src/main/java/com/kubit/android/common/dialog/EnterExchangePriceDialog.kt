package com.kubit.android.common.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kubit.android.R
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.databinding.DialogEnterExchangePriceBinding
import com.kubit.android.model.data.exchange.ExchangeType

class EnterExchangePriceDialog(
    private val exchangeType: ExchangeType,
    private val onConfirmListener: (exchangeType: ExchangeType, value: Double) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: DialogEnterExchangePriceBinding? = null
    private val binding: DialogEnterExchangePriceBinding get() = _binding!!

    private var strPrice: String = "0"
        set(value) {
            field = value

            binding.tvEnterExchangePriceTotalPrice.text = try {
                ConvertUtil.price2krwString(value.toDouble(), pWithKRW = true)
            } catch (e: NumberFormatException) {
                value
            }
        }

    // region Fragment Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)

        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEnterExchangePriceBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // endregion Fragment Lifecycle

    private fun init() {
        binding.apply {
            tvEnterExchangePriceTitle.text = when (exchangeType) {
                ExchangeType.DEPOSIT -> getString(R.string.enterExchangePrice_depositTitle)
                ExchangeType.WITHDRAWAL -> getString(R.string.enterExchangePrice_withdrawalTitle)
            }
            strPrice = "0"

            ivEnterExchangePriceClose.setOnClickListener {
                dismiss()
            }
            tvEnterExchangePriceClear.setOnClickListener {
                strPrice = "0"
            }
            tvEnterExchangePriceConfirm.setOnClickListener {
                dismiss()
                onConfirmListener(exchangeType, strPrice.toDoubleOrNull() ?: 0.0)
            }

            tvEnterExchangePriceNum1.setOnClickListener {
                appendNumber(1)
            }
            tvEnterExchangePriceNum2.setOnClickListener {
                appendNumber(2)
            }
            tvEnterExchangePriceNum3.setOnClickListener {
                appendNumber(3)
            }
            tvEnterExchangePriceNum4.setOnClickListener {
                appendNumber(4)
            }
            tvEnterExchangePriceNum5.setOnClickListener {
                appendNumber(5)
            }
            tvEnterExchangePriceNum6.setOnClickListener {
                appendNumber(6)
            }
            tvEnterExchangePriceNum7.setOnClickListener {
                appendNumber(7)
            }
            tvEnterExchangePriceNum8.setOnClickListener {
                appendNumber(8)
            }
            tvEnterExchangePriceNum9.setOnClickListener {
                appendNumber(9)
            }
            tvEnterExchangePriceNum0.setOnClickListener {
                appendNumber(0)
            }
            clEnterExchangePriceNumDelete.setOnClickListener {
                popNumber()
            }
        }
    }

    private fun appendNumber(pNum: Int) {
        if (strPrice == "0") {
            strPrice = pNum.toString()
        } else {
            strPrice += pNum.toString()
        }
    }

    private fun popNumber() {
        if (strPrice.isNotEmpty()) {
            strPrice = strPrice.substring(0, strPrice.length - 1).ifEmpty { "0" }
        }
    }

    companion object {
        private const val TAG: String = "EnterExchangePriceDialog"
    }

}