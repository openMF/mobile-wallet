package com.mifos.passcode

import com.mifos.passcode.Platform

class AndroidPlatform : com.mifos.passcode.Platform {
//    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
    override val name: String = "Android"
}

actual fun getPlatform(): com.mifos.passcode.Platform = AndroidPlatform()