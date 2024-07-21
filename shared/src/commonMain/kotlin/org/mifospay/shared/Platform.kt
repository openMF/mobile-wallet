package org.mifospay.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform