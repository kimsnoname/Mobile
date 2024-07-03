package com.kubit.android.model.data.investment

interface InvestmentData {
    /**
     * @return RecyclerView에 표시할 Item 개수를 반환
     */
    fun getItemCount(): Int

    /**
     * @param itemPos   Item 포지션
     *
     * @return Item과 대응되는 ViewHolder Layout ID를 반환
     */
    fun getItemType(itemPos: Int): Int
}