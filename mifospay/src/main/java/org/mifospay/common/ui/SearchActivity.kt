package org.mifospay.common.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.R
import org.mifospay.base.BaseActivity
import org.mifospay.theme.MifosTheme

/**
 * Created by naman on 21/8/17.
 */
@AndroidEntryPoint
class SearchActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setContent {
            MifosTheme {
                SearchScreenRoute(
                    onBackClick = { finish() }
                )
            }
        }
    }
}