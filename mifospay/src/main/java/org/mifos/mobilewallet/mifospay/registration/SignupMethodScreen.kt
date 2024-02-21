package org.mifos.mobilewallet.mifospay.registration

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.mifos.mobilewallet.core.utils.Constants.MIFOS_CONSUMER_SAVINGS_PRODUCT_ID
import org.mifos.mobilewallet.core.utils.Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosBottomSheet
import org.mifos.mobilewallet.mifospay.registration.ui.MobileVerificationActivity
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.DebugUtil
import org.mifos.mobilewallet.mifospay.utils.Toaster
import org.mifos.mobilewallet.mifospay.utils.Constants as MifosConstant

const val REQUEST_CODE_SIGN_IN = 1

@Composable
fun SignupMethodContentScreen(
    onDismissSignUp: () -> Unit
) {
    SignupMethodScreen(onDismissSignUp = onDismissSignUp)
}

@Composable
fun SignupMethodScreen(
    onDismissSignUp: () -> Unit
) {

    val context = LocalContext.current
    var mifosSavingProductId by remember { mutableIntStateOf(0) }
    var showProgress by remember { mutableStateOf(false) }

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient by remember { mutableStateOf(GoogleSignIn.getClient(context, gso)) }
    var googleSignInAccount by remember { mutableStateOf<GoogleSignInAccount?>(null) }

    fun signUpWithMifos() {
        googleSignInAccount.signUpWithMifos(context, mifosSavingProductId) {
            googleSignInClient.signOut().addOnCompleteListener(context as Activity) {
                googleSignInAccount = null
            }
            onDismissSignUp.invoke()
        }
    }

    val launchGoogleSignup = rememberLauncherForActivityResult(
        contract = GoogleApiContract(googleSignInClient)
    ) { task ->
        try {
            // Google Sign In was successful, authenticate with Firebase
            googleSignInAccount = task?.getResult(ApiException::class.java)
            if (googleSignInAccount != null) {
                signUpWithMifos()
            } else {
                Toaster.showToast(context, MifosConstant.GOOGLE_SIGN_IN_FAILED)
            }

        } catch (e: Exception) {
            // Google Sign In failed, update UI appropriately
            e.message?.let { DebugUtil.log(MifosConstant.GOOGLE_SIGN_IN_FAILED, it) }
            Toaster.showToast(context, MifosConstant.GOOGLE_SIGN_IN_FAILED)
        }
        onDismissSignUp.invoke()
    }

    fun signUp(checkedGoogleAccount: Boolean) {
        if (checkedGoogleAccount) {
            launchGoogleSignup.launch(REQUEST_CODE_SIGN_IN)
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
                .background(color = Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(id = R.string.create_an_account)
            )
            OutlinedButton(
                modifier = Modifier.padding(top = 48.dp),
                onClick = {
                    onSignUpAsMerchant.invoke(checkedGoogleAccountState)
                },
                border = BorderStroke(1.dp, Color.LightGray),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) {
                Text(
                    text = stringResource(id = R.string.sign_up_as_merchant).uppercase(),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 8.dp)
                        .weight(.4f),
                    thickness = 1.dp
                )
                Text(
                    modifier = Modifier
                        .wrapContentWidth()
                        .weight(.1f),
                    text = stringResource(id = R.string.or)
                )
                Divider(
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
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) {
                Text(
                    text = stringResource(id = R.string.sign_up_as_customer).uppercase(),
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
                    colors = CheckboxDefaults.colors(Color.Black)
                )
                Text(
                    text = stringResource(id = R.string.ease_my_sign_up_using_google_account),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Divider(thickness = 48.dp, color = Color.Transparent)
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

fun GoogleSignInAccount?.signUpWithMifos(
    context: Context,
    mifosSavingsProductId: Int,
    signOutGoogleClient: () -> Unit
) {
    val googleSignInAccount = this
    val intent = Intent(context, MobileVerificationActivity::class.java)
    intent.putExtra(Constants.MIFOS_SAVINGS_PRODUCT_ID, mifosSavingsProductId)
    if (googleSignInAccount != null) {
        intent.putExtra(Constants.GOOGLE_PHOTO_URI, googleSignInAccount.photoUrl)
        intent.putExtra(Constants.GOOGLE_DISPLAY_NAME, googleSignInAccount.displayName)
        intent.putExtra(Constants.GOOGLE_EMAIL, googleSignInAccount.email)
        intent.putExtra(Constants.GOOGLE_FAMILY_NAME, googleSignInAccount.familyName)
        intent.putExtra(Constants.GOOGLE_GIVEN_NAME, googleSignInAccount.givenName)
    }
    context.startActivity(intent)
    signOutGoogleClient.invoke()
}

@Preview
@Composable
fun SignupMethodContentScreenPreview() {
    MaterialTheme {
        SignupMethodContentScreen(true, {}, {})
    }
}
