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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosBottomSheet
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosDialogBox
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTextField
import org.mifospay.core.designsystem.component.PermissionBox
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme
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
    viewModel: EditProfileViewModel = koinViewModel(),
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
private fun EditProfileScreen(
    editProfileUiState: EditProfileUiState,
    updateSuccess: Boolean,
    onBackClick: () -> Unit,
    updateEmail: (String) -> Unit,
    updateMobile: (String) -> Unit,
    modifier: Modifier = Modifier,
    uri: Uri? = null,
) {
    var showDiscardChangesDialog by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        MifosScaffold(
            topBarTitle = R.string.feature_profile_edit_profile,
            backPress = { showDiscardChangesDialog = true },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            scaffoldContent = {
                when (editProfileUiState) {
                    is EditProfileUiState.Loading -> {
                        MfLoadingWheel(
                            contentDesc = stringResource(R.string.feature_profile_loading),
                            backgroundColor = MaterialTheme.colorScheme.surface,
                        )
                    }

                    is EditProfileUiState.Success -> {
                        EditProfileScreenContent(
                            editProfileUiState = editProfileUiState,
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
    editProfileUiState: EditProfileUiState.Success,
    updateSuccess: Boolean,
    contentPadding: PaddingValues,
    updateEmail: (String) -> Unit,
    updateMobile: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    uri: Uri? = null,
) {
    var username by rememberSaveable { mutableStateOf(editProfileUiState.username) }
    var mobile by rememberSaveable { mutableStateOf(editProfileUiState.mobile) }
    var vpa by rememberSaveable { mutableStateOf(editProfileUiState.vpa) }
    var email by rememberSaveable { mutableStateOf(editProfileUiState.email) }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    PermissionBox(
        requiredPermissions = if (Build.VERSION.SDK_INT >= 33) {
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

            val galleryLauncher = rememberLauncherForActivityResult(
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

    LazyColumn(
        modifier = modifier
            .padding(contentPadding)
            .fillMaxSize(),
        contentPadding = PaddingValues(top = 30.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            EditProfileScreenImage(
                imageUri = imageUri,
                onCameraIconClick = { showBottomSheet = true },
                modifier = Modifier.padding(bottom = 5.dp),
            )
        }

        item {
            MifosTextField(
                value = username,
                onValueChange = { username = it },
                label = stringResource(id = R.string.feature_profile_username),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
            )
        }

        item {
            MifosTextField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(id = R.string.feature_profile_email),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
            )
        }

        item {
            MifosTextField(
                value = vpa,
                onValueChange = { vpa = it },
                label = stringResource(id = R.string.feature_profile_vpa),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
            )
        }

        item {
            MifosTextField(
                value = mobile,
                onValueChange = { mobile = it },
                label = stringResource(id = R.string.feature_profile_mobile),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
            )
        }

        item {
            MifosButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(54.dp),
                color = MaterialTheme.colorScheme.primary,
                text = {
                    Text(
                        text = stringResource(id = R.string.feature_profile_save),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                },
                onClick = {
                    if (isDataSaveNecessary(email, editProfileUiState.email)) {
                        updateEmail(email)
                    }
                    if (isDataSaveNecessary(mobile, editProfileUiState.mobile)) {
                        updateMobile(mobile)
                    }
                    if (updateSuccess) {
                        // if user details is successfully saved then go back to Profile Activity
                        // same behaviour as onBackPress, hence reused the callback
                        Toast.makeText(
                            context,
                            context.getString(R.string.feature_profile_updated_sucessfully),
                            Toast.LENGTH_SHORT,
                        ).show()
                        onBackClick.invoke()
                    } else {
                        scope.launch {
                            Toast.makeText(
                                context,
                                context.getString(R.string.feature_profile_failed_to_save_changes),
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                },
            )
        }
    }
}

private fun isDataSaveNecessary(
    input: String,
    initialInput: String,
): Boolean = input == initialInput

@Composable
private fun EditProfileBottomSheetContent(
    onClickProfilePicture: () -> Unit,
    onChangeProfilePicture: () -> Unit,
    onRemoveProfilePicture: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ImagePickerOption(
            label = stringResource(id = R.string.feature_profile_click_profile_picture),
            icon = MifosIcons.Camera,
            onClick = onClickProfilePicture,
        )

        ImagePickerOption(
            label = stringResource(id = R.string.feature_profile_change_profile_picture),
            icon = MifosIcons.PhotoLibrary,
            onClick = onChangeProfilePicture,
        )

        ImagePickerOption(
            label = stringResource(id = R.string.feature_profile_remove_profile_picture),
            icon = MifosIcons.Delete,
            onClick = onRemoveProfilePicture,
        )
    }
}

@Composable
private fun ImagePickerOption(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(4.dp),
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
            Text(text = label, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
internal fun EditProfileSaveButton(
    onClick: () -> Unit,
    buttonText: Int,
    modifier: Modifier = Modifier,
) {
    MifosButton(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        modifier = modifier
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
        get() = sequenceOf(
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
    @PreviewParameter(EditProfilePreviewProvider::class) editProfileUiState: EditProfileUiState,
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
