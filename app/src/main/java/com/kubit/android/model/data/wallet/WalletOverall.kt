package com.kubit.android.model.data.wallet

data class WalletOverall(
    /**
     * 보유 원화 금액
     */
    val KRW: Double,
    /**
     * 보유 자산 리스트
     */
    val walletList: List<WalletData>
) {

    override fun toString(): String {
        return "$TAG{" +
                "KRW=$KRW, " +
                "walletList=$walletList}"
    }

    companion object{
        private const val TAG: String = "WalletOverall"
    }

}
