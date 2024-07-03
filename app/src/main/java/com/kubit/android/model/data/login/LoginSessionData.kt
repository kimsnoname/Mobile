package com.kubit.android.model.data.login

data class LoginSessionData(
    val userName: String,
    val grantType: String,
    val accessToken: String,
    val refreshToken: String
) {

    override fun toString(): String {
        return "$TAG{" +
                "userName=$userName, " +
                "grantType=$grantType, " +
                "accessToken=$accessToken, " +
                "refreshToken=$refreshToken}"
    }

    companion object {
        private const val TAG: String = "LoginSessionData"
    }

}