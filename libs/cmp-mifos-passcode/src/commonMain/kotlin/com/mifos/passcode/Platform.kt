package com.mifos.passcode

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform