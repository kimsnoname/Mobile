package com.kubit.android.exchange.viewholder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kubit.android.R
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.databinding.ItemExchangeRecordBinding
import com.kubit.android.model.data.exchange.ExchangeRecordData
import com.kubit.android.model.data.exchange.ExchangeType

class ExchangeRecordViewHolder(
    private val binding: ItemExchangeRecordBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val context: Context get() = binding.root.context

    private val coinBlueColor: Int = ContextCompat.getColor(context, R.color.coin_blue)
    private val coinRedColor: Int = ContextCompat.getColor(context, R.color.coin_red)

    private fun getTextColor(pExchangeType: ExchangeType): Int = when (pExchangeType) {
        ExchangeType.DEPOSIT -> coinRedColor
        ExchangeType.WITHDRAWAL -> coinBlueColor
    }

    fun bindData(pData: ExchangeRecordData) {
        val pos = bindingAdapterPosition

        binding.apply {
            // 입출금 여부
            tvExchangeRecordItemExchangeType.text = when (pData.exchangeType) {
                ExchangeType.DEPOSIT -> "입금"
                ExchangeType.WITHDRAWAL -> "출금"
            }
            tvExchangeRecordItemExchangeType.setTextColor(getTextColor(pData.exchangeType))
            // 금액
            tvExchangeRecordItemKrw.text = ConvertUtil.price2krwString(pData.krw, pWithKRW = true)
            // 시간
            tvExchangeRecordItemTime.text = pData.time
        }
    }

}