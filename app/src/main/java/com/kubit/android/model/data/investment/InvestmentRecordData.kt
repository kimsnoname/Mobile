package com.kubit.android.model.data.investment

import com.kubit.android.R

data class InvestmentRecordData(
    val recordList: List<RecordData>
) : InvestmentData {

    override fun getItemCount(): Int = recordList.size

    override fun getItemType(itemPos: Int): Int = R.layout.item_investment_record

}