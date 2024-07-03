package com.kubit.android.investment.viewholder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kubit.android.R
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.databinding.ItemInvestmentRecordBinding
import com.kubit.android.model.data.investment.InvestmentRecordData
import com.kubit.android.model.data.transaction.TransactionType

class InvestmentRecordViewHolder(
    private val binding: ItemInvestmentRecordBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val context: Context get() = binding.root.context

    private val coinBlueColor: Int = ContextCompat.getColor(context, R.color.coin_blue)
    private val coinRedColor: Int = ContextCompat.getColor(context, R.color.coin_red)

    private fun getTextColor(pTransactionType: TransactionType): Int = when (pTransactionType) {
        TransactionType.ASK -> coinBlueColor
        TransactionType.BID -> coinRedColor
    }

    fun bindData(pData: InvestmentRecordData) {
        val pos = bindingAdapterPosition
        val record = pData.recordList[pos]

        binding.apply {
            // 코인명
            tvInvestmentRecordItemName.text = "${record.nameKor}(${record.coinCode})"
            // 거래 타입
            tvInvestmentRecordItemTradeType.text = when (record.transactionType) {
                TransactionType.ASK -> "매도"
                TransactionType.BID -> "매수"
            }
            tvInvestmentRecordItemTradeType.setTextColor(getTextColor(record.transactionType))
            // 체결시간
            tvInvestmentRecordItemTime.text = record.time
            // 거래금액
            tvInvestmentRecordItemTradePrice.text =
                "${ConvertUtil.tradePrice2string(record.transactionPrice)} KRW"
            // 거래수량
            tvInvestmentRecordItemQuantity.text =
                "${ConvertUtil.coinQuantity2string(record.quantity)} ${record.coinCode}"
            // 거래단가
            tvInvestmentRecordItemTradeUnitPrice.text =
                "${ConvertUtil.tradePrice2string(record.transactionUnitPrice)} KRW"
            // 수수료
            tvInvestmentRecordItemFee.text = "${ConvertUtil.tradePrice2string(record.fee)} KRW"
            // 정산금액
            tvInvestmentRecordItemReturnPrice.text =
                "${ConvertUtil.tradePrice2string(record.returnPrice)} KRW"
        }
    }

}