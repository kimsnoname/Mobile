package com.kubit.android.model.data.login

enum class LoginResult {
    /**
     * 로그인 성공
     */
    SUCCESS,

    /**
     * 로그인 실패 -> ID 및 PW 확인 요청
     */
    FAIL,

    /**
     * 에러
     */
    ERROR
}