package com.mifos.passcode

import com.mifos.passcode.Platform

class Greeting {
    private val platform: com.mifos.passcode.Platform = com.mifos.passcode.getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}