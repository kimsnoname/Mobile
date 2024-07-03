package com.kubit.android.coinlist.viewholder

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kubit.android.R
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.databinding.ItemCoinListBinding
import com.kubit.android.model.data.coin.CoinSnapshotData
import com.kubit.android.model.data.coin.PriceChangeType

class CoinListViewHolder(
    private val binding: ItemCoinListBinding,
    private val onViewHolderClick: (pos: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.apply {
            clCoinListItem.setOnClickListener {
                onViewHolderClick(bindingAdapterPosition)
            }
        }
    }

    fun bindData(pData: CoinSnapshotData) {
        val pos = bindingAdapterPosition

        binding.apply {
            cvCoinListItemChangeRateStick.setSignedChangeRate(pData.signedChangeRate)

            tvCoinListItemNameKor.text = pData.nameKor
            tvCoinListItemNameEng.text = pData.nameEng

            tvCoinListItemTradePrice.text =
                ConvertUtil.tradePrice2string(pData.tradePrice)
            tvCoinListItemSignedChangeRate.text =
                ConvertUtil.changeRate2string(pData.signedChangeRate)
            tvCoinListItemAccTradePrice24H.text =
                ConvertUtil.accTradePrice24H2string(pData.accTradePrice24H)

            val colorResId = when (pData.change) {
                PriceChangeType.EVEN -> R.color.gray
                PriceChangeType.RISE -> R.color.coin_red
                PriceChangeType.FALL -> R.color.coin_blue
            }
            tvCoinListItemTradePrice.setTextColor(
                ContextCompat.getColor(
                    tvCoinListItemTradePrice.context,
                    colorResId
                )
            )
            tvCoinListItemSignedChangeRate.setTextColor(
                ContextCompat.getColor(
                    tvCoinListItemSignedChangeRate.context,
                    colorResId
                )
            )
            tvCoinListItemAccTradePrice24H.setTextColor(
                ContextCompat.getColor(
                    tvCoinListItemAccTradePrice24H.context,
                    colorResId
                )
            )
        }
    }

    companion object {
        private const val TAG: String = "CoinListViewHolder"
    }

}