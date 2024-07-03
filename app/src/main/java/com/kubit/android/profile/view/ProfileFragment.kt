package com.kubit.android.profile.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.kubit.android.R
import com.kubit.android.base.BaseFragment
import com.kubit.android.coinlist.view.CoinListFragment
import com.kubit.android.common.dialog.MessageDialog
import com.kubit.android.common.session.KubitSession
import com.kubit.android.common.util.DLog
import com.kubit.android.databinding.FragmentProfileBinding
import com.kubit.android.login.view.LoginActivity
import com.kubit.android.main.viewmodel.MainViewModel

class ProfileFragment : BaseFragment() {

    private val model: MainViewModel by activityViewModels()
    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding get() = _binding!!


    private var _loginIntentForResult: ActivityResultLauncher<Intent>? = null
    private val loginIntentForResult: ActivityResultLauncher<Intent> get() = _loginIntentForResult!!

    // region Fragment LifeCycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _loginIntentForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        applyLoginSession()
                        model.requestWalletOverall()
                    }

                    else -> {
                    }
                }
            }

        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        setObserver()
        init()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        loginIntentForResult.unregister()
        _loginIntentForResult = null
    }
    // endregion Fragment LifeCycle

    private fun setObserver() {
        model.resetResult.observe(viewLifecycleOwner, Observer { resetResult ->
            if (resetResult != null) {
                model.setProgressFlag(false)
                showToastMsg(getString(R.string.toast_msg_successful_reset))
                model.clearResetResult()
            }
        })
    }

    private fun init() {
        applyLoginSession()

        binding.apply {
            tvProfileLogin.setOnClickListener {
                // 로그인한 경우
                if (KubitSession.isLogin()) {
                    showLogoutDialog()
                }
                // 로그아웃한 경우
                else {
                    val loginIntent = Intent(requireContext(), LoginActivity::class.java)
                    loginIntentForResult.launch(loginIntent)
                }
            }
            tvProfileUserGuide.setOnClickListener {
                // TODO: 사용자 이용가이드 기능 추가
                showToastMsg("서비스 개봉박두")
            }
            tvProfileReset.setOnClickListener {
                // 로그인한 경우
                if (KubitSession.isLogin()) {
                    showResetDialog()
                }
                // 로그아웃한 경우
                else {
                    val loginIntent = Intent(requireContext(), LoginActivity::class.java)
                    loginIntentForResult.launch(loginIntent)
                }
            }
        }
    }

    private fun applyLoginSession() {
        binding.apply {
            tvProfileUserName.text =
                if (KubitSession.isLogin()) KubitSession.userName else getString(R.string.profile_requestLogin)
            tvProfileLogin.text = getString(
                if (KubitSession.isLogin()) R.string.profile_logout else R.string.profile_login
            )
        }
    }

    // region Dialog
    private fun showLogoutDialog() {
        for (fragment in childFragmentManager.fragments) {
            if (fragment is MessageDialog) {
                return
            }
        }

        MessageDialog(
            pMsg = getString(R.string.dialog_msg_003),
            pLeftBtnText = "로그아웃",
            pLeftBtnClickListener = {
                model.requestLogout()
                showToastMsg(getString(R.string.toast_msg_logout_success))
                applyLoginSession()
            },
            pRightBtnText = "취소",
            pRightBtnClickListener = {

            }
        ).show(childFragmentManager, MessageDialog.TAG)
    }

    private fun showResetDialog() {
        for (fragment in childFragmentManager.fragments) {
            if (fragment is MessageDialog) {
                return
            }
        }

        MessageDialog(
            pMsg = getString(R.string.dialog_msg_004),
            pLeftBtnText = "초기화",
            pLeftBtnClickListener = {
                model.requestReset()
            },
            pRightBtnText = "취소",
            pRightBtnClickListener = {

            }
        ).show(childFragmentManager, MessageDialog.TAG)
    }
    // endregion Dialog

    companion object {
        const val TAG: String = "ProfileFragment"

        private var instance: ProfileFragment? = null

        @JvmStatic
        fun getInstance(): ProfileFragment {
            if (instance == null) {
                instance = ProfileFragment()
            }

            return instance!!
        }

        @JvmStatic
        fun clearInstance() {
            instance = null
        }
    }
}