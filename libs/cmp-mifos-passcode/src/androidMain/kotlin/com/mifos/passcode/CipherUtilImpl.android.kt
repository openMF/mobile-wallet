package com.mifos.passcode

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricPrompt
import com.mifos.passcode.ICipherUtil
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature

class CipherUtilAndroidImpl: com.mifos.passcode.ICipherUtil {
    private val KEY_NAME = "biometric_key"

    override fun generateKeyPair(): KeyPair {
        val keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(KEY_NAME,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY).run {
            setDigests(KeyProperties.DIGEST_SHA256)
            setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
            build()
        }
        keyPairGenerator.initialize(parameterSpec)
        return keyPairGenerator.genKeyPair()
    }

    override fun getPublicKey(): PublicKey? = getKeyPair()?.public

    private fun getKeyPair(): KeyPair? {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        keyStore?.getCertificate(KEY_NAME).let { return KeyPair(it?.publicKey, null) }
    }

    override fun getCrypto(): Crypto {
        val signature = Signature.getInstance("SHA256withRSA")
        val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val key: PrivateKey = if(keyStore.containsAlias(KEY_NAME))
            keyStore.getKey(KEY_NAME, null) as PrivateKey
        else
            generateKeyPair().private
        signature.initSign(key)
        return BiometricPrompt.CryptoObject(signature)
    }

    override suspend fun removePublicKey() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        keyStore?.deleteEntry(KEY_NAME)
    }

}

actual typealias CommonKeyPair = KeyPair

actual typealias CommonPublicKey = PublicKey

actual typealias Crypto = BiometricPrompt.CryptoObject