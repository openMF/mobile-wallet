package org.mifos.mobilewallet.mifospay.editprofile.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.theme.MifosTheme

@AndroidEntryPoint
class EditProfileActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MifosTheme {
                EditProfileScreen(
                    onSaveChanges = {
                        // TODO : save locally or send it to backend
                    }
                )
            }
        }
    }
}