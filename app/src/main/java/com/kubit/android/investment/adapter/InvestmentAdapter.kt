package com.kubit.android.investment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kubit.android.R
import com.kubit.android.databinding.ItemInvestmentAssetBinding
import com.kubit.android.databinding.ItemInvestmentNotYetBinding
import com.kubit.android.databinding.ItemInvestmentPortfolioBinding
import com.kubit.android.databinding.ItemInvestmentRecordBinding
import com.kubit.android.databinding.ItemInvestmentWalletBinding
import com.kubit.android.investment.viewholder.InvestmentAssetViewHolder
import com.kubit.android.investment.viewholder.InvestmentNotYetViewHolder
import com.kubit.android.investment.viewholder.InvestmentPortfolioViewHolder
import com.kubit.android.investment.viewholder.InvestmentRecordViewHolder
import com.kubit.android.investment.viewholder.InvestmentWalletViewHolder
import com.kubit.android.model.data.investment.InvestmentAssetData
import com.kubit.android.model.data.investment.InvestmentData
import com.kubit.android.model.data.investment.InvestmentNotYetData
import com.kubit.android.model.data.investment.InvestmentRecordData
import com.kubit.android.model.data.investment.NotYetData

class InvestmentAdapter(
    private var data: InvestmentData,
    private val onNotYetItemClick: (notYetData: NotYetData, pos: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_investment_asset -> {
                InvestmentAssetViewHolder(
                    ItemInvestmentAssetBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            R.layout.item_investment_portfolio -> {
                InvestmentPortfolioViewHolder(
                    ItemInvestmentPortfolioBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            R.layout.item_investment_wallet -> {
                InvestmentWalletViewHolder(
                    ItemInvestmentWalletBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            R.layout.item_investment_record -> {
                InvestmentRecordViewHolder(
                    ItemInvestmentRecordBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            R.layout.item_investment_not_yet -> {
                InvestmentNotYetViewHolder(
                    ItemInvestmentNotYetBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                ) { pos ->
                    val mData = data
                    if (mData is InvestmentNotYetData) {
                        val notYetData = mData.notYetList[pos]
                        onNotYetItemClick(notYetData, pos)
                    }
                }
            }

            else -> {
                InvestmentNotYetViewHolder(
                    ItemInvestmentNotYetBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                ) { pos ->

                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is InvestmentAssetViewHolder -> {
                holder.bindData(data as InvestmentAssetData)
            }

            is InvestmentPortfolioViewHolder -> {
                holder.bindData(data as InvestmentAssetData)
            }

            is InvestmentWalletViewHolder -> {
                holder.bindData(data as InvestmentAssetData)
            }

            is InvestmentRecordViewHolder -> {
                holder.bindData(data as InvestmentRecordData)
            }

            is InvestmentNotYetViewHolder -> {
                holder.bindData(data as InvestmentNotYetData)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = data.getItemType(position)

    override fun getItemCount(): Int = data.getItemCount()

    fun update(pData: InvestmentData) {
        data = pData
        notifyDataSetChanged()
    }

    companion object {
        private const val TAG: String = "InvestmentAdapter"
    }

}