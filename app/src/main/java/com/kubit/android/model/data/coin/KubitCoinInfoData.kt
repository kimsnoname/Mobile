package com.kubit.android.model.data.coin

data class KubitCoinInfoData(
    /**
     * 업비트에서 제공중인 시장 정보 ex) KRW-BTC
     */
    val market: String,
    /**
     * market의 접두사로써, 마켓 코드 ex) KRW
     */
    val marketCode: String,
    /**
     * 거래 대상 암호화폐 한글명
     */
    val nameKor: String,
    /**
     * 거래 대상 암호화폐 영문명
     */
    val nameEng: String,
    /**
     * 유의 종목 여부
     */
    val marketWarning: Boolean = false
) : java.io.Serializable {

    override fun toString(): String {
        return "KubitCoinInfoData{" +
                "market=$market, " +
                "marketCode=$marketCode, " +
                "nameKor=$nameKor, " +
                "nameEng=$nameEng, " +
                "marketWarning=$marketWarning}"
    }

}