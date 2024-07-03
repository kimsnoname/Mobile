package com.kubit.android.model.data.investment

import com.kubit.android.R

data class InvestmentNotYetData(
    val notYetList: List<NotYetData>
) : InvestmentData {

    override fun getItemCount(): Int = notYetList.size

    override fun getItemType(itemPos: Int): Int = R.layout.item_investment_not_yet

}
