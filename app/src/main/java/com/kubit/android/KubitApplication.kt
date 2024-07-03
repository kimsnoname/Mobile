package com.kubit.android

import android.app.Application
import com.kubit.android.common.session.KubitSession

class KubitApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        KubitSession.init(this)
    }

}