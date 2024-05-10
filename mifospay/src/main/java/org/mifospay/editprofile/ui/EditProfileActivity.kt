package org.mifospay.editprofile.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.base.BaseActivity
import org.mifospay.theme.MifosTheme

@AndroidEntryPoint
class EditProfileActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MifosTheme {
                EditProfileScreenRoute(
                    onSaveChanges = {
                        // TODO : save locally or send it to backend
                    },
                    onBackClick = { finish() }
                )
            }
        }
    }
}