package org.mifos.mobilewallet.mifospay.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    var userName by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
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
                text = "Login",
                style = styleMedium30sp
            )
            Text(
                modifier = Modifier
                    .padding(top = 32.dp),
                text = "Welcome back!",
                style = styleNormal18sp.copy(color = grey)
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                value = userName,
                onValueChange = {
                    userName = it
                },
                label = { Text("Username") }
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                value = password,
                onValueChange = {
                    password = it
                },
                label = { Text("Password") }
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                enabled = userName.isNotEmpty() && password.isNotEmpty(),
                onClick = {
                    login.invoke(userName, password)
                }
            ) {
                Text(text = "Login", style = styleMedium16sp.copy(color = Color.White))
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
                    text = "Sign up.",
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
