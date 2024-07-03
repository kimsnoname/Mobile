package com.kubit.android.common.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kubit.android.R
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.common.util.DLog
import com.kubit.android.databinding.DialogEnterPriceBinding
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.round

class EnterPriceDialog(
    private val priceType: Type,
    private val initUnitPrice: Double,
    private val initQuantity: Double,
    private val initTotalPrice: Double,
    private val onConfirmListener: (priceType: Type, value: Double) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: DialogEnterPriceBinding? = null
    private val binding: DialogEnterPriceBinding get() = _binding!!

    private val withDecimalPoint: Boolean = initUnitPrice < 100
    private val upFormat = DecimalFormat("############.########").apply {
        roundingMode = RoundingMode.UP
    }
    private val downFormat = DecimalFormat("############.########").apply {
        roundingMode = RoundingMode.DOWN
    }

    private var strUnitPrice: String = "0"
        set(value) {
            field = value

            binding.tvEnterPriceUnitPrice.text = try {
                ConvertUtil.tradePrice2string(
                    value.toDouble(),
                    pWithDecimalPoint = withDecimalPoint
                )
            } catch (e: NumberFormatException) {
                value
            }
        }
    private var strQuantity: String = "0"
        set(value) {
            field = value

            binding.tvEnterPriceQuantity.text = try {
                ConvertUtil.coinQuantity2string(value.toDouble())
            } catch (e: NumberFormatException) {
                value
            }
        }
    private var strTotalPrice: String = "0"
        set(value) {
            field = value

            binding.tvEnterPriceTotalPrice.text = try {
                ConvertUtil.price2krwString(value.toDouble(), pWithKRW = true)
            } catch (e: NumberFormatException) {
                value
            }
        }

    private val regex: Regex = Regex("0+")
    private var _dotExist: Boolean = when (priceType) {
        Type.UNIT_PRICE -> initUnitPrice.toString().split('.').let { list ->
            if (list.size < 2) false else if (list[1].contains("E")) false else !regex.matches(list[1])
        }

        Type.QUANTITY -> initQuantity.toString().split('.').let { list ->
            if (list.size < 2) false else if (list[1].contains("E")) false else !regex.matches(list[1])
        }

        Type.TOTAL_PRICE -> initTotalPrice.toString().split('.').let { list ->
            if (list.size < 2) false else if (list[1].contains("E")) false else !regex.matches(list[1])
        }
    }
    private val dotExist: Boolean get() = _dotExist

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
        _binding = DialogEnterPriceBinding.inflate(inflater, container, false)

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
            tvEnterPriceTitle.text = when (priceType) {
                Type.UNIT_PRICE -> getString(R.string.enterPrice_unitPriceTitle)
                Type.QUANTITY -> getString(R.string.enterPrice_quantityTitle)
                Type.TOTAL_PRICE -> getString(R.string.enterPrice_totalPriceTitle)
            }
            strUnitPrice = upFormat.format(initUnitPrice)
            strQuantity = downFormat.format(initQuantity)
            strTotalPrice = upFormat.format(initTotalPrice)

            ivEnterPriceClose.setOnClickListener {
                dismiss()
            }
            tvEnterPriceClear.setOnClickListener {
                strUnitPrice = upFormat.format(initUnitPrice)
                strQuantity = downFormat.format(initQuantity)
                strTotalPrice = upFormat.format(initTotalPrice)
                DLog.d(
                    TAG,
                    "clear! strUnitPrice=$strUnitPrice, strQuantity=$strQuantity, strTotalPrice=$strTotalPrice"
                )
            }
            tvEnterPriceConfirm.setOnClickListener {
                when (priceType) {
                    Type.UNIT_PRICE -> {
                        dismiss()
                        onConfirmListener(priceType, strUnitPrice.toDoubleOrNull() ?: 0.0)
                    }

                    Type.QUANTITY -> {
                        dismiss()
                        onConfirmListener(priceType, strQuantity.toDoubleOrNull() ?: 0.0)
                    }

                    Type.TOTAL_PRICE -> {
                        dismiss()
                        onConfirmListener(priceType, strTotalPrice.toDoubleOrNull() ?: 0.0)
                    }
                }
            }

            tvEnterPriceNum1.setOnClickListener {
                appendNumber(1)
            }
            tvEnterPriceNum2.setOnClickListener {
                appendNumber(2)
            }
            tvEnterPriceNum3.setOnClickListener {
                appendNumber(3)
            }
            tvEnterPriceNum4.setOnClickListener {
                appendNumber(4)
            }
            tvEnterPriceNum5.setOnClickListener {
                appendNumber(5)
            }
            tvEnterPriceNum6.setOnClickListener {
                appendNumber(6)
            }
            tvEnterPriceNum7.setOnClickListener {
                appendNumber(7)
            }
            tvEnterPriceNum8.setOnClickListener {
                appendNumber(8)
            }
            tvEnterPriceNum9.setOnClickListener {
                appendNumber(9)
            }
            tvEnterPriceNum0.setOnClickListener {
                when (priceType) {
                    Type.UNIT_PRICE -> {
                        if (strUnitPrice != "0") {
                            appendNumber(0)
                        }
                    }

                    Type.QUANTITY -> {
                        if (strQuantity != "0") {
                            appendNumber(0)
                        }
                    }

                    Type.TOTAL_PRICE -> {
                        if (strTotalPrice != "0") {
                            appendNumber(0)
                        }
                    }
                }
            }
            tvEnterPriceDot.setOnClickListener {
                if (priceType == Type.QUANTITY && !dotExist) {
                    strQuantity += '.'
                    _dotExist = true
                }
            }
            clEnterPriceNumDelete.setOnClickListener {
                popNumber()
            }
        }
    }

    private fun appendNumber(pNum: Int) {
        when (priceType) {
            Type.UNIT_PRICE -> {
                DLog.d("${TAG}_appendNumber", "dotExist=$dotExist, strUnitPrice=$strUnitPrice")
                if (!dotExist) {
                    if (strUnitPrice == "0") {
                        strUnitPrice = pNum.toString()
                    } else {
                        strUnitPrice += pNum.toString()
                    }
                }
            }

            Type.QUANTITY -> {
                DLog.d("${TAG}_appendNumber", "dotExist=$dotExist, strQuantity=$strQuantity")
                if (strQuantity == "0") {
                    strQuantity = pNum.toString()
                } else {
                    strQuantity += pNum.toString()
                }
            }

            Type.TOTAL_PRICE -> {
                DLog.d("${TAG}_appendNumber", "dotExist=$dotExist, strTotalPrice=$strTotalPrice")
                if (!dotExist) {
                    if (strTotalPrice == "0") {
                        strTotalPrice = pNum.toString()
                    } else {
                        strTotalPrice += pNum.toString()
                    }
                }
            }
        }
    }

    private fun popNumber() {
        when (priceType) {
            Type.UNIT_PRICE -> {
                if (strUnitPrice.isNotEmpty()) {
                    val last = strUnitPrice.last()
                    if (last == '.') {
                        _dotExist = false
                    }
                    strUnitPrice =
                        strUnitPrice.substring(0, strUnitPrice.length - 1).ifEmpty { "0" }
                }
            }

            Type.QUANTITY -> {
                if (strQuantity.isNotEmpty()) {
                    val last = strQuantity.last()
                    if (last == '.') {
                        _dotExist = false
                    }
                    strQuantity = strQuantity.substring(0, strQuantity.length - 1).ifEmpty { "0" }
                }
            }

            Type.TOTAL_PRICE -> {
                if (strTotalPrice.isNotEmpty()) {
                    val last = strTotalPrice.last()
                    if (last == '.') {
                        _dotExist = false
                    }
                    strTotalPrice =
                        strTotalPrice.substring(0, strTotalPrice.length - 1).ifEmpty { "0" }
                }
            }
        }
    }

    enum class Type {
        UNIT_PRICE,
        QUANTITY,
        TOTAL_PRICE
    }

    companion object {
        private const val TAG: String = "EnterPriceDialog"
    }

}