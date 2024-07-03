package com.kubit.android.base

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kubit.android.R
import com.kubit.android.common.dialog.ProgressDialog
import com.kubit.android.common.util.VibrateManager

open class BaseActivity : AppCompatActivity() {

    private lateinit var mProgressDialog: ProgressDialog

    protected fun setFragment(
        frameLayoutId: Int,
        fragment: Fragment,
        tag: String? = null
    ) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(frameLayoutId, fragment, tag)
            .commit()
    }

    protected fun showProgress(pMsg: String = "") {
        if (!this::mProgressDialog.isInitialized) {
            mProgressDialog = ProgressDialog(this, "")
        }

        mProgressDialog.setMessage(pMsg)
        mProgressDialog.show()
    }

    protected fun dismissProgress() {
        if (this::mProgressDialog.isInitialized) {
            mProgressDialog.dismiss()
        }
    }

    protected fun showToastMsg(pMsg: String) {
        Toast.makeText(this, pMsg, Toast.LENGTH_SHORT).show()
    }

    protected fun showErrorMsg() {
        Toast.makeText(
            this,
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
        VibrateManager.requestVibrate(this, VibrateManager.VibrationType.TICK)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus

            if (view is EditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)

                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.post {
                        view.clearFocus()
                        val imm: InputMethodManager =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

}