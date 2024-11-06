/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.qr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

@Composable
actual fun rememberCameraPermissionState(): CameraPermissionState {
    return remember {
        IosMutableCameraPermissionState()
    }
}

/**
 * Original source: https://github.com/kalinjul/EasyQRScan
 */
abstract class MutableCameraPermissionState : CameraPermissionState {
    override var status: CameraPermissionStatus by mutableStateOf(getCameraPermissionStatus())
}

/**
 * Original source: https://github.com/kalinjul/EasyQRScan
 */
class IosMutableCameraPermissionState : MutableCameraPermissionState() {
    override fun requestCameraPermission() {
        AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) {
            this.status = getCameraPermissionStatus()
        }
    }

    override fun goToSettings() {
        val appSettingsUrl = NSURL(string = UIApplicationOpenSettingsURLString)
        if (UIApplication.sharedApplication.canOpenURL(appSettingsUrl)) {
            UIApplication.sharedApplication.openURL(appSettingsUrl)
        }
    }
}

/**
 * Original source: https://github.com/kalinjul/EasyQRScan
 */
fun getCameraPermissionStatus(): CameraPermissionStatus {
    val authorizationStatus = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
    return if (authorizationStatus == AVAuthorizationStatusAuthorized) {
        CameraPermissionStatus.Granted
    } else {
        CameraPermissionStatus.Denied
    }
}
