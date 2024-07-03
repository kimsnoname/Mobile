package com.kubit.android.investment.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.tabs.TabLayout
import com.kubit.android.R
import com.kubit.android.base.BaseFragment
import com.kubit.android.common.deco.BorderItemDecoration
import com.kubit.android.common.dialog.MessageDialog
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.common.util.DLog
import com.kubit.android.databinding.FragmentInvestmentBinding
import com.kubit.android.investment.adapter.InvestmentAdapter
import com.kubit.android.main.viewmodel.MainViewModel
import com.kubit.android.model.data.investment.InvestmentAssetData
import com.kubit.android.model.data.investment.InvestmentData
import com.kubit.android.model.data.investment.InvestmentNotYetData
import com.kubit.android.model.data.investment.InvestmentRecordData

class InvestmentFragment : BaseFragment() {

    private val model: MainViewModel by activityViewModels()
    private var _binding: FragmentInvestmentBinding? = null
    private val binding: FragmentInvestmentBinding get() = _binding!!

    private lateinit var assetAdapter: InvestmentAdapter
    private lateinit var recordAdapter: InvestmentAdapter
    private lateinit var notYetAdapter: InvestmentAdapter

    // region Fragment LifeCycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvestmentBinding.inflate(inflater, container, false)

        setObserver()
        init()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        model.requestInvestmentTickerData()
    }

    override fun onStop() {
        super.onStop()
        model.stopTickerData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // endregion Fragment LifeCycle

    private fun setObserver() {
        model.investmentAssetData.observe(viewLifecycleOwner, Observer { assetData ->
            if (assetData != null) {
                DLog.d(TAG, "assetData=$assetData")
                assetAdapter.update(assetData)
            }
        })

        model.investmentRecordData.observe(viewLifecycleOwner, Observer { recordData ->
            if (recordData != null) {
                DLog.d(TAG, "recordData=$recordData")
                model.setProgressFlag(false)
                recordAdapter.update(recordData)
            }
        })

        model.investmentNotYetData.observe(viewLifecycleOwner, Observer { notYetData ->
            if (notYetData != null) {
                DLog.d(TAG, "notYetData=$notYetData")
                model.setProgressFlag(false)
                notYetAdapter.update(notYetData)
            }
        })
    }

    private fun init() {
        assetAdapter = InvestmentAdapter(
            InvestmentAssetData(
                KRW = 1.0,
                totalAsset = 1.0,
                totalBidPrice = 0.0,
                changeValuation = 0.0,
                totalValuation = 1.0,
                earningRate = 0.0,
                userWalletList = listOf(),
                portfolioList = listOf(PieEntry(1f, "KRW"))
            )
        ) { notYetData, pos -> }
        recordAdapter = InvestmentAdapter(
            InvestmentRecordData(listOf())
        ) { notYetData, pos -> }
        notYetAdapter = InvestmentAdapter(
            InvestmentNotYetData(listOf())
        ) { notYetData, pos ->
            if (notYetData.isSelected) {
                notYetData.isSelected = false
                model.removeNotYetData(notYetData)
            } else {
                notYetData.isSelected = true
                model.addNotYetData(notYetData)
            }
            notYetAdapter.notifyItemChanged(pos)
        }

        binding.apply {
            tlInvestment.apply {
                addTab(
                    newTab().setId(R.id.investment_tab_asset)
                        .setText(getString(R.string.investment_tab_asset))
                )
                addTab(
                    newTab().setId(R.id.investment_tab_record)
                        .setText(getString(R.string.investment_tab_record))
                )
                addTab(
                    newTab().setId(R.id.investment_tab_not_yet)
                        .setText(getString(R.string.investment_tab_not_yet))
                )

                addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        when (tab?.id) {
                            R.id.investment_tab_asset -> {
                                rvInvestmentAsset.visibility = View.VISIBLE
                                rvInvestmentRecord.visibility = View.GONE
                                rvInvestmentNotYet.visibility = View.GONE
                                tvInvestmentRemoveSelectOrder.visibility = View.GONE
                            }

                            R.id.investment_tab_record -> {
                                rvInvestmentAsset.visibility = View.GONE
                                rvInvestmentRecord.visibility = View.VISIBLE
                                rvInvestmentNotYet.visibility = View.GONE
                                tvInvestmentRemoveSelectOrder.visibility = View.GONE

                                if (model.investmentRecordData.value == null) {
                                    model.requestInvestmentRecordData()
                                }
                            }

                            R.id.investment_tab_not_yet -> {
                                rvInvestmentAsset.visibility = View.GONE
                                rvInvestmentRecord.visibility = View.GONE
                                rvInvestmentNotYet.visibility = View.VISIBLE
                                tvInvestmentRemoveSelectOrder.visibility = View.VISIBLE

                                if (model.investmentNotYetData.value == null) {
                                    model.requestInvestmentNotYetData()
                                }
                            }
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {
                    }
                })
            }

            rvInvestmentAsset.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = assetAdapter
                itemAnimator = null
                addItemDecoration(
                    BorderItemDecoration(
                        borderPos = listOf(BorderItemDecoration.BorderPos.BOTTOM),
                        borderWidth = ConvertUtil.dp2px(requireContext(), 2),
                        borderColor = ContextCompat.getColor(requireContext(), R.color.border)
                    )
                )
            }
            rvInvestmentRecord.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = recordAdapter
                itemAnimator = null
                addItemDecoration(
                    BorderItemDecoration(
                        borderPos = listOf(BorderItemDecoration.BorderPos.BOTTOM),
                        borderWidth = ConvertUtil.dp2px(requireContext(), 2),
                        borderColor = ContextCompat.getColor(requireContext(), R.color.border)
                    )
                )
            }
            rvInvestmentNotYet.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = notYetAdapter
                itemAnimator = null
                addItemDecoration(
                    BorderItemDecoration(
                        borderPos = listOf(BorderItemDecoration.BorderPos.BOTTOM),
                        borderWidth = ConvertUtil.dp2px(requireContext(), 2),
                        borderColor = ContextCompat.getColor(requireContext(), R.color.border)
                    )
                )
            }

            tvInvestmentRemoveSelectOrder.setOnClickListener {
                if (model.enableRemvoeNotYetData) {
                    showRemoveNotYetDialog()
                } else {
                    showToastMsg(getString(R.string.toast_msg_selected_not_yet_empty))
                }
            }
        }
    }

    private fun showRemoveNotYetDialog() {
        for (fragment in childFragmentManager.fragments) {
            if (fragment is MessageDialog) {
                return
            }
        }

        MessageDialog(
            pMsg = getString(R.string.dialog_msg_005),
            pLeftBtnText = "거래취소",
            pLeftBtnClickListener = {
                model.requestRemoveNotYetData()
            },
            pRightBtnText = "닫기",
            pRightBtnClickListener = {
            }
        ).show(childFragmentManager, MessageDialog.TAG)
    }

    companion object {
        const val TAG: String = "InvestmentFragment"

        private var instance: InvestmentFragment? = null

        @JvmStatic
        fun getInstance(): InvestmentFragment {
            if (instance == null) {
                instance = InvestmentFragment()
            }

            return instance!!
        }

        @JvmStatic
        fun clearInstance() {
            instance = null
        }
    }
}