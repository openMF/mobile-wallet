package com.mifos.passcode

class DesktopPlatform: com.mifos.passcode.Platform {
    //    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val name: String = "Desktop"
}

actual fun getPlatform(): Platform = DesktopPlatform()