/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.authenticator.biometrics.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mobile_wallet.feature.authenticator_biometrics.generated.resources.Res
import mobile_wallet.feature.authenticator_biometrics.generated.resources.eye_scanner
import mobile_wallet.feature.authenticator_biometrics.generated.resources.face_scan
import mobile_wallet.feature.authenticator_biometrics.generated.resources.fingerprint
import mobile_wallet.feature.authenticator_biometrics.generated.resources.keypad
import org.jetbrains.compose.resources.painterResource
import org.mifos.authenticator.biometrics.Platform
import org.mifos.authenticator.biometrics.getPlatform
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthOptions
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticatorStatus

@Composable
fun SystemAuthenticatorButton(
    onClick: () -> Unit,
    platformAuthOptions: List<PlatformAuthOptions> = listOf(PlatformAuthOptions.UserCredential),
    authenticatorStatus: Set<PlatformAuthenticatorStatus>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        when (getPlatform()) {
            Platform.ANDROID -> {
                if (authenticatorStatus.contains(PlatformAuthenticatorStatus.BIOMETRICS_SET)) {
                    if (
                        platformAuthOptions.contains(PlatformAuthOptions.Iris) ||
                        (
                            platformAuthOptions.contains(PlatformAuthOptions.FaceId) &&
                                platformAuthOptions.contains(PlatformAuthOptions.Fingerprint)
                            )
                    ) {
                        AuthenticateButton(
                            onClick = onClick,
                            text = "Use Biometrics",
                        )
                    } else if (platformAuthOptions.contains(PlatformAuthOptions.Fingerprint)) {
                        Image(
                            painter = painterResource(Res.drawable.fingerprint),
                            contentDescription = "Fingerprint icon",
                            modifier = Modifier.size(50.dp)
                                .clickable { onClick() },
                        )
                    } else if (platformAuthOptions.contains(PlatformAuthOptions.FaceId)) {
                        Image(
                            painter = painterResource(Res.drawable.face_scan),
                            contentDescription = "Fingerprint icon",
                            modifier = Modifier.size(50.dp)
                                .clickable { onClick() },
                        )
                    } else if (platformAuthOptions.contains(PlatformAuthOptions.Iris)) {
                        Image(
                            painter = painterResource(Res.drawable.eye_scanner),
                            contentDescription = "Fingerprint icon",
                            modifier = Modifier.size(50.dp)
                                .clickable { onClick() },
                        )
                    } else if (platformAuthOptions.contains(PlatformAuthOptions.UserCredential)) {
                        Image(
                            painter = painterResource(Res.drawable.keypad),
                            contentDescription = "Fingerprint icon",
                            modifier = Modifier.size(50.dp)
                                .clickable { onClick() },
                        )
                    }
                } else if (authenticatorStatus.contains(PlatformAuthenticatorStatus.DEVICE_CREDENTIAL_SET)) {
                    Image(
                        painter = painterResource(Res.drawable.keypad),
                        contentDescription = "Fingerprint icon",
                        modifier = Modifier.size(50.dp)
                            .clickable { onClick() },
                    )
                } else {
                    Text("Set up Authentication Option")
                }
            }
            Platform.IOS -> {
                if (authenticatorStatus.contains(PlatformAuthenticatorStatus.BIOMETRICS_SET)) {
                    Image(
                        painter = painterResource(Res.drawable.face_scan),
                        contentDescription = "Fingerprint icon",
                        modifier = Modifier.size(40.dp)
                            .clickable { onClick() },
                    )
                } else if (authenticatorStatus.contains(PlatformAuthenticatorStatus.DEVICE_CREDENTIAL_SET)) {
                    Image(
                        painter = painterResource(Res.drawable.keypad),
                        contentDescription = "Fingerprint icon",
                        modifier = Modifier.size(40.dp)
                            .clickable { onClick() },
                    )
                } else {
                    Text("Set up Authentication Option")
                }
            }
            Platform.JVM -> {
                if (
                    authenticatorStatus.contains(PlatformAuthenticatorStatus.BIOMETRICS_SET) ||
                    authenticatorStatus.contains(PlatformAuthenticatorStatus.DEVICE_CREDENTIAL_SET)
                ) {
                    AuthenticateButton(
                        onClick = onClick,
                        text = "Authenticate using Windows Hello",
                    )
                } else {
                    Text("Unsupported platform")
                }
            }
            Platform.JS -> {
                Text("Unsupported platform")
            }
            Platform.WASMJS -> {
                Text("Unsupported platform")
            }
        }
    }
}

@Composable
fun AuthenticateButton(
    onClick: () -> Unit,
    text: String,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(50.dp)
            .width(250.dp)
            .clip(RoundedCornerShape(50)),
    ) {
        Text(text)
    }
}
