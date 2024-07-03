package com.kubit.android.model.data.coin

/**
 * 임의의 시점에 캡쳐한 코인 스냅샷 데이터
 */
data class CoinSnapshotData(
    /**
     * 종목 구분 코드
     */
    val market: String,
    /**
     * market의 접두사, ex) KRW-BTC -> KRW
     */
    val marketCode: String,
    /**
     * 코인 한글명
     */
    val nameKor: String,
    /**
     * 코인 영문명
     */
    val nameEng: String,
    /**
     * 최근 거래 일자(UTC), yyyyMMdd
     */
    val tradeDate: String,
    /**
     * 최근 거래 시각(UTC), HHmmss
     */
    val tradeTime: String,
    /**
     * 최근 거래 일자(KST), yyyyMMdd
     */
    val tradeDateKST: String,
    /**
     * 최근 거래 시각(KST), HHmmss
     */
    val tradeTimeKST: String,
    /**
     * 최근 거래 일시(UTC), Unix Timestamp
     */
    val tradeTimeStamp: Long,
    /**
     * 시가
     */
    val openingPrice: Double,
    /**
     * 고가
     */
    val highPrice: Double,
    /**
     * 저가
     */
    val lowPrice: Double,
    /**
     * 종가(현재가)
     */
    val tradePrice: Double,
    /**
     * 전일 종가(UTC 0시 기준)
     */
    val prevClosingPrice: Double,
    /**
     * 코인 가격 변화 타입
     *
     * @see PriceChangeType
     */
    val change: PriceChangeType,
    /**
     * 전일종가 대비 변화액의 절대값
     */
    val changePrice: Double,
    /**
     * 전일종가 대비 변화율의 절대값
     */
    val changeRate: Double,
    /**
     * 전일종가 대비 부호가 있는 변화액
     */
    val signedChangePrice: Double,
    /**
     * 전일종가 대비 부호가 있는 변화율
     */
    val signedChangeRate: Double,
    /**
     * 가장 최근 거래량
     */
    val tradeVolume: Double,
    /**
     * 누적 거래대금(UTC 0시 기준)
     */
    val accTradePrice: Double,
    /**
     * 24시간 누적 거래대금
     */
    val accTradePrice24H: Double,
    /**
     * 누적 거래량(UTC 0시 기준)
     */
    val accTradeVolume: Double,
    /**
     * 24시간 누적 거래량
     */
    val accTradeVolume24H: Double,
    /**
     * 52주 신고가
     */
    val highest52WeekPrice: Double,
    /**
     * 52주 신고가 달성일, yyyy-MM-dd
     */
    val highest52WeekDate: String,
    /**
     * 52주 신저가
     */
    val lowest52WeekPrice: Double,
    /**
     * 52주 신저가 달성일, yyyy-MM-dd
     */
    val lowest52WeekDate: String,
    /**
     * 타임스탬프
     */
    val timestamp: Long,
    /**
     * 검색어와 비교할 문자열
     */
    private val containQuery: String = nameKor + nameEng.lowercase() + market.lowercase()
) {

    fun contain(pQuery: String): Boolean = containQuery.contains(pQuery)

    override fun toString(): String {
        return "$TAG{" +
                "market=$market, " +
                "nameKor=$nameKor, " +
                "nameEng=$nameEng, " +
                "tradePrice=$tradePrice, " +
                "timestamp=$timestamp}"
    }

    companion object {
        private const val TAG: String = "CoinSnapshotData"

        fun getDefaultData(): CoinSnapshotData = CoinSnapshotData(
            market = "KRW-BTC",
            marketCode = "KRW",
            nameKor = "비트코인",
            nameEng = "BTC/KRW",
            tradeDate = "20230522",
            tradeTime = "",
            tradeDateKST = "",
            tradeTimeKST = "",
            tradeTimeStamp = 0,
            openingPrice = 0.0,
            highPrice = 0.0,
            lowPrice = 0.0,
            tradePrice = 0.0,
            prevClosingPrice = 0.0,
            change = PriceChangeType.EVEN,
            changePrice = 0.0,
            changeRate = 0.0,
            signedChangePrice = 0.0,
            signedChangeRate = 0.0,
            tradeVolume = 0.0,
            accTradePrice = 0.0,
            accTradePrice24H = 0.0,
            accTradeVolume = 0.0,
            accTradeVolume24H = 0.0,
            highest52WeekPrice = 0.0,
            highest52WeekDate = "",
            lowest52WeekPrice = 0.0,
            lowest52WeekDate = "",
            timestamp = 0
        )
    }

}