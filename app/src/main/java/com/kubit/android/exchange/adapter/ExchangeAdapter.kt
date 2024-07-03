package com.kubit.android.exchange.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kubit.android.databinding.ItemExchangeRecordBinding
import com.kubit.android.exchange.viewholder.ExchangeRecordViewHolder
import com.kubit.android.model.data.exchange.ExchangeRecordData

class ExchangeAdapter(
    private var items: List<ExchangeRecordData>
) : RecyclerView.Adapter<ExchangeRecordViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ExchangeRecordViewHolder(
        ItemExchangeRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ExchangeRecordViewHolder, position: Int) {
        val data = items[position]
        holder.bindData(data)
    }

    override fun getItemCount(): Int = items.size

    fun update(pItems: List<ExchangeRecordData>) {
        items = pItems
        notifyDataSetChanged()
    }

    companion object {
        private const val TAG: String = "ExchangeAdapter"
    }

}