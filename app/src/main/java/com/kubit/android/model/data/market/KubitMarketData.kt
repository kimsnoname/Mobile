package com.kubit.android.model.data.market

import com.kubit.android.model.data.coin.KubitCoinInfoData

class KubitMarketData(
    private val kubitCoinMap: HashMap<String, ArrayList<KubitCoinInfoData>> = hashMapOf()
) : java.io.Serializable {

    /**
     * 마켓 코드에 대응되는 마켓 코드 파라미터를 저장하는 HashMap
     */
    private val marketCode: HashMap<String, String> = hashMapOf()

    val isValid get() = kubitCoinMap.size > 0

    /**
     * Kubit 코인 데이터를 추가하는 함수
     *
     * @param pCoinInfoData     추가할 코인 데이터
     */
    fun addCoin(pCoinInfoData: KubitCoinInfoData) {
        // 이전에 추가한 적이 있는 마켓의 코인인 경우
        if (kubitCoinMap.containsKey(pCoinInfoData.marketCode)) {
            kubitCoinMap[pCoinInfoData.marketCode]?.add(pCoinInfoData)
        }
        // 처음 추가하는 마켓의 코인인 경우
        else {
            kubitCoinMap[pCoinInfoData.marketCode] = arrayListOf(pCoinInfoData)
        }
    }

    /**
     * 임의의 마켓에서 거래 가능한 코인 리스트를 반환하는 함수
     *
     * @param pMarketCode   마켓 코드
     *
     * @return pMarketCode 마켓에서 거래 가능한 코인 리스트
     */
    fun getKubitCoinInfoDataList(pMarketCode: KubitMarketCode): List<KubitCoinInfoData> {
        return when (pMarketCode) {
            KubitMarketCode.KRW -> {
                kubitCoinMap["KRW"] ?: listOf()
            }

            KubitMarketCode.FAVORITE -> {
                listOf()
            }
        }
    }

    override fun toString(): String {
        return "KubitMarketData{" +
                "kubitCoinMap=$kubitCoinMap}"
    }

}