package org.mifos.mobilewallet.mifospay.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.theme.MifosTheme
import org.mifos.mobilewallet.mifospay.theme.grey
import org.mifos.mobilewallet.mifospay.theme.styleMedium16sp
import org.mifos.mobilewallet.mifospay.theme.styleMedium30sp
import org.mifos.mobilewallet.mifospay.theme.styleNormal18sp

@Composable
fun LoginScreen(
    login: (username: String, password: String) -> Unit,
    signUp: () -> Unit
) {
    var userName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    var password by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    var passwordVisibility: Boolean by remember { mutableStateOf(false) }

    MifosTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(top = 100.dp, start = 48.dp, end = 48.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.login),
                style = styleMedium30sp
            )
            Text(
                modifier = Modifier
                    .padding(top = 32.dp),
                text = stringResource(id = R.string.welcome_back),
                style = styleNormal18sp.copy(color = grey)
            )
            Spacer(modifier = Modifier.padding(top = 32.dp))
            MifosOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = userName,
                onValueChange = {
                    userName = it
                },
                label = R.string.username
            )
            Spacer(modifier = Modifier.padding(top = 16.dp))
            MifosOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = {
                    password = it
                },
                label = R.string.password,
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisibility)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(imageVector = image, null)
                    }
                }
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                enabled = userName.text.isNotEmpty() && password.text.isNotEmpty(),
                onClick = {
                    login.invoke(userName.text, password.text)
                },
                contentPadding = PaddingValues(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.login).uppercase(),
                    style = styleMedium16sp.copy(color = Color.White)
                )
            }
            // Hide reset password for now
            /*Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                text = "Forgot Password",
                textAlign = TextAlign.Center,
                style = styleMedium16sp.copy(
                    textDecoration = TextDecoration.Underline,
                )
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                text = "OR",
                textAlign = TextAlign.Center,
                style = styleMedium16sp.copy(color = grey)
            )*/
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don’t have an account yet? ",
                    style = styleMedium16sp
                )
                Text(
                    modifier = Modifier.clickable {
                        signUp.invoke()
                    },
                    text = stringResource(id = R.string.sign_up),
                    style = styleMedium16sp.copy(
                        textDecoration = TextDecoration.Underline,
                    ),
                )
            }
        }
    }
}

@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
fun LoanScreenPreview() {
    LoginScreen({ _, _ -> }, {})
}
