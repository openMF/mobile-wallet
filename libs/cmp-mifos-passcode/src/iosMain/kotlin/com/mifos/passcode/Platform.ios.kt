package com.mifos.passcode

class IOSPlatform: com.mifos.passcode.Platform {
//    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val name: String = "Ios"
}

actual fun getPlatform(): com.mifos.passcode.Platform = IOSPlatform()