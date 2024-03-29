package org.mifos.mobilewallet.mifospay.editprofile.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.togitech.ccp.component.TogiCountryCodePicker
import kotlinx.coroutines.launch
import org.mifos.mobilewallet.mifospay.BuildConfig
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MfLoadingWheel
import org.mifos.mobilewallet.mifospay.designsystem.component.MfOutlinedTextField
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosBottomSheet
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosTopBar
import org.mifos.mobilewallet.mifospay.designsystem.theme.historyItemTextStyle
import org.mifos.mobilewallet.mifospay.designsystem.theme.styleMedium16sp
import org.mifos.mobilewallet.mifospay.editprofile.presenter.EditProfileUiState
import org.mifos.mobilewallet.mifospay.editprofile.presenter.EditProfileViewModel
import org.mifos.mobilewallet.mifospay.ui.EmptyContentScreen
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onChangePassword: () -> Unit,
    onChangePasscode: () -> Unit,
    onSaveChanges: () -> Unit
) {
    val editProfileUiState by viewModel.editProfileUiState.collectAsStateWithLifecycle()

    EditProfileScreen(
        editProfileUiState = editProfileUiState,
        onChangePassword = onChangePassword,
        onChangePasscode = onChangePasscode,
        onSaveChanges = onSaveChanges
    )
}

@Composable
fun EditProfileScreen(
    editProfileUiState: EditProfileUiState,
    onChangePassword: () -> Unit,
    onChangePasscode: () -> Unit,
    onSaveChanges: () -> Unit
) {
    val context = LocalContext.current
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val file = createImageFile(context)
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        imageUri = uri
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.permission_granted))
            }
            cameraLauncher.launch(uri)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.permission_denied))
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = uri
        }
    }

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                galleryLauncher.launch("image/*")
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.storage_permission_is_required_to_access_gallery))
                }
            }
        }
    )

    Scaffold(
        topBar = { MifosTopBar(topBarTitle = R.string.edit_profile) {} },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onSaveChanges.invoke() },
                containerColor = Color.Black
            ) {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
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
                    var username by rememberSaveable { mutableStateOf(editProfileUiState.username) }
                    var mobile by rememberSaveable { mutableStateOf(editProfileUiState.mobile) }
                    var vpa by rememberSaveable { mutableStateOf(editProfileUiState.vpa) }
                    var email by rememberSaveable { mutableStateOf(editProfileUiState.email) }
                    EditProfileScreenImage(imageUri = imageUri, onCameraIconClick = { showBottomSheet = true })
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
                        TogiCountryCodePicker(
                            modifier = Modifier,
                            initialPhoneNumber = editProfileUiState.mobile,
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
                    EditProfileButton(
                        onClick = { onChangePassword.invoke() },
                        buttonText = R.string.change_password
                    )
                    EditProfileButton(
                        onClick = { onChangePasscode.invoke() },
                        buttonText = R.string.change_passcode
                    )

                    if (showBottomSheet) {
                        MifosBottomSheet(
                            content = {
                                EditProfileBottomSheetContent(
                                    {
                                        val permissionCheckResult =
                                            ContextCompat.checkSelfPermission(
                                                context,
                                                Manifest.permission.CAMERA
                                            )
                                        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                            cameraLauncher.launch(uri)
                                        } else {
                                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                        }
                                        showBottomSheet = false
                                    },
                                    {
                                        val permissionCheckResult =
                                            ContextCompat.checkSelfPermission(
                                                context,
                                                Manifest.permission.READ_EXTERNAL_STORAGE
                                            )
                                        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                            galleryLauncher.launch("image/*")
                                        } else {
                                            storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                        }
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
            }
        }
    }
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
fun EditProfileButton(onClick: () -> Unit, buttonText: Int) {
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

@Preview(showBackground = true)
@Composable
private fun EditProfileLoadingScreenPreview() {
    EditProfileScreen(editProfileUiState = EditProfileUiState.Loading, {}, {}, {})
}

@Preview(showBackground = true)
@Composable
private fun EditProfileSuccessScreenPreview() {
    EditProfileScreen(editProfileUiState = EditProfileUiState.Success(), {}, {}, {})
}

@Preview(showBackground = true)
@Composable
private fun EditProfileErrorScreenPreview() {
    EditProfileScreen(editProfileUiState = EditProfileUiState.Error("Error Screen"), {}, {}, {})
}