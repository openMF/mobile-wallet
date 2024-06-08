git sttpackage org.mifospay.password.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.mifos.mobilewallet.mifospay.password.presenter.EditPasswordUiState
import org.mifos.mobilewallet.mifospay.password.presenter.EditPasswordViewModel
import org.mifospay.R
import org.mifospay.core.designsystem.component.MfPasswordTextField
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.theme.MifosTheme

@Composable
fun EditPasswordScreen(
    viewModel: EditPasswordViewModel = hiltViewModel(),
    onBackPress: () -> Unit,
    onCancelChanges: () -> Unit
) {
    val editPasswordUiState by viewModel.editPasswordUiState.collectAsStateWithLifecycle()
    EditPasswordScreen(
        editPasswordUiState = editPasswordUiState,
        onCancelChanges = onCancelChanges,
        onBackPress = onBackPress,
        onSave = { currentPass, newPass, confirmPass ->
            viewModel.updatePassword(
                currentPassword = currentPass,
                newPassword = newPass,
                newPasswordRepeat = confirmPass
            )
        }
    )
}

@Composable
fun EditPasswordScreen(
    editPasswordUiState: EditPasswordUiState,
    onCancelChanges: () -> Unit,
    onBackPress: () -> Unit,
    onSave: (currentPass: String, newPass: String, confirmPass: String) -> Unit
) {
    val context = LocalContext.current
    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmNewPassword by rememberSaveable { mutableStateOf("") }
    var isConfirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isNewPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isConfirmNewPasswordVisible by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val currentSnackbarHostState by rememberUpdatedState(snackbarHostState)

    LaunchedEffect(editPasswordUiState) {
        when (editPasswordUiState) {
            is EditPasswordUiState.Error -> {
                val errorMessage = editPasswordUiState.message
                coroutineScope.launch {
                    currentSnackbarHostState.showSnackbar(errorMessage)
                }
            }

            EditPasswordUiState.Loading -> {}
            EditPasswordUiState.Success -> {
                coroutineScope.launch {
                    currentSnackbarHostState.showSnackbar(context.getString(R.string.password_changed_successfully))
                }
            }

            else -> {}
        }
    }

    MifosScaffold(
        topBarTitle = R.string.change_password,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        backPress = onBackPress,
        scaffoldContent = { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MfPasswordTextField(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                MfPasswordTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    password = currentPassword,
                    label = stringResource(R.string.current_password),
                    isError = false,
                    isPasswordVisible = isConfirmPasswordVisible,
                    onTogglePasswordVisibility = {
                        isConfirmPasswordVisible = !isConfirmPasswordVisible
                    },
                    onPasswordChange = { currentPassword = it },
                )
                MfPasswordTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    password = newPassword,
                    label = stringResource(id = R.string.new_password),
                    isError = newPassword.isNotEmpty() && newPassword.length < 6,
                    errorMessage = if (newPassword.isNotEmpty() && newPassword.length < 6) stringResource(
                        id = R.string.password_length_error
                    ) else null,
                    isPasswordVisible = isNewPasswordVisible,
                    onTogglePasswordVisibility = { isNewPasswordVisible = !isNewPasswordVisible },
                    onPasswordChange = { newPassword = it }
                )
                MfPasswordTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    password = confirmNewPassword,
                    label = stringResource(id = R.string.confirm_new_password),
                    isError = newPassword != confirmNewPassword && confirmNewPassword.isNotEmpty(),
                    errorMessage = if (newPassword != confirmNewPassword && confirmNewPassword.isNotEmpty()) stringResource(
                        id = R.string.password_mismatch_error
                    ) else null,
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
                    MifosButton(
                        onClick = { onCancelChanges.invoke() },
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        contentPadding = PaddingValues(16.dp),
                        content = { Text(text = stringResource(id = R.string.cancel)) }
                    )
                    MifosButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        onClick = {
                            onSave.invoke(currentPassword, newPassword, confirmNewPassword)
                        },
                        contentPadding = PaddingValues(16.dp),
                        content = { Text(text = stringResource(id = R.string.save)) }
                    )
                }
            }
        }
    }
    )
}

class EditPasswordUiStateProvider : PreviewParameterProvider<EditPasswordUiState> {
    override val values: Sequence<EditPasswordUiState>
        get() = sequenceOf(
            EditPasswordUiState.Loading,
            EditPasswordUiState.Success,
            EditPasswordUiState.Error("Some Error Occurred")
        )
}

@Preview
@Composable
private fun EditPasswordScreenPreview(
    @PreviewParameter(EditPasswordUiStateProvider::class) editPasswordUiState: EditPasswordUiState
) {
    MifosTheme {
        EditPasswordScreen(editPasswordUiState = editPasswordUiState, {}, {}, { a, b, c -> })
    }
}
