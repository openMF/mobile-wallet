package org.mifospay.editprofile.ui

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.togitech.ccp.component.TogiCountryCodePicker
import org.mifospay.BuildConfig
import org.mifospay.R
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MfOutlinedTextField
import org.mifospay.core.designsystem.component.MifosBottomSheet
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.PermissionBox
import org.mifospay.core.designsystem.theme.historyItemTextStyle
import org.mifospay.core.designsystem.theme.styleMedium16sp
import org.mifospay.core.ui.DevicePreviews
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.editprofile.presenter.EditProfileUiState
import org.mifospay.editprofile.presenter.EditProfileViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

@Composable
fun EditProfileScreenRoute(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onSaveChanges: () -> Unit,
    onBackClick: () -> Unit
) {
    val editProfileUiState by viewModel.editProfileUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.fetchProfileDetails()
    }

    EditProfileScreen(
        editProfileUiState = editProfileUiState,
        onSaveChanges = onSaveChanges,
        onBackClick = onBackClick,
        updateEmail = {
            viewModel.updateEmail(
                it
            )
        },
        updateMobile = {
            viewModel.updateMobile(
                it
            )
        }
    )
}

@Composable
fun EditProfileScreen(
    editProfileUiState: EditProfileUiState,
    onSaveChanges: () -> Unit,
    onBackClick: () -> Unit,
    updateEmail: (String) -> Unit,
    updateMobile: (String) -> Unit
) {
    val context = LocalContext.current
    val file = createImageFile(context)
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when (editProfileUiState) {
            is EditProfileUiState.Error -> {
                EmptyContentScreen(
                    modifier = Modifier,
                    title = stringResource(id = R.string.error_oops),
                    subTitle = stringResource(id = R.string.unexpected_error_subtitle),
                    iconTint = Color.Black,
                    iconImageVector = Icons.Rounded.Info
                )
            }

            EditProfileUiState.Loading -> {
                MfLoadingWheel(
                    contentDesc = stringResource(R.string.loading),
                    backgroundColor = Color.White
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
                    uri,
                    onBackClick = onBackClick,
                    onSaveChanges = onSaveChanges,
                    updateEmail = updateEmail,
                    updateMobile = updateMobile
                )
            }

            else -> {}
        }
    }
}

@Composable
fun EditProfileScreenContent(
    initialUsername: String,
    initialMobile: String,
    initialVpa: String,
    initialEmail: String,
    uri: Uri?,
    onBackClick: () -> Unit,
    onSaveChanges: () -> Unit,
    updateEmail: (String) -> Unit,
    updateMobile: (String) -> Unit
) {
    var username by rememberSaveable { mutableStateOf(initialUsername) }
    var mobile by rememberSaveable { mutableStateOf(initialMobile) }
    var vpa by rememberSaveable { mutableStateOf(initialVpa) }
    var email by rememberSaveable { mutableStateOf(initialEmail) }
    val snackBarHostState = remember { SnackbarHostState() }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }

    PermissionBox(
        requiredPermissions = if (Build.VERSION.SDK_INT >= 33) {
            listOf(
                Manifest.permission.CAMERA
            )
        } else {
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        },
        title = R.string.permission_required,
        description = R.string.approve_permission_description_camera,
        confirmButtonText = R.string.proceed,
        dismissButtonText = R.string.dismiss,
        onGranted = {
            val cameraLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
                    imageUri = uri
                }

            val galleryLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                uri?.let {
                    imageUri = uri
                }
            }

            if (showBottomSheet) {
                MifosBottomSheet(
                    content = {
                        EditProfileBottomSheetContent(
                            {
                                cameraLauncher.launch(uri)
                                showBottomSheet = false
                            },
                            {
                                galleryLauncher.launch("image/*")
                                showBottomSheet = false
                            },
                            {
                                imageUri = null
                                showBottomSheet = false
                            })
                    },
                    onDismiss = { showBottomSheet = false }
                )
            }
        }
    )

    MifosScaffold(
        topBarTitle = R.string.edit_profile,
        backPress = { onBackClick.invoke() },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        scaffoldContent = { contentPadding ->
            Box(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .verticalScroll(rememberScrollState())
                ) {
                    EditProfileScreenImage(
                        imageUri = imageUri,
                        onCameraIconClick = { showBottomSheet = true })
                    MfOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        value = username,
                        label = stringResource(id = R.string.username),
                        onValueChange = { username = it }
                    )
                    MfOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        value = email,
                        label = stringResource(id = R.string.email),
                        onValueChange = { email = it }
                    )
                    MfOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        value = vpa,
                        label = stringResource(id = R.string.vpa),
                        onValueChange = { vpa = it }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp)
                    ) {
                        val keyboardController = LocalSoftwareKeyboardController.current
                        if (LocalInspectionMode.current) {
                            Text("Placeholder for TogiCountryCodePicker")
                        } else {
                            TogiCountryCodePicker(
                                modifier = Modifier,
                                initialPhoneNumber = mobile,
                                autoDetectCode = true,
                                shape = RoundedCornerShape(3.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color.Black
                                ),
                                onValueChange = { (code, phone), isValid ->
                                    if (isValid) {
                                        mobile = code + phone
                                    }
                                },
                                label = { Text(stringResource(id = R.string.phone_number)) },
                                keyboardActions = KeyboardActions { keyboardController?.hide() }
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
                        },
                        buttonText = R.string.save
                    )
                }
            }
        }
    )
}

private fun isDataSaveNecessary(input: String, initialInput: String): Boolean {
    return input == initialInput
}

@Composable
fun EditProfileBottomSheetContent(
    onClickProfilePicture: () -> Unit,
    onChangeProfilePicture: () -> Unit,
    onRemoveProfilePicture: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(top = 8.dp, bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clickable { onClickProfilePicture.invoke() },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.Camera, contentDescription = null)
            Text(
                text = stringResource(id = R.string.click_profile_picture),
                style = historyItemTextStyle
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clickable { onChangeProfilePicture.invoke() },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.PhotoLibrary, contentDescription = null)
            Text(
                text = stringResource(id = R.string.change_profile_picture),
                style = historyItemTextStyle
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clickable { onRemoveProfilePicture.invoke() },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
            Text(
                text = stringResource(id = R.string.remove_profile_picture),
                style = historyItemTextStyle
            )
        }
    }
}

@Composable
fun EditProfileSaveButton(onClick: () -> Unit, buttonText: Int) {
    Button(
        onClick = { onClick.invoke() },
        colors = ButtonDefaults.buttonColors(Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        Text(text = stringResource(id = buttonText), style = styleMedium16sp.copy(Color.White))
    }
}

fun createImageFile(context: Context): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
}

@DevicePreviews
@Composable
private fun EditProfileScreenContentPreview() {
    EditProfileScreenContent(
        initialUsername = "John",
        initialEmail = "john@mifos.org",
        initialVpa = "vpa",
        initialMobile = "+1 55557772901",
        uri = null,
        onBackClick = {},
        onSaveChanges = {},
        updateEmail = {},
        updateMobile = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun EditProfileLoadingScreenPreview() {
    EditProfileScreen(editProfileUiState = EditProfileUiState.Loading, {}, {}, {}, {})
}

@Preview(showBackground = true)
@Composable
private fun EditProfileSuccessScreenPreview() {
    EditProfileScreen(editProfileUiState = EditProfileUiState.Success(), {}, {}, {}, {})
}

@Preview(showBackground = true)
@Composable
private fun EditProfileSuccessDataScreenPreview() {
    EditProfileScreen(editProfileUiState = EditProfileUiState.Success(
        name = "John Doe",
        username = "John",
        email = "john@mifos.org",
        vpa = "vpa",
        mobile = "+1 55557772901"
    ), {}, {}, {}, {})
}

@Preview(showBackground = true)
@Composable
private fun EditProfileErrorScreenPreview() {
    EditProfileScreen(editProfileUiState = EditProfileUiState.Error("Error Screen"), {}, {}, {}, {})
}