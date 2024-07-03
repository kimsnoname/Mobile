package com.kubit.android.common.util

import android.content.Context
import android.util.TypedValue
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.abs

object ConvertUtil {

    private val priceFormatterOver100 = DecimalFormat("###,###,###").apply {
        roundingMode = RoundingMode.DOWN
    }
    private val priceFormatterUnder100 = DecimalFormat("#0.00######").apply {
        roundingMode = RoundingMode.DOWN
    }

    private val changeRateFormatter = DecimalFormat("#0.00").apply {
        roundingMode = RoundingMode.DOWN
    }

    private val orderSizeFormatter = DecimalFormat("###,###,##0.000").apply {
        roundingMode = RoundingMode.DOWN
    }

    private val coinQuantityFormatter = DecimalFormat("###,###,###,##0.########").apply {
        roundingMode = RoundingMode.DOWN
    }

    private val pieChartLabelFormatter = DecimalFormat("#0.0").apply {
        roundingMode = RoundingMode.DOWN
    }

    fun dp2px(context: Context, dp: Int): Int = dp2px(context, dp.toFloat()).toInt()

    fun dp2px(context: Context, dp: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)

    fun px2dp(context: Context, px: Int): Int = px2dp(context, px.toFloat()).toInt()

    fun px2dp(context: Context, px: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, context.resources.displayMetrics)


    /**
     * double 타입의 가격을 문자열로 변환하는 함수
     *
     * @param pTradePrice   가격
     */
    fun tradePrice2string(pTradePrice: Double): String {
        return if (abs(pTradePrice) < 100) {
            priceFormatterUnder100.format(pTradePrice)
        } else {
            priceFormatterOver100.format(pTradePrice)
        }
    }

    /**
     * double 타입의 가격을 문자열로 변환하는 함수
     *
     * @param pTradePrice       가격
     * @param pWithDecimalPoint 소수점 표시 여부
     */
    fun tradePrice2string(pTradePrice: Double, pWithDecimalPoint: Boolean): String {
        return if (pWithDecimalPoint) {
            priceFormatterUnder100.format(pTradePrice)
        } else {
            priceFormatterOver100.format(pTradePrice)
        }
    }

    /**
     * double 타입의 부호가 있는 변화율을 문자열로 변환하는 함수
     *
     * @param pSignedChangeRate 부호가 있는 변화율
     */
    fun changeRate2string(pSignedChangeRate: Double): String {
        return "${changeRateFormatter.format(pSignedChangeRate * 100)}%"
    }

    /**
     * 24시간 누적 거래대금을 문자열로 변환하는 함수
     *
     * @param pAccTradePrice24H 24시간 누적 거래대금
     */
    fun accTradePrice24H2string(pAccTradePrice24H: Double): String {
        val volumeUnitMillion = pAccTradePrice24H.div(1000000).toInt()
        return if (volumeUnitMillion < 100) {
            "${priceFormatterUnder100.format(volumeUnitMillion)}백만"
        } else {
            "${priceFormatterOver100.format(volumeUnitMillion)}백만"
        }
    }

    /**
     * 호가 매물량을 문자열로 변환하는 함수
     *
     * @param pOrderSize    호가 매물량
     */
    fun orderSize2string(pOrderSize: Double): String {
        return orderSizeFormatter.format(pOrderSize)
    }

    /**
     * 코인 보유 수량을 문자열로 변환하는 함수
     *
     * @param pQuantity 코인 보유 수량
     */
    fun coinQuantity2string(pQuantity: Double): String {
        return coinQuantityFormatter.format(pQuantity)
    }

    /**
     * 코인 보유 수량을 문자열로 변환하는 함수
     *
     * @param pQuantity     코인 보유 수량
     * @param pMarketCode   종목 구분 코드
     */
    fun coinQuantity2string(pQuantity: Double, pMarket: String): String {
        return "${coinQuantityFormatter.format(pQuantity)} ${
            pMarket.split('-').getOrNull(1) ?: ""
        }".trim()
    }

    /**
     * 투자내역 화면의 보유자산 포트폴리오 파이 차트 라벨을 만들 때 사용함
     *
     * @param pRatio    코인의 보유 비중
     */
    fun ratio2pieChartLabel(pRatio: Double): String {
        return pieChartLabelFormatter.format(pRatio * 100)
    }

    /**
     * 금액을 "${금액} KRW" 형태의 문자열로 변환하는 함수
     *
     * @param pPrice    금액
     * @param pWithKRW  KRW 단위를 붙일지 여부
     */
    fun price2krwString(pPrice: Double, pWithKRW: Boolean): String {
        return if (pWithKRW) {
            "${priceFormatterOver100.format(pPrice)} KRW"
        } else {
            priceFormatterOver100.format(pPrice)
        }
    }

}