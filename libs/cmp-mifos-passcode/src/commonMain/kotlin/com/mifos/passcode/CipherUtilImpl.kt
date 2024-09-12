package com.mifos.passcode

interface ICipherUtil {
    @Throws(Exception::class)
    fun generateKeyPair(): com.mifos.passcode.CommonKeyPair

    fun getPublicKey(): com.mifos.passcode.CommonPublicKey?

    @Throws(Exception::class)
    fun getCrypto(): com.mifos.passcode.Crypto

    @Throws(Exception::class)
    suspend fun removePublicKey()
}

expect class CommonKeyPair
expect interface CommonPublicKey
expect class Crypto