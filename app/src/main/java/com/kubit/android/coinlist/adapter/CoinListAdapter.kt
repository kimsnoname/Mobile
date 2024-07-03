package com.kubit.android.coinlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kubit.android.coinlist.viewholder.CoinListViewHolder
import com.kubit.android.common.util.DLog
import com.kubit.android.databinding.ItemCoinListBinding
import com.kubit.android.model.data.coin.CoinSnapshotData

class CoinListAdapter(
    private val items: ArrayList<CoinSnapshotData>,
    private val onCoinItemClick: (coinSnapshotData: CoinSnapshotData) -> Unit
) : RecyclerView.Adapter<CoinListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinListViewHolder =
        CoinListViewHolder(
            ItemCoinListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onViewHolderClick = { pos ->
                DLog.d(TAG, "CoinItem is clicked!, pos=$pos, item=${items[pos]}")
                onCoinItemClick(items[pos])
            }
        )

    override fun onBindViewHolder(holder: CoinListViewHolder, position: Int) {
        val data = items[position]
        holder.bindData(data)
    }

    override fun getItemCount(): Int = items.size

    fun update(pItems: List<CoinSnapshotData>) {
        items.clear()
        items.addAll(pItems)
        notifyDataSetChanged()
    }

    companion object {
        private const val TAG: String = "CoinListAdapter"
    }

}