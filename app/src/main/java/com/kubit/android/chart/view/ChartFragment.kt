package com.kubit.android.chart.view

import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.kubit.android.R
import com.kubit.android.base.BaseFragment
import com.kubit.android.common.util.ChartUtil
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.common.util.DLog
import com.kubit.android.databinding.FragmentChartBinding
import com.kubit.android.databinding.PopupWindowChartMainIndicatorSelectorBinding
import com.kubit.android.databinding.PopupWindowChartUnitSelectorBinding
import com.kubit.android.model.data.chart.ChartMainIndicator
import com.kubit.android.model.data.chart.ChartUnit
import com.kubit.android.transaction.viewmodel.TransactionViewModel

class ChartFragment : BaseFragment() {

    private val model: TransactionViewModel by activityViewModels()
    private var _binding: FragmentChartBinding? = null
    private val binding: FragmentChartBinding get() = _binding!!

    // region Color Resource
    private val textColor: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.text)
    }
    private val secondaryColor: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.secondary)
    }
    private val coinRedColor: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.coin_red)
    }
    private val coinBlueColor: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.coin_blue)
    }
    private val grayColor: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.gray)
    }
    private val borderColor: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.border)
    }
    private val movingAvg5Color: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.moving_avg_5)
    }
    private val movingAvg10Color: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.moving_avg_10)
    }
    private val movingAvg20Color: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.moving_avg_20)
    }
    private val movingAvg60Color: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.moving_avg_60)
    }
    private val movingAvg120Color: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.moving_avg_120)
    }
    // endregion Color Resource

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
        _binding = FragmentChartBinding.inflate(inflater, container, false)

        setObserver()
        init()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        model.requestCoinChart()
    }

    override fun onStop() {
        super.onStop()
        model.stopCoinChart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // endregion Fragment LifeCycle

    private fun setObserver() {
        model.chartMainIndicator.observe(viewLifecycleOwner, Observer { chartMainIndicator ->
            val legendList: List<LegendEntry> =
                ChartUtil.getChartMainIndicatorLegend(chartMainIndicator, requireContext())
            binding.chartPrice.legend.setCustom(legendList)
        })

        model.chartUnit.observe(viewLifecycleOwner, Observer { unit ->
            if (unit != null) {
                when (unit) {
                    ChartUnit.MINUTE_1 -> {
                        binding.rbChartUnitMinute.text = getString(R.string.chart_unitMinute_1)
                    }

                    ChartUnit.MINUTE_3 -> {
                        binding.rbChartUnitMinute.text = getString(R.string.chart_unitMinute_3)
                    }

                    ChartUnit.MINUTE_5 -> {
                        binding.rbChartUnitMinute.text = getString(R.string.chart_unitMinute_5)
                    }

                    ChartUnit.MINUTE_10 -> {
                        binding.rbChartUnitMinute.text = getString(R.string.chart_unitMinute_10)
                    }

                    ChartUnit.MINUTE_15 -> {
                        binding.rbChartUnitMinute.text = getString(R.string.chart_unitMinute_15)
                    }

                    ChartUnit.MINUTE_30 -> {
                        binding.rbChartUnitMinute.text = getString(R.string.chart_unitMinute_30)
                    }

                    ChartUnit.MINUTE_60 -> {
                        binding.rbChartUnitMinute.text = getString(R.string.chart_unitMinute_60)
                    }

                    ChartUnit.MINUTE_240 -> {
                        binding.rbChartUnitMinute.text = getString(R.string.chart_unitMinute_240)
                    }

                    ChartUnit.DAY -> {
                    }

                    ChartUnit.WEEK -> {
                    }

                    ChartUnit.MONTH -> {
                    }
                }
            }
        })

        model.chartDataWrapper.observe(viewLifecycleOwner, Observer { chartDataWrapper ->
            if (chartDataWrapper != null) {
                // region PriceChart
                val candleDataSet = CandleDataSet(chartDataWrapper.candleEntries, "").apply {
                    axisDependency = YAxis.AxisDependency.LEFT
                    // 심지 부분 설정
                    shadowColor = grayColor
                    shadowWidth = 0.7F
                    // 음봉
                    decreasingColor = coinBlueColor
                    decreasingPaintStyle = Paint.Style.FILL
                    // 양봉
                    increasingColor = coinRedColor
                    increasingPaintStyle = Paint.Style.FILL

                    neutralColor = textColor
                    setDrawValues(false)
                    // 터치시 노란 선 제거
                    highLightColor = Color.TRANSPARENT
                }
                binding.chartPrice.apply {
                    data = CombinedData().apply {
                        setData(CandleData(candleDataSet))

                        model.chartMainIndicator.value?.let { chartMainIndicator ->
                            setData(
                                ChartUtil.getChartMainIndicator(
                                    chartMainIndicator,
                                    chartDataWrapper.candleEntries,
                                    requireContext()
                                )
                            )
                        }
                    }
                    invalidate()
                }
                // endregion PriceChart

                // region Transaction Volume Chart
                val transactionVolumeDataSet =
                    BarDataSet(chartDataWrapper.transactionVolumeEntries, "").apply {
                        colors = chartDataWrapper.transactionVolumeColors.let { colorIds ->
                            val ret = arrayListOf<Int>()
                            colorIds.forEach { colorId ->
                                ret.add(ContextCompat.getColor(requireContext(), colorId))
                            }
                            ret
                        }
                        setDrawValues(false)
                        highLightColor = Color.TRANSPARENT
                    }

                binding.chartTransactionVolume.apply {
                    data = CombinedData().apply {
                        setData(BarData(transactionVolumeDataSet))
                        setData(LineData().apply {
                            addDataSet(
                                LineDataSet(
                                    chartDataWrapper.transactionVolumeAvg5Entries,
                                    ""
                                ).apply {
                                    setDrawCircles(false)
                                    color = movingAvg5Color
                                    highLightColor = Color.TRANSPARENT
                                    valueTextSize = 0f
                                    lineWidth = 1.0f
                                }
                            )
                            addDataSet(
                                LineDataSet(
                                    chartDataWrapper.transactionVolumeAvg10Entries,
                                    ""
                                ).apply {
                                    setDrawCircles(false)
                                    color = movingAvg10Color
                                    highLightColor = Color.TRANSPARENT
                                    valueTextSize = 0f
                                    lineWidth = 1.0f
                                }
                            )
                            addDataSet(
                                LineDataSet(
                                    chartDataWrapper.transactionVolumeAvg20Entries,
                                    ""
                                ).apply {
                                    setDrawCircles(false)
                                    color = movingAvg20Color
                                    highLightColor = Color.TRANSPARENT
                                    valueTextSize = 0f
                                    lineWidth = 1.0f
                                }
                            )
                        })
                    }
                    invalidate()
                }
                // endregion Transaction Volume Chart
            }
        })
    }

    private fun init() {
        initPriceChart()
        initVolumeChart()

        binding.apply {
            rgChartUnit.setOnCheckedChangeListener { radioGroup, id ->
                when (id) {
                    R.id.rb_chart_unitMinute -> {
                        model.setChartUnitToMinute()
                    }

                    R.id.rb_chart_unitDay -> {
                        model.setChartUnitToDay()
                    }

                    R.id.rb_chart_unitWeek -> {
                        model.setChartUnitToWeek()
                    }

                    R.id.rb_chart_unitMonth -> {
                        model.setChartUnitToMonth()
                    }
                }
            }
            rbChartUnitMinute.setOnClickListener {
                if (!chartUnitSelectorPopupWindow.isShowing) {
                    applyChartUnitToPopupWindow()
                    chartUnitSelectorPopupWindow.showAsDropDown(
                        rbChartUnitMinute,
                        -(ConvertUtil.dp2px(requireContext(), 60) - it.measuredWidth) / 2,
                        0,
                        Gravity.CENTER_HORIZONTAL
                    )
                }
            }
            clChartSetting.setOnClickListener {
                if (!chartMainIndicatorSelectorPopupWindow.isShowing) {
                    applyChartMainIndicatorToPopupWindow()
                    chartMainIndicatorSelectorPopupWindow.showAsDropDown(
                        clChartSetting,
                        0,
                        0,
                        Gravity.RIGHT
                    )
                }
            }
        }
    }

    private fun initPriceChart() {
        binding.apply {
            chartPrice.syncChart(chartTransactionVolume)

            chartPrice.description.isEnabled = false
            chartPrice.setMaxVisibleValueCount(200)
            chartPrice.setPinchZoom(false)
            chartPrice.setDrawGridBackground(false)
            // x축 설정
            chartPrice.xAxis.apply {
                textColor = Color.TRANSPARENT
                position = XAxis.XAxisPosition.BOTTOM
                // 세로선 표시 여부 설정
                this.setDrawGridLines(true)
                axisLineColor = grayColor
                gridColor = grayColor
            }
            // 왼쪽 y축 설정
            chartPrice.axisLeft.apply {
                textColor = this@ChartFragment.textColor
                isEnabled = false
            }
            // 오른쪽 y축 설정
            chartPrice.axisRight.apply {
                setLabelCount(7, false)
                textColor = this@ChartFragment.textColor
                // 가로선 표시 여부 설정
                setDrawGridLines(true)
                // 차트의 오른쪽 테두리 라인 설정
                setDrawAxisLine(true)
                axisLineColor = grayColor
                gridColor = grayColor
            }
            chartPrice.legend.apply {
                isEnabled = true
                textColor = this@ChartFragment.textColor
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(true)
            }
        }
    }

    private fun initVolumeChart() {
        binding.apply {
            chartTransactionVolume.syncChart(chartPrice)

            chartTransactionVolume.legend.isEnabled = true

            chartTransactionVolume.description.isEnabled = false
            chartTransactionVolume.setMaxVisibleValueCount(200)
            chartTransactionVolume.setPinchZoom(false)
            chartTransactionVolume.setDrawGridBackground(false)
            chartTransactionVolume.xAxis.apply {
                textColor = Color.TRANSPARENT
                position = XAxis.XAxisPosition.BOTTOM
                this.setDrawGridLines(true)
                axisLineColor = grayColor
                gridColor = grayColor
            }
            chartTransactionVolume.axisLeft.apply {
                textColor = this@ChartFragment.textColor
                isEnabled = false
            }
            chartTransactionVolume.axisRight.apply {
                setLabelCount(7, false)
                textColor = Color.WHITE
                setDrawGridLines(true)
                setDrawAxisLine(true)
                axisLineColor = grayColor
                gridColor = grayColor
            }
            chartTransactionVolume.legend.apply {
                isEnabled = true
                setCustom(
                    listOf(
                        LegendEntry().apply {
                            label = "5"
                            formColor = movingAvg5Color
                        },
                        LegendEntry().apply {
                            label = "10"
                            formColor = movingAvg10Color
                        },
                        LegendEntry().apply {
                            label = "20"
                            formColor = movingAvg20Color
                        }
                    )
                )
                textColor = this@ChartFragment.textColor
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(true)
            }
        }
    }

    // region Chart Main Indicator Selector PopupWindow
    private val chartMainIndicatorSelectorPopupView: View by lazy {
        layoutInflater.inflate(R.layout.popup_window_chart_main_indicator_selector, null)
    }
    private val chartMainIndicatorSelectorBinding: PopupWindowChartMainIndicatorSelectorBinding by lazy {
        PopupWindowChartMainIndicatorSelectorBinding.bind(chartMainIndicatorSelectorPopupView)
    }
    private val chartMainIndicatorSelectorPopupWindow: PopupWindow by lazy {
        initChartMainIndicatorSelectorPopupWindow()
    }

    private fun initChartMainIndicatorSelectorPopupWindow(): PopupWindow {
        chartMainIndicatorSelectorBinding.apply {
            rgChartMainIndicatorSelector.setOnCheckedChangeListener { radioGroup, id ->
                when (id) {
                    R.id.rb_chartMainIndicatorSelector_movingAvg -> {
                        model.setChartMainIndicator(ChartMainIndicator.MOVING_AVERAGE)
                        chartMainIndicatorSelectorPopupWindow.dismiss()
                    }

                    R.id.rb_chartMainIndicatorSelector_bollingerBands -> {
                        model.setChartMainIndicator(ChartMainIndicator.BOLLINGER_BANDS)
                        chartMainIndicatorSelectorPopupWindow.dismiss()
                    }

                    R.id.rb_chartMainIndicatorSelector_dailyBalanceTable -> {
                        model.setChartMainIndicator(ChartMainIndicator.DAILY_BALANCE_TABLE)
                        chartMainIndicatorSelectorPopupWindow.dismiss()
                    }

                    R.id.rb_chartMainIndicatorSelector_pivot -> {
                        model.setChartMainIndicator(ChartMainIndicator.PIVOT)
                        chartMainIndicatorSelectorPopupWindow.dismiss()
                    }

                    R.id.rb_chartMainIndicatorSelector_envelopes -> {
                        model.setChartMainIndicator(ChartMainIndicator.ENVELOPES)
                        chartMainIndicatorSelectorPopupWindow.dismiss()
                    }

                    R.id.rb_chartMainIndicatorSelector_priceChannels -> {
                        model.setChartMainIndicator(ChartMainIndicator.PRICE_CHANNELS)
                        chartMainIndicatorSelectorPopupWindow.dismiss()
                    }
                }
            }
        }

        applyChartMainIndicatorToPopupWindow()

        return PopupWindow(
            chartMainIndicatorSelectorPopupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = 10f
        }
    }

    private fun applyChartMainIndicatorToPopupWindow() {
        model.chartMainIndicator.value?.let { chartMainIndicator ->
            when (chartMainIndicator) {
                ChartMainIndicator.MOVING_AVERAGE -> {
                    chartMainIndicatorSelectorBinding.rbChartMainIndicatorSelectorMovingAvg.toggle()
                }

                ChartMainIndicator.BOLLINGER_BANDS -> {
                    chartMainIndicatorSelectorBinding.rbChartMainIndicatorSelectorBollingerBands.toggle()
                }

                ChartMainIndicator.DAILY_BALANCE_TABLE -> {
                    chartMainIndicatorSelectorBinding.rbChartMainIndicatorSelectorDailyBalanceTable.toggle()
                }

                ChartMainIndicator.PIVOT -> {
                    chartMainIndicatorSelectorBinding.rbChartMainIndicatorSelectorPivot.toggle()
                }

                ChartMainIndicator.ENVELOPES -> {
                    chartMainIndicatorSelectorBinding.rbChartMainIndicatorSelectorEnvelopes.toggle()
                }

                ChartMainIndicator.PRICE_CHANNELS -> {
                    chartMainIndicatorSelectorBinding.rbChartMainIndicatorSelectorPriceChannels.toggle()
                }
            }
        }
    }
    // endregion Chart Main Indicator Selector PopupWindow

    // region Chart Unit Selector PopupWindow
    private val chartUnitSelectorPopupView: View by lazy {
        layoutInflater.inflate(R.layout.popup_window_chart_unit_selector, null)
    }
    private val chartUnitSelectorBinding: PopupWindowChartUnitSelectorBinding by lazy {
        PopupWindowChartUnitSelectorBinding.bind(chartUnitSelectorPopupView)
    }
    private val chartUnitSelectorPopupWindow: PopupWindow by lazy {
        initChartUnitSelectorPopupWindow()
    }

    private fun initChartUnitSelectorPopupWindow(): PopupWindow {
        chartUnitSelectorBinding.apply {
            rgChartUnitSelectorMinute.setOnCheckedChangeListener { radioGroup, id ->
                when (id) {
                    R.id.rb_chartUnitSelector_minute1 -> {
                        model.setChartUnitToMinute(ChartUnit.MINUTE_1)
                        chartUnitSelectorPopupWindow.dismiss()
                    }

                    R.id.rb_chartUnitSelector_minute3 -> {
                        model.setChartUnitToMinute(ChartUnit.MINUTE_3)
                        chartUnitSelectorPopupWindow.dismiss()
                    }

                    R.id.rb_chartUnitSelector_minute5 -> {
                        model.setChartUnitToMinute(ChartUnit.MINUTE_5)
                        chartUnitSelectorPopupWindow.dismiss()
                    }

                    R.id.rb_chartUnitSelector_minute10 -> {
                        model.setChartUnitToMinute(ChartUnit.MINUTE_10)
                        chartUnitSelectorPopupWindow.dismiss()
                    }

                    R.id.rb_chartUnitSelector_minute15 -> {
                        model.setChartUnitToMinute(ChartUnit.MINUTE_15)
                        chartUnitSelectorPopupWindow.dismiss()
                    }

                    R.id.rb_chartUnitSelector_minute30 -> {
                        model.setChartUnitToMinute(ChartUnit.MINUTE_30)
                        chartUnitSelectorPopupWindow.dismiss()
                    }

                    R.id.rb_chartUnitSelector_minute60 -> {
                        model.setChartUnitToMinute(ChartUnit.MINUTE_60)
                        chartUnitSelectorPopupWindow.dismiss()
                    }

                    R.id.rb_chartUnitSelector_minute240 -> {
                        model.setChartUnitToMinute(ChartUnit.MINUTE_240)
                        chartUnitSelectorPopupWindow.dismiss()
                    }
                }
            }
        }

        applyChartUnitToPopupWindow()

        return PopupWindow(
            chartUnitSelectorPopupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = 10f
        }
    }

    private fun applyChartUnitToPopupWindow() {
        model.chartUnit.value?.let { chartUnit ->
            when (chartUnit) {
                ChartUnit.MINUTE_1 -> {
                    chartUnitSelectorBinding.rbChartUnitSelectorMinute1.toggle()
                }

                ChartUnit.MINUTE_3 -> {
                    chartUnitSelectorBinding.rbChartUnitSelectorMinute3.toggle()
                }

                ChartUnit.MINUTE_5 -> {
                    chartUnitSelectorBinding.rbChartUnitSelectorMinute5.toggle()
                }

                ChartUnit.MINUTE_10 -> {
                    chartUnitSelectorBinding.rbChartUnitSelectorMinute10.toggle()
                }

                ChartUnit.MINUTE_15 -> {
                    chartUnitSelectorBinding.rbChartUnitSelectorMinute15.toggle()
                }

                ChartUnit.MINUTE_30 -> {
                    chartUnitSelectorBinding.rbChartUnitSelectorMinute30.toggle()
                }

                ChartUnit.MINUTE_60 -> {
                    chartUnitSelectorBinding.rbChartUnitSelectorMinute60.toggle()
                }

                ChartUnit.MINUTE_240 -> {
                    chartUnitSelectorBinding.rbChartUnitSelectorMinute240.toggle()
                }

                else -> {
                }
            }
        }
    }
    // endregion Chart Unit Selector PopupWindow

    private fun CombinedChart.syncChart(pOtherChart: CombinedChart) {
        onChartGestureListener = object : OnChartGestureListener {
            override fun onChartGestureStart(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
            }

            override fun onChartGestureEnd(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
            }

            override fun onChartLongPressed(me: MotionEvent?) {
            }

            override fun onChartDoubleTapped(me: MotionEvent?) {
            }

            override fun onChartSingleTapped(me: MotionEvent?) {
            }

            override fun onChartFling(
                me1: MotionEvent?,
                me2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ) {
            }

            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
                syncCharts(mainChart = this@syncChart, otherChart = pOtherChart)
            }

            override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
                syncCharts(mainChart = this@syncChart, otherChart = pOtherChart)
            }
        }
    }

    private fun syncCharts(mainChart: CombinedChart, otherChart: CombinedChart) {
        val mainVals = FloatArray(9)
        val otherVals = FloatArray(9)
        val mainMatrix: Matrix = mainChart.viewPortHandler.matrixTouch
        mainMatrix.getValues(mainVals)

        val otherMatrix: Matrix = otherChart.viewPortHandler.matrixTouch
        otherMatrix.getValues(otherVals)
        otherVals[Matrix.MSCALE_X] = mainVals[Matrix.MSCALE_X]
        otherVals[Matrix.MTRANS_X] = mainVals[Matrix.MTRANS_X]
        otherVals[Matrix.MSKEW_X] = mainVals[Matrix.MSKEW_X]
        otherMatrix.setValues(otherVals)
        otherChart.viewPortHandler.refresh(otherMatrix, otherChart, true)
    }

    companion object {
        const val TAG: String = "ChartFragment"

        private var instance: ChartFragment? = null

        @JvmStatic
        fun getInstance(): ChartFragment {
            if (instance == null) {
                instance = ChartFragment()
            }

            return instance!!
        }

        @JvmStatic
        fun clearInstance() {
            instance = null
        }
    }
}