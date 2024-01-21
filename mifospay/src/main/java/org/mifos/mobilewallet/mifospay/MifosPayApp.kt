package org.mifos.mobilewallet.mifospay

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import butterknife.ButterKnife
import com.mifos.mobile.passcode.utils.ForegroundChecker
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by naman on 17/8/17.
 */
@HiltAndroidApp
class MifosPayApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (instance == null) {
            instance = this
        }
        ButterKnife.setDebug(true)

        //Initialize ForegroundChecker
        ForegroundChecker.init(this)
    }

    companion object {
        private var instance: MifosPayApp? = null

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }

        operator fun get(context: Context): MifosPayApp {
            return context.applicationContext as MifosPayApp
        }

        @JvmStatic
        val context: Context?
            get() = instance
    }
}
