package com.kubit.android.common.util

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.kubit.android.R
import com.kubit.android.model.data.chart.ChartMainIndicator
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

object ChartUtil {

    private const val TAG: String = "ChartUtil"

    private val legendHashMap: HashMap<ChartMainIndicator, List<LegendEntry>> = hashMapOf()

    fun getChartMainIndicatorLegend(
        pChartMainIndicator: ChartMainIndicator,
        pContext: Context
    ): List<LegendEntry> {
        return legendHashMap[pChartMainIndicator] ?: when (pChartMainIndicator) {
            ChartMainIndicator.MOVING_AVERAGE -> {
                listOf<LegendEntry>(
                    LegendEntry().apply {
                        label = "단순 MA"
                        form = Legend.LegendForm.NONE
                    },
                    LegendEntry().apply {
                        label = "5"
                        formColor = ContextCompat.getColor(pContext, R.color.moving_avg_5)
                    },
                    LegendEntry().apply {
                        label = "10"
                        formColor = ContextCompat.getColor(pContext, R.color.moving_avg_10)
                    },
                    LegendEntry().apply {
                        label = "20"
                        formColor = ContextCompat.getColor(pContext, R.color.moving_avg_20)
                    },
                    LegendEntry().apply {
                        label = "60"
                        formColor = ContextCompat.getColor(pContext, R.color.moving_avg_60)
                    },
                    LegendEntry().apply {
                        label = "120"
                        formColor = ContextCompat.getColor(pContext, R.color.moving_avg_120)
                    }
                ).also {
                    legendHashMap[pChartMainIndicator] = it
                }
            }

            ChartMainIndicator.BOLLINGER_BANDS -> {
                listOf<LegendEntry>(
                    LegendEntry().apply {
                        label = "Upper"
                        formColor = ContextCompat.getColor(pContext, R.color.bollinger_band_upper)
                    },
                    LegendEntry().apply {
                        label = "Middle"
                        formColor = ContextCompat.getColor(pContext, R.color.bollinger_band_middle)
                    },
                    LegendEntry().apply {
                        label = "Lower"
                        formColor = ContextCompat.getColor(pContext, R.color.bollinger_band_lower)
                    }
                ).also {
                    legendHashMap[pChartMainIndicator] = it
                }
            }

            ChartMainIndicator.DAILY_BALANCE_TABLE -> {
                listOf<LegendEntry>(
                    LegendEntry().apply {
                        label = "전환 0"
                        formColor = ContextCompat.getColor(pContext, R.color.daily_balance_table_1)
                    },
                    LegendEntry().apply {
                        label = "기준 26"
                        formColor = ContextCompat.getColor(pContext, R.color.daily_balance_table_2)
                    },
                    LegendEntry().apply {
                        label = "후행 26"
                        formColor = ContextCompat.getColor(pContext, R.color.daily_balance_table_3)
                    },
                    LegendEntry().apply {
                        label = "선행1 26"
                        formColor = ContextCompat.getColor(pContext, R.color.daily_balance_table_4)
                    },
                    LegendEntry().apply {
                        label = "선행2 52"
                        formColor = ContextCompat.getColor(pContext, R.color.daily_balance_table_5)
                    }
                ).also {
                    legendHashMap[pChartMainIndicator] = it
                }
            }

            ChartMainIndicator.PIVOT -> {
                listOf<LegendEntry>(
                    LegendEntry().apply {
                        label = "저항2"
                        formColor =
                            ContextCompat.getColor(pContext, R.color.pivot_resistance_line_1)
                    },
                    LegendEntry().apply {
                        label = "저항1"
                        formColor =
                            ContextCompat.getColor(pContext, R.color.pivot_resistance_line_2)
                    },
                    LegendEntry().apply {
                        label = "피봇"
                        formColor = ContextCompat.getColor(pContext, R.color.pivot_base_line)
                    },
                    LegendEntry().apply {
                        label = "지지1"
                        formColor = ContextCompat.getColor(pContext, R.color.pivot_support_line_1)
                    },
                    LegendEntry().apply {
                        label = "지지2"
                        formColor = ContextCompat.getColor(pContext, R.color.pivot_support_line_2)
                    }
                ).also {
                    legendHashMap[pChartMainIndicator] = it
                }
            }

            ChartMainIndicator.ENVELOPES -> {
                listOf<LegendEntry>(
                    LegendEntry().apply {
                        label = "상한선"
                        formColor = ContextCompat.getColor(pContext, R.color.envelopes_upper_limit)
                    },
                    LegendEntry().apply {
                        label = "중심선"
                        formColor = ContextCompat.getColor(pContext, R.color.envelopes_base_line)
                    },
                    LegendEntry().apply {
                        label = "하한선"
                        formColor = ContextCompat.getColor(pContext, R.color.envelopes_lower_bound)
                    },
                    LegendEntry().apply {
                        label = "20, 6"
                        form = Legend.LegendForm.NONE
                    }
                ).also {
                    legendHashMap[pChartMainIndicator] = it
                }
            }

            ChartMainIndicator.PRICE_CHANNELS -> {
                listOf<LegendEntry>(
                    LegendEntry().apply {
                        label = "상한선"
                        formColor =
                            ContextCompat.getColor(pContext, R.color.price_channels_upper_limit)
                    },
                    LegendEntry().apply {
                        label = "중심선"
                        formColor =
                            ContextCompat.getColor(pContext, R.color.price_channels_base_line)
                    },
                    LegendEntry().apply {
                        label = "하한선"
                        formColor =
                            ContextCompat.getColor(pContext, R.color.price_channels_lower_bound)
                    }
                ).also {
                    legendHashMap[pChartMainIndicator] = it
                }
            }
        }
    }

    fun getChartMainIndicator(
        pChartMainIndicator: ChartMainIndicator,
        pCandleEntries: List<CandleEntry>,
        pContext: Context
    ): LineData {
        return when (pChartMainIndicator) {
            ChartMainIndicator.MOVING_AVERAGE -> {
                getMovingAverage(pCandleEntries, pContext)
            }

            ChartMainIndicator.BOLLINGER_BANDS -> {
                getBollingerBands(pCandleEntries, pContext)
            }

            ChartMainIndicator.DAILY_BALANCE_TABLE -> {
                getDailyBalanceTable(pCandleEntries, pContext)
            }

            ChartMainIndicator.PIVOT -> {
                getPivot(pCandleEntries, pContext)
            }

            ChartMainIndicator.ENVELOPES -> {
                getEnvelopes(pCandleEntries, pContext)
            }

            ChartMainIndicator.PRICE_CHANNELS -> {
                getPriceChannels(pCandleEntries, pContext)
            }
        }
    }

    private fun getMovingAverage(pCandleEntries: List<CandleEntry>, pContext: Context): LineData {
        val average5Entries: ArrayList<Entry> = arrayListOf()
        val average10Entries: ArrayList<Entry> = arrayListOf()
        val average20Entries: ArrayList<Entry> = arrayListOf()
        val average60Entries: ArrayList<Entry> = arrayListOf()
        val average120Entries: ArrayList<Entry> = arrayListOf()

        var sum5: Float = 0f
        var sum10: Float = 0f
        var sum20: Float = 0f
        var sum60: Float = 0f
        var sum120: Float = 0f

        for (idx in 1..pCandleEntries.size) {
            val candle = pCandleEntries[idx - 1]
            sum5 += candle.close
            sum10 += candle.close
            sum20 += candle.close
            sum60 += candle.close
            sum120 += candle.close

            if (idx >= 5) {
                average5Entries.add(
                    Entry(
                        candle.x,
                        sum5 / 5f
                    )
                )
                sum5 -= pCandleEntries[idx - 4].close
            }
            if (idx >= 10) {
                average10Entries.add(
                    Entry(
                        candle.x,
                        sum10 / 10f
                    )
                )
                sum10 -= pCandleEntries[idx - 9].close
            }
            if (idx >= 20) {
                average20Entries.add(
                    Entry(
                        candle.x,
                        sum20 / 20f
                    )
                )
                sum20 -= pCandleEntries[idx - 19].close
            }
            if (idx >= 60) {
                average60Entries.add(
                    Entry(
                        candle.x,
                        sum60 / 60f
                    )
                )
                sum60 -= pCandleEntries[idx - 59].close
            }
            if (idx >= 120) {
                average120Entries.add(
                    Entry(
                        candle.x,
                        sum120 / 120f
                    )
                )
                sum120 -= pCandleEntries[idx - 119].close
            }
        }

        val average5DataSet = LineDataSet(average5Entries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.moving_avg_5)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val average10DataSet = LineDataSet(average10Entries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.moving_avg_10)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val average20DataSet = LineDataSet(average20Entries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.moving_avg_20)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val average60DataSet = LineDataSet(average60Entries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.moving_avg_60)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val average120DataSet = LineDataSet(average120Entries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.moving_avg_120)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }

        return LineData().apply {
            addDataSet(average5DataSet)
            addDataSet(average10DataSet)
            addDataSet(average20DataSet)
            addDataSet(average60DataSet)
            addDataSet(average120DataSet)
        }
    }

    private fun getBollingerBands(pCandleEntries: List<CandleEntry>, pContext: Context): LineData {
        val N = 20f
        val K = 2.0f

        val baseLineEntries: ArrayList<Entry> = arrayListOf()
        val upperLimitEntries: ArrayList<Entry> = arrayListOf()
        val lowerBoundEntries: ArrayList<Entry> = arrayListOf()
        var sum: Float = 0f

        for (idx in 1..pCandleEntries.size) {
            val candle = pCandleEntries[idx - 1]
            sum += candle.close

            if (idx >= N) {
                val baseLineValue: Float = sum / N
                baseLineEntries.add(
                    Entry(
                        candle.x,
                        baseLineValue
                    )
                )

                var diffSum: Float = 0f
                for (idx2 in 0 until N.toInt()) {
                    diffSum += (pCandleEntries[idx - 1 - idx2].close - baseLineValue).pow(2f)
                }
                diffSum /= N
                val sigma = sqrt(diffSum)

                upperLimitEntries.add(
                    Entry(
                        candle.x,
                        baseLineValue + sigma * K
                    )
                )
                lowerBoundEntries.add(
                    Entry(
                        candle.x,
                        baseLineValue - sigma * K
                    )
                )

                sum -= pCandleEntries[idx - 1 - (N.toInt() - 1)].close
            }
        }

        val baseLineDataSet = LineDataSet(baseLineEntries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.bollinger_band_middle)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val upperLimitDataSet = LineDataSet(upperLimitEntries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.bollinger_band_upper)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val lowerBoundDataSet = LineDataSet(lowerBoundEntries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.bollinger_band_lower)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }

        return LineData().apply {
            addDataSet(baseLineDataSet)
            addDataSet(upperLimitDataSet)
            addDataSet(lowerBoundDataSet)
        }
    }

    private fun getDailyBalanceTable(
        pCandleEntries: List<CandleEntry>,
        pContext: Context
    ): LineData {
        val DBT_1: Int = 9
        val DBT_2: Int = 26
        val DBT_3: Int = 26
        val DBT_4: Int = 26
        val DBT_5: Int = 52
        // 전환선
        val transitionLineEntries = ArrayList<Entry>()
        // 기준선
        val baselineEntries = ArrayList<Entry>()
        // 후행스팬
        val trailingSpanEntries = ArrayList<Entry>()
        // 선행스팬1
        val leadingSpan1Entries = ArrayList<Entry>()
        // 선행스팬2
        val leadingSpan2Entries = ArrayList<Entry>()
        var maxDuring1: Float
        var minDuring1: Float
        var maxDuring2: Float
        var minDuring2: Float
        var maxDuring5: Float
        var minDuring5: Float
        for (idx in 1..pCandleEntries.size) {
            val candle = pCandleEntries[idx - 1]

            maxDuring1 = 0.0f
            minDuring1 = 9.8765434E8f
            maxDuring2 = 0.0f
            minDuring2 = 9.8765434E8f
            maxDuring5 = 0.0f
            minDuring5 = 9.8765434E8f

            if (idx >= DBT_1) {
                for (i in 0 until DBT_1) {
                    maxDuring1 = max(maxDuring1, pCandleEntries[idx - 1 - i].high)
                    minDuring1 = min(minDuring1, pCandleEntries[idx - 1 - i].low)
                }
                val transitionValue: Float = (maxDuring1 + minDuring1) / 2.0f
                transitionLineEntries.add(
                    Entry(
                        candle.x,
                        transitionValue
                    )
                )
            }
            if (idx >= DBT_2) {
                for (i in 0 until DBT_2) {
                    maxDuring2 = max(maxDuring2, pCandleEntries[idx - 1 - i].high)
                    minDuring2 = min(minDuring2, pCandleEntries[idx - 1 - i].low)
                }
                val baselineValue: Float = (maxDuring2 + minDuring2) / 2.0f
                baselineEntries.add(
                    Entry(
                        candle.x,
                        baselineValue
                    )
                )
            }
            if (idx >= DBT_3) {
                trailingSpanEntries.add(
                    Entry(
                        candle.x - DBT_3.toFloat(),
                        candle.close
                    )
                )
            }
            if (idx >= DBT_4) {
                val transitionValue: Float = (maxDuring1 + minDuring1) / 2.0f
                val baselineValue: Float = (maxDuring2 + minDuring2) / 2.0f
                leadingSpan1Entries.add(
                    Entry(
                        candle.x + 26.0f,
                        (transitionValue + baselineValue) / 2.0f
                    )
                )
            }
            if (idx >= DBT_5) {
                for (i in 0 until DBT_5) {
                    maxDuring5 = max(maxDuring5, pCandleEntries[idx - 1 - i].high)
                    minDuring5 = min(minDuring5, pCandleEntries[idx - 1 - i].low)
                }
                leadingSpan2Entries.add(
                    Entry(
                        candle.x + 26.0f,
                        (maxDuring5 + minDuring5) / 2.0f
                    )
                )
            }
        }

        val transitionDataSet = LineDataSet(transitionLineEntries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.daily_balance_table_1)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val baselineDataSet = LineDataSet(baselineEntries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.daily_balance_table_2)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val trailingSpanDataSet = LineDataSet(trailingSpanEntries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.daily_balance_table_3)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val leadingSpan1DataSet = LineDataSet(leadingSpan1Entries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.daily_balance_table_4)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val leadingSpan2DataSet = LineDataSet(leadingSpan2Entries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.daily_balance_table_5)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }

        return LineData().apply {
            addDataSet(transitionDataSet)
            addDataSet(baselineDataSet)
            addDataSet(trailingSpanDataSet)
            addDataSet(leadingSpan1DataSet)
            addDataSet(leadingSpan2DataSet)
        }
    }

    private fun getPivot(pCandleEntries: List<CandleEntry>, pContext: Context): LineData {
        val pivotBaselineEntries = ArrayList<Entry>()
        val resistanceLine1Entries = ArrayList<Entry>()
        val resistanceLine2Entries = ArrayList<Entry>()
        val supportLine1Entries = ArrayList<Entry>()
        val supportLine2Entries = ArrayList<Entry>()
        for (idx in 1..pCandleEntries.size) {
            val candle = pCandleEntries[idx - 1]
            val pivotValue: Float = (candle.high + candle.low + candle.close) / 3.0f

            pivotBaselineEntries.add(Entry(candle.x, pivotValue))
            resistanceLine1Entries.add(
                Entry(
                    candle.x,
                    2 * pivotValue - candle.low
                )
            )
            resistanceLine2Entries.add(
                Entry(
                    candle.x,
                    pivotValue + candle.high - candle.low
                )
            )
            supportLine1Entries.add(
                Entry(
                    candle.x,
                    2 * pivotValue - candle.high
                )
            )
            supportLine2Entries.add(
                Entry(
                    candle.x,
                    pivotValue - candle.high + candle.low
                )
            )
        }

        val resistanceLine1DataSet = LineDataSet(resistanceLine1Entries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.pivot_resistance_line_1)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val resistanceLine2DataSet = LineDataSet(resistanceLine2Entries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.pivot_resistance_line_2)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val pivotBaselineDataSet = LineDataSet(pivotBaselineEntries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.pivot_base_line)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val supportLine1DataSet = LineDataSet(supportLine1Entries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.pivot_support_line_1)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val supprotLine2DataSet = LineDataSet(supportLine2Entries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.pivot_support_line_2)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }

        return LineData().apply {
            addDataSet(pivotBaselineDataSet)
            addDataSet(resistanceLine1DataSet)
            addDataSet(resistanceLine2DataSet)
            addDataSet(supportLine1DataSet)
            addDataSet(supprotLine2DataSet)
        }
    }

    private fun getEnvelopes(pCandleEntries: List<CandleEntry>, pContext: Context): LineData {
        val ENV_N: Int = 20
        val ENV_K: Int = 6

        val baselineEntries = ArrayList<Entry>()
        val upperLimitEntries = ArrayList<Entry>()
        val lowerBoundEntries = ArrayList<Entry>()

        var sum: Float = 0.0f
        for (idx in 1..pCandleEntries.size) {
            val candle = pCandleEntries[idx - 1]
            sum += candle.close

            if (idx >= ENV_N) {
                val average: Float = sum / ENV_N.toFloat()
                baselineEntries.add(
                    Entry(
                        candle.x,
                        average
                    )
                )
                upperLimitEntries.add(
                    Entry(
                        candle.x,
                        average * (1 + 0.01f * ENV_K)
                    )
                )
                lowerBoundEntries.add(
                    Entry(
                        candle.x,
                        average * (1 - 0.01f * ENV_K)
                    )
                )

                sum -= pCandleEntries[idx - (ENV_N - 1)].close
            }
        }

        val baselineDataSet = LineDataSet(baselineEntries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.envelopes_base_line)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val upperLimitDataSet = LineDataSet(upperLimitEntries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.envelopes_upper_limit)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val lowerBoundDataSet = LineDataSet(lowerBoundEntries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.envelopes_lower_bound)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }

        return LineData().apply {
            addDataSet(baselineDataSet)
            addDataSet(upperLimitDataSet)
            addDataSet(lowerBoundDataSet)
        }
    }

    private fun getPriceChannels(pCandleEntries: List<CandleEntry>, pContext: Context): LineData {
        val PC_N: Int = 5

        val baselineEntries = ArrayList<Entry>()
        val upperLimitEntries = ArrayList<Entry>()
        val lowerBoundEntries = ArrayList<Entry>()

        for (idx in 1..pCandleEntries.size) {
            val candle = pCandleEntries[idx - 1]
            if (idx >= PC_N) {
                var maxDuringN: Float = 0.0f
                var minDuringN: Float = 9.8765434E8f
                for (i in 0 until PC_N) {
                    maxDuringN = max(maxDuringN, pCandleEntries[idx - 1 - i].high)
                    minDuringN = min(minDuringN, pCandleEntries[idx - 1 - 1].low)
                }
                baselineEntries.add(
                    Entry(
                        candle.x,
                        (maxDuringN + minDuringN) / 2.0f
                    )
                )
                upperLimitEntries.add(
                    Entry(
                        candle.x,
                        maxDuringN
                    )
                )
                lowerBoundEntries.add(
                    Entry(
                        candle.x,
                        minDuringN
                    )
                )
            }
        }

        val baselineDataSet = LineDataSet(baselineEntries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.price_channels_base_line)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val upperLimitDataSet = LineDataSet(upperLimitEntries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.price_channels_upper_limit)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val lowerBoundDataSet = LineDataSet(lowerBoundEntries, "").apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(pContext, R.color.price_channels_lower_bound)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }

        return LineData().apply {
            addDataSet(baselineDataSet)
            addDataSet(upperLimitDataSet)
            addDataSet(lowerBoundDataSet)
        }
    }

}