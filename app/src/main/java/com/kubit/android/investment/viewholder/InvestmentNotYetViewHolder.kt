package com.kubit.android.investment.viewholder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kubit.android.R
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.databinding.ItemInvestmentNotYetBinding
import com.kubit.android.model.data.investment.InvestmentNotYetData
import com.kubit.android.model.data.investment.NotYetData
import com.kubit.android.model.data.transaction.TransactionType

class InvestmentNotYetViewHolder(
    private val binding: ItemInvestmentNotYetBinding,
    private val onNotYetItemClick: (pos: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onNotYetItemClick(bindingAdapterPosition)
        }
    }

    private val context: Context get() = binding.root.context

    private val coinBlueColor: Int = ContextCompat.getColor(context, R.color.coin_blue)
    private val coinRedColor: Int = ContextCompat.getColor(context, R.color.coin_red)

    private fun getTextColor(pTransactionType: TransactionType): Int = when (pTransactionType) {
        TransactionType.ASK -> coinBlueColor
        TransactionType.BID -> coinRedColor
    }

    fun bindData(pData: InvestmentNotYetData) {
        val pos = bindingAdapterPosition
        val notYet = pData.notYetList[pos]

        binding.apply {
            // 코인명
            tvInvestmentNotYetItemName.text = "${notYet.nameKor}(${notYet.coinCode})"
            // 거래 타입
            tvInvestmentNotYetItemTradeType.text = when (notYet.transactionType) {
                TransactionType.ASK -> "매도"
                TransactionType.BID -> "매수"
            }
            tvInvestmentNotYetItemTradeType.setTextColor(getTextColor(notYet.transactionType))
            // 선택 여부
            ivInvestmentNotYetItemCheck.setImageResource(if (notYet.isSelected) R.drawable.icon_check_selected else R.drawable.icon_check_unselected)
            // 주문시간
            tvInvestmentNotYetItemTime.text = notYet.time
            // 주문수량
            tvInvestmentNotYetItemQuantity.text =
                "${ConvertUtil.coinQuantity2string(notYet.quantity)} ${notYet.coinCode}"
            // 주문가격
            tvInvestmentNotYetItemPrice.text = "${ConvertUtil.tradePrice2string(notYet.price)} KRW"
            // 미체결량
            tvInvestmentNotYetItemNotYetQuantity.text =
                "${ConvertUtil.coinQuantity2string(notYet.notYetQuantity)} ${notYet.coinCode}"
        }
    }

}