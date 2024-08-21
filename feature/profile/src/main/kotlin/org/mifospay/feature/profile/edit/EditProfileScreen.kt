/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.profile.edit

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.togitech.ccp.component.TogiCountryCodePicker
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MfOutlinedTextField
import org.mifospay.core.designsystem.component.MifosBottomSheet
import org.mifospay.core.designsystem.component.MifosDialogBox
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.PermissionBox
import org.mifospay.core.designsystem.icon.MifosIcons.Camera
import org.mifospay.core.designsystem.icon.MifosIcons.Delete
import org.mifospay.core.designsystem.icon.MifosIcons.PhotoLibrary
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.designsystem.theme.historyItemTextStyle
import org.mifospay.core.designsystem.theme.styleMedium16sp
import org.mifospay.feature.profile.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditProfileScreenRoute(
    onBackClick: () -> Unit,
    getUri: (context: Context, file: File) -> Uri,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val editProfileUiState by viewModel.editProfileUiState.collectAsStateWithLifecycle()
    val updateSuccess by viewModel.updateSuccess.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val file = createImageFile(context)
    val uri = getUri(context, file)

    EditProfileScreen(
        editProfileUiState = editProfileUiState,
        updateSuccess = updateSuccess,
        onBackClick = onBackClick,
        updateEmail = viewModel::updateEmail,
        updateMobile = viewModel::updateMobile,
        modifier = modifier,
        uri = uri,
    )
}

@Composable
fun EditProfileScreen(
    editProfileUiState: EditProfileUiState,
    updateSuccess: Boolean,
    onBackClick: () -> Unit,
    updateEmail: (String) -> Unit,
    updateMobile: (String) -> Unit,
    modifier: Modifier = Modifier,
    uri: Uri? = null,
) {
    var showDiscardChangesDialog by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier =
            modifier
                .fillMaxSize(),
    ) {
        MifosScaffold(
            topBarTitle = R.string.feature_profile_edit_profile,
            backPress = { showDiscardChangesDialog = true },
            scaffoldContent = {
                when (editProfileUiState) {
                    EditProfileUiState.Loading -> {
                        MfLoadingWheel(
                            contentDesc = stringResource(R.string.feature_profile_loading),
                            backgroundColor = MaterialTheme.colorScheme.surface,
                        )
                    }

                    is EditProfileUiState.Success -> {
                        val initialUsername = editProfileUiState.username
                        val initialMobile = editProfileUiState.mobile
                        val initialVpa = editProfileUiState.vpa
                        val initialEmail = editProfileUiState.email

                        EditProfileScreenContent(
                            initialUsername,
                            initialMobile,
                            initialVpa,
                            initialEmail,
                            updateSuccess = updateSuccess,
                            contentPadding = it,
                            updateEmail = updateEmail,
                            updateMobile = updateMobile,
                            onBackClick = onBackClick,
                            uri = uri,
                        )
                    }
                }
            },
        )

        MifosDialogBox(
            showDialogState = showDiscardChangesDialog,
            onDismiss = { showDiscardChangesDialog = false },
            title = R.string.feature_profile_discard_changes,
            confirmButtonText = R.string.feature_profile_confirm_text,
            onConfirm = {
                showDiscardChangesDialog = false
                onBackClick.invoke()
            },
            dismissButtonText = R.string.feature_profile_dismiss_text,
        )
    }
}

@Suppress("LongMethod")
@Composable
private fun EditProfileScreenContent(
    initialUsername: String,
    initialMobile: String,
    initialVpa: String,
    initialEmail: String,
    updateSuccess: Boolean,
    contentPadding: PaddingValues,
    updateEmail: (String) -> Unit,
    updateMobile: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    uri: Uri? = null,
) {
    var username by rememberSaveable { mutableStateOf(initialUsername) }
    var mobile by rememberSaveable { mutableStateOf(initialMobile) }
    var vpa by rememberSaveable { mutableStateOf(initialVpa) }
    var email by rememberSaveable { mutableStateOf(initialEmail) }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    PermissionBox(
        requiredPermissions =
            if (Build.VERSION.SDK_INT >= 33) {
                listOf(Manifest.permission.CAMERA)
            } else {
                listOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            },
        title = R.string.feature_profile_permission_required,
        confirmButtonText = R.string.feature_profile_proceed,
        dismissButtonText = R.string.feature_profile_dismiss,
        description = R.string.feature_profile_approve_description,
        onGranted = {
            val cameraLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
                    imageUri = uri
                }

            val galleryLauncher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent(),
                ) { uri: Uri? ->
                    uri?.let {
                        imageUri = uri
                    }
                }

            if (showBottomSheet) {
                MifosBottomSheet(
                    content = {
                        EditProfileBottomSheetContent(
                            onClickProfilePicture = {
                                if (uri != null) {
                                    cameraLauncher.launch(uri)
                                }
                                showBottomSheet = false
                            },
                            onChangeProfilePicture = {
                                galleryLauncher.launch("image/*")
                                showBottomSheet = false
                            },
                            onRemoveProfilePicture = {
                                imageUri = null
                                showBottomSheet = false
                            },
                        )
                    },
                    onDismiss = { showBottomSheet = false },
                )
            }
        },
    )
    Box(
        modifier =
            modifier
                .padding(contentPadding)
                .fillMaxSize(),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState()),
        ) {
            EditProfileScreenImage(
                imageUri = imageUri,
                onCameraIconClick = { showBottomSheet = true },
            )
            MfOutlinedTextField(
                value = username,
                label = stringResource(id = R.string.feature_profile_username),
                onValueChange = { username = it },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
            )
            MfOutlinedTextField(
                value = email,
                label = stringResource(id = R.string.feature_profile_email),
                onValueChange = { email = it },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
            )
            MfOutlinedTextField(
                value = vpa,
                label = stringResource(id = R.string.feature_profile_vpa),
                onValueChange = { vpa = it },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
            ) {
                val keyboardController = LocalSoftwareKeyboardController.current
                if (LocalInspectionMode.current) {
                    Text("Placeholder for TogiCountryCodePicker")
                } else {
                    TogiCountryCodePicker(
                        modifier = Modifier,
                        initialPhoneNumber = " ",
                        autoDetectCode = true,
                        shape = RoundedCornerShape(3.dp),
                        colors =
                            TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        onValueChange = { (code, phone), isValid ->
                            if (isValid) {
                                mobile = code + phone
                            }
                        },
                        label = { Text(stringResource(id = R.string.feature_profile_phone_number)) },
                        keyboardActions = KeyboardActions { keyboardController?.hide() },
                    )
                }
            }
            EditProfileSaveButton(
                onClick = {
                    if (isDataSaveNecessary(email, initialEmail)) {
                        updateEmail(email)
                    }
                    if (isDataSaveNecessary(mobile, initialMobile)) {
                        updateMobile(mobile)
                    }
                    if (updateSuccess) {
                        // if user details is successfully saved then go back to Profile Activity
                        // same behaviour as onBackPress, hence reused the callback
                        onBackClick.invoke()
                    } else {
                        Toast
                            .makeText(
                                context,
                                R.string.feature_profile_failed_to_save_changes,
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
                },
                buttonText = R.string.feature_profile_save,
            )
        }
    }
}

private fun isDataSaveNecessary(
    input: String,
    initialInput: String,
): Boolean = input == initialInput

@Composable
fun EditProfileBottomSheetContent(
    onClickProfilePicture: () -> Unit,
    onChangeProfilePicture: () -> Unit,
    onRemoveProfilePicture: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(top = 8.dp, bottom = 12.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clickable { onClickProfilePicture.invoke() },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = Camera, contentDescription = null)
            Text(
                text = stringResource(id = R.string.feature_profile_click_profile_picture),
                style = historyItemTextStyle,
            )
        }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clickable { onChangeProfilePicture.invoke() },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = PhotoLibrary, contentDescription = null)
            Text(
                text = stringResource(id = R.string.feature_profile_change_profile_picture),
                style = historyItemTextStyle,
            )
        }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clickable { onRemoveProfilePicture.invoke() },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = Delete, contentDescription = null)
            Text(
                text = stringResource(id = R.string.feature_profile_remove_profile_picture),
                style = historyItemTextStyle,
            )
        }
    }
}

@Composable
private fun EditProfileSaveButton(
    onClick: () -> Unit,
    buttonText: Int,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        modifier =
            modifier
                .fillMaxWidth()
                .padding(16.dp),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(12.dp),
    ) {
        Text(
            text = stringResource(id = buttonText),
            style = styleMedium16sp.copy(MaterialTheme.colorScheme.onPrimary),
        )
    }
}

private fun createImageFile(context: Context): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir,
    )
}

internal class EditProfilePreviewProvider : PreviewParameterProvider<EditProfileUiState> {
    override val values: Sequence<EditProfileUiState>
        get() =
            sequenceOf(
                EditProfileUiState.Loading,
                EditProfileUiState.Success(),
                EditProfileUiState.Success(
                    name = "John Doe",
                    username = "John",
                    email = "john@mifos.org",
                    vpa = "vpa",
                    mobile = "+1 55557772901",
                ),
            )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditProfileScreenPreview(
    @PreviewParameter(EditProfilePreviewProvider::class)
    editProfileUiState: EditProfileUiState,
) {
    MifosTheme {
        EditProfileScreen(
            editProfileUiState = editProfileUiState,
            updateSuccess = false,
            onBackClick = {},
            updateEmail = {},
            updateMobile = {},
            uri = null,
        )
    }
}
