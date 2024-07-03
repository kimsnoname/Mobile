package com.kubit.android.base

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kubit.android.R
import com.kubit.android.common.util.VibrateManager

open class BaseFragment : Fragment() {

    protected fun showToastMsg(pMsg: String) {
        Toast.makeText(requireContext(), pMsg, Toast.LENGTH_SHORT).show()
    }

    protected fun showErrorMsg() {
        Toast.makeText(
            requireContext(),
            resources.getText(R.string.toast_msg_error_001),
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * EditText에 포커싱을 요청하는 함수
     */
    protected fun EditText.focus() {
        post {
            requestFocus()
        }
    }

    /**
     * EditText로부터 포커싱을 해제 하는 함수
     *
     * @param isKeyboardHide    소프트 키보드를 숨길지 여부
     */
    protected fun EditText.clearFocus(isKeyboardHide: Boolean) {
        post {
            clearFocus()

            if (isKeyboardHide) {
                val imm: InputMethodManager =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(windowToken, 0)
            }
        }
    }

    protected fun vibrate() {
        VibrateManager.requestVibrate(requireContext(), VibrateManager.VibrationType.TICK)
    }

}