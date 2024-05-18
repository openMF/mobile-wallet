package org.mifospay.password.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifos.mobilewallet.mifospay.password.presenter.EditPasswordUiState
import org.mifos.mobilewallet.mifospay.password.presenter.EditPasswordViewModel
import org.mifospay.R
import org.mifospay.core.designsystem.component.MfPasswordTextField
import org.mifospay.core.designsystem.component.MifosTopBar

@Composable
fun EditPasswordScreen(
    viewModel: EditPasswordViewModel = hiltViewModel(),
    onCancelChanges: () -> Unit
) {
    val context = LocalContext.current
    var currentPassword by rememberSaveable { mutableStateOf("") }
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
            password = currentPassword,
            label = stringResource(R.string.current_password),
            isError = false,
            isPasswordVisible = isConfirmPasswordVisible,
            onTogglePasswordVisibility = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
            onPasswordChange = { currentPassword = it }
        )
        MfPasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            password = newPassword,
            label = stringResource(id = R.string.new_password),
            isError = if(newPassword.isNotEmpty() && newPassword.length < 6) true else false,
            errorMessage = (if(newPassword.isNotEmpty() && newPassword.length < 6) stringResource(id = R.string.password_length_error) else null),
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
            isError = if(newPassword != confirmNewPassword && confirmNewPassword.isNotEmpty()) true else false,
            errorMessage = (if(newPassword != confirmNewPassword && confirmNewPassword.isNotEmpty()) stringResource(id = R.string.password_mismatch_error) else null),
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
                        currentPassword,
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
            Toast.makeText(context, (editPasswordUiState as EditPasswordUiState.Error).message, Toast.LENGTH_SHORT).show()
        }
        EditPasswordUiState.Loading -> {

        }
        EditPasswordUiState.Success -> {

        }

        else -> {}
    }
}
