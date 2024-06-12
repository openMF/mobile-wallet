package org.mifospay.feature.faq

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.core.designsystem.theme.MifosTheme

@AndroidEntryPoint
class FAQActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MifosTheme {
                FaqScreenRoute(
                    navigateBack = { onBackPressedDispatcher.onBackPressed() }
                )
            }
        }
    }
}