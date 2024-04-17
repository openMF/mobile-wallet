package org.mifospay.faq.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.base.BaseActivity
import org.mifospay.theme.MifosTheme

/**
 * This class is the UI component of the Architecture.
 *
 * @author ankur
 * @since 11/July/2018
 */


@AndroidEntryPoint
class FAQActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MifosTheme {
                FaqScreen(
                    navigateBack = { onBackPressedDispatcher.onBackPressed() }
                )
            }
        }
    }
}