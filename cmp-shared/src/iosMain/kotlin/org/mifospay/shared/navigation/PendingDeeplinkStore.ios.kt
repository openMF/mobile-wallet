package org.mifospay.shared.navigation

actual object PendingDeepLinkStore {
    actual fun store(uri: String) {
    }

    actual fun consume(): String? {
        TODO("Not yet implemented")
    }

    actual fun clear() {
    }
}