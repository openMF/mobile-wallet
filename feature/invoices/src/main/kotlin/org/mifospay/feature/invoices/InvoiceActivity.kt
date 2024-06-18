package org.mifospay.feature.invoices

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.core.designsystem.theme.MifosTheme


@AndroidEntryPoint
class InvoiceActivity : AppCompatActivity() {
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