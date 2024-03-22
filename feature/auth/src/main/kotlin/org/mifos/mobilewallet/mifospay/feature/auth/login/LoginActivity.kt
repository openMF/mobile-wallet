package org.mifos.mobilewallet.mifospay.feature.auth.login

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.designsystem.theme.MifosTheme

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MifosTheme {
                LoginScreen()
            }
        }
    }
}
