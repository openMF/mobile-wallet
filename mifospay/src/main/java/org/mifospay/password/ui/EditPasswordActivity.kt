package org.mifospay.password.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.core.designsystem.theme.MifosTheme

@AndroidEntryPoint
class EditPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MifosTheme {
                EditPasswordScreen(
                    onCancelChanges = { finish() }
                )
            }
        }
    }
}
