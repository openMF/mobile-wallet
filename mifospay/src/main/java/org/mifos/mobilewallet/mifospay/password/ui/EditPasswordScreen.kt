package org.mifos.mobilewallet.mifospay.password.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MfPasswordTextField
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosTopBar
import org.mifos.mobilewallet.mifospay.password.presenter.EditPasswordUiState
import org.mifos.mobilewallet.mifospay.password.presenter.EditPasswordViewModel

@Composable
fun EditPasswordScreen(
    viewModel: EditPasswordViewModel = hiltViewModel(),
    onCancelChanges: () -> Unit
) {
    val context = LocalContext.current
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmNewPassword by rememberSaveable { mutableStateOf("") }
    var isConfirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isNewPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isConfirmNewPasswordVisible by rememberSaveable { mutableStateOf(false) }

    val editPasswordUiState by viewModel.editPasswordUiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        MifosTopBar(topBarTitle = R.string.change_password, backPress = {})
        MfPasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            password = confirmPassword,
            label = stringResource(id = R.string.confirm_password),
            isError = false,
            isPasswordVisible = isConfirmPasswordVisible,
            onTogglePasswordVisibility = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
            onPasswordChange = { confirmPassword = it }
        )
        MfPasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            password = newPassword,
            label = stringResource(id = R.string.new_password),
            isError = newPassword.isEmpty() || newPassword.length < 6,
            isPasswordVisible = isNewPasswordVisible,
            onTogglePasswordVisibility = { isNewPasswordVisible = !isNewPasswordVisible },
            onPasswordChange = { newPassword = it }
        )
        MfPasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            password = confirmNewPassword,
            label = stringResource(id = R.string.confirm_new_password),
            isError = confirmNewPassword.isEmpty() || newPassword != confirmNewPassword,
            isPasswordVisible = isConfirmNewPasswordVisible,
            onTogglePasswordVisibility = {
                isConfirmNewPasswordVisible = !isConfirmNewPasswordVisible
            },
            onPasswordChange = { confirmNewPassword = it }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .padding(all = 8.dp),
                onClick = { onCancelChanges.invoke() },
                colors = ButtonDefaults.buttonColors(Color.Black),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(text = stringResource(id = R.string.cancel), color = Color.White)
            }
            Button(
                modifier = Modifier
                    .weight(1f)
                    .padding(all = 8.dp),
                onClick = {
                    viewModel.updatePassword(
                        confirmPassword,
                        newPassword,
                        confirmNewPassword
                    )
                },
                colors = ButtonDefaults.buttonColors(Color.Black),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(text = stringResource(id = R.string.save), color = Color.White)
            }
        }
    }
    when (editPasswordUiState) {
        is EditPasswordUiState.Error -> {
            Toast.makeText(context, (editPasswordUiState as EditPasswordUiState.Error).message,Toast.LENGTH_SHORT).show()
        }
        EditPasswordUiState.Loading -> {}
        EditPasswordUiState.Success -> {}
    }
}

@Preview(showBackground = true)
@Composable
private fun EditPasswordScreenPreview() {
    EditPasswordScreen(hiltViewModel(), {})
}