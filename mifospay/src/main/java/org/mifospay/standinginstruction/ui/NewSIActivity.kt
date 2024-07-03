package org.mifospay.standinginstruction.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.base.BaseActivity
import org.mifospay.core.designsystem.theme.MifosTheme

@AndroidEntryPoint
class NewSIActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MifosTheme {
                NewSIScreenRoute(onBackPress = { finish() })
            }
        }
    }
}