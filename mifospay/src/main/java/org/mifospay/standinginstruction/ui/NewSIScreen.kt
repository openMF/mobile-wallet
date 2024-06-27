package org.mifospay.standinginstruction.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mifospay.R
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosOutlinedTextField
import org.mifospay.core.designsystem.component.MifosScaffold

@Composable
fun NewSIScreen(modifier: Modifier = Modifier) {
    var amount by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    var vpa by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }

    var siInterval by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }

    MifosScaffold(
        topBarTitle = R.string.tile_si_activity,
        backPress = { /*TODO*/ },
        scaffoldContent = {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(it)) {
                MifosOutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    value = amount,
                    onValueChange = {
                        amount = it
                    },
                    label = R.string.amount
                )
                Spacer(modifier = Modifier.padding(top = 16.dp))
                MifosOutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    value = vpa,
                    onValueChange = {
                        vpa = it
                    },
                    label = R.string.vpa,
                )
                Spacer(modifier = Modifier.padding(top = 16.dp))
                MifosOutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    value = siInterval,
                    onValueChange = {
                        siInterval = it
                    },
                    label = R.string.recurrence_interval_in_months
                )
                Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.Center) {
                    Text(text = stringResource(id = R.string.valid_till))
                }
                MifosButton(
                    modifier = Modifier
                        .width(150.dp)
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    onClick = {}
                ) {
                    Text(text = stringResource(R.string.select_date), color = Color.White)
                }
                MifosButton(
                    modifier = Modifier
                        .width(150.dp)
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    onClick = {}
                ) {
                    Text(text = stringResource(id = R.string.submit), color = Color.White)
                }
            }
        })
}

@Preview
@Composable
private fun NewSIScreenPreview() {
    NewSIScreen()
}