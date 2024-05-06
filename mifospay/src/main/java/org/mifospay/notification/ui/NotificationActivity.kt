package org.mifospay.notification.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.base.BaseActivity
import org.mifospay.theme.MifosTheme

/**
 * This activity is to view notifications record.
 * Notifications Datatable is populated automatically by server when an event happens.
 * This feature is yet to be implemented on the server side.
 */
@AndroidEntryPoint
class NotificationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MifosTheme {
                NotificationScreen()
            }
        }
    }
}