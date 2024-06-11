package org.mifospay.invoice.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.base.BaseActivity
import org.mifospay.theme.MifosTheme

@AndroidEntryPoint
class InvoiceActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent.data
        setContent {
            MifosTheme {
                InvoiceDetailScreen(data = data,onBackPress = { finish() })
            }
        }
    }
}