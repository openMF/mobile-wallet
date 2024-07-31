@file:Suppress("MaxLineLength")

package org.mifospay.feature.auth.social_signup

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch
import org.mifospay.common.Constants
import org.mifospay.core.data.util.Constants.MIFOS_CONSUMER_SAVINGS_PRODUCT_ID
import org.mifospay.core.data.util.Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID
import org.mifospay.core.designsystem.component.MifosBottomSheet
import org.mifospay.feature.auth.R

const val TAG = "Social Login"

// Followed this https://medium.com/@nirmale.ashwin9696/a-comprehensive-guide-to-google-sign-in-integration-with-credential-manager-in-android-apps-05286f8f5848
// Keeping until we fix sign up
@Composable
fun SocialSignupMethodContentScreen(
    onDismissSignUp: () -> Unit
) {
    SocialSignupMethodScreen(onDismissSignUp = onDismissSignUp)
}

@Composable
fun SocialSignupMethodScreen(
    onDismissSignUp: () -> Unit
) {

    val context = LocalContext.current
    var mifosSavingProductId by remember { mutableIntStateOf(0) }
    var showProgress by remember { mutableStateOf(false) }

    val credentialManager = CredentialManager.create(context)
    val coroutineScope = rememberCoroutineScope()
    var showFilterByAuthorizedAccounts by rememberSaveable { mutableStateOf(false) }
    var googleIdTokenCredential by remember { mutableStateOf<GoogleIdTokenCredential?>(null) }

    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId("728434912738-ea88f1thgvhi9058o23dbtp3p0555m32.apps.googleusercontent.com")
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()


    fun signUpWithMifos() {
        googleIdTokenCredential.signUpWithMifos(context, mifosSavingProductId) {
            coroutineScope.launch {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            }
            onDismissSignUp.invoke()
        }
    }

    fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        googleIdTokenCredential?.let {
                            signUpWithMifos()
                        } ?: {
                            Toast.makeText(
                                context,
                                Constants.GOOGLE_SIGN_IN_FAILED,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }


    fun signUpCredentialManager() {
        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                showFilterByAuthorizedAccounts = false
                Log.e(TAG, e.message.toString())
                // handleFailure(e)
            }
        }
    }

    fun signUp(checkedGoogleAccount: Boolean) {
        if (checkedGoogleAccount) {
            signUpCredentialManager()
        } else {
            signUpWithMifos()
        }
        showProgress = true
    }

    MifosBottomSheet(
        content = {
            SignupMethodContentScreen(
                showProgress = showProgress,
                onSignUpAsMerchant = { checkedGoogleAccount ->
                    mifosSavingProductId = MIFOS_MERCHANT_SAVINGS_PRODUCT_ID
                    signUp(checkedGoogleAccount)
                },
                onSignupAsCustomer = { checkedGoogleAccount ->
                    mifosSavingProductId = MIFOS_CONSUMER_SAVINGS_PRODUCT_ID
                    signUp(checkedGoogleAccount)
                }
            )
        },
        onDismiss = {
            onDismissSignUp.invoke()
        }
    )
}

@Composable
fun SignupMethodContentScreen(
    showProgress: Boolean,
    onSignUpAsMerchant: (Boolean) -> Unit,
    onSignupAsCustomer: (Boolean) -> Unit,
) {

    var checkedGoogleAccountState by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(id = R.string.feature_auth_create_an_account)
            )
            OutlinedButton(
                modifier = Modifier.padding(top = 48.dp),
                onClick = {
                    onSignUpAsMerchant.invoke(checkedGoogleAccountState)
                },
                border = BorderStroke(1.dp, Color.LightGray),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = stringResource(id = R.string.feature_auth_sign_up_as_merchant).uppercase(),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 8.dp)
                        .weight(.4f),
                    thickness = 1.dp
                )
                Text(
                    modifier = Modifier
                        .wrapContentWidth()
                        .weight(.1f),
                    text = stringResource(id = R.string.feature_auth_or)
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 24.dp)
                        .weight(.4f),
                    thickness = 1.dp
                )
            }
            OutlinedButton(
                modifier = Modifier.padding(top = 24.dp),
                onClick = {
                    onSignupAsCustomer.invoke(checkedGoogleAccountState)
                },
                border = BorderStroke(1.dp, Color.LightGray),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = stringResource(id = R.string.feature_auth_sign_up_as_customer).uppercase(),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                    .clickable {
                        checkedGoogleAccountState = !checkedGoogleAccountState
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checkedGoogleAccountState,
                    onCheckedChange = {
                        checkedGoogleAccountState = !checkedGoogleAccountState
                    },
                    colors = CheckboxDefaults.colors(MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = stringResource(id = R.string.feature_auth_ease_my_sign_up_using_google_account),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            HorizontalDivider(thickness = 48.dp, color = Color.Transparent)
        }
        if (showProgress) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 140.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = Color.Black,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}

fun GoogleIdTokenCredential?.signUpWithMifos(
    context: Context,
    mifosSavingsProductId: Int,
    signOutGoogleClient: () -> Unit
) {
    val googleIdTokenCredential = this
    //Todo:navigate to MobileVerificationScreen with googleIdTokenCredential.givenName,profilePictureUri,
    // familyName,mifosSavingsProductId,displayName,data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID")
    signOutGoogleClient.invoke()
}

@Preview
@Composable
fun SignupMethodContentScreenPreview() {
    MaterialTheme {
        SignupMethodContentScreen(true, {}, {})
    }
}
