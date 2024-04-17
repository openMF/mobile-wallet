package org.mifospay.bank.link_bank

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.theme.MifosTheme


@AndroidEntryPoint
class LinkBankAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MifosTheme {
                LinkBankAccountRoute(
                    onBackClick = { finish() }
                )
            }
        }
    }
}

