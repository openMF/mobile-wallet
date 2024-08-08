package org.mifospay.common.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.base.BaseActivity
import org.mifospay.core.designsystem.theme.MifosTheme

/**
 * Created by naman on 21/8/17.
 */
@AndroidEntryPoint
class SearchActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MifosTheme {
                SearchScreenRoute(
                    onBackClick = { finish() }
                )
            }
        }
    }
}