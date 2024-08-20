package org.mifospay.shared

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.mifospay.shared.di.initKoin

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MyApplication)
        }
    }
}