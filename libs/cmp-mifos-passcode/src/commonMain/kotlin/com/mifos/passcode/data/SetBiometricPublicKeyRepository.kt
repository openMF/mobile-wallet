package com.mifos.passcode.data

import kotlinx.coroutines.delay

private var publicKeyOnServer = ""
class SetBiometricPublicKeyRepository {
    suspend fun set(publicKey: String) {
        delay(500)
        publicKeyOnServer = publicKey
    }
}

class VerifyBiometric {
    suspend fun verify(signedUserId: String): Result<Unit> {
        return Result.success(Unit)
    }
}