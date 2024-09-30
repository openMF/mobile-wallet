/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExpiryDateInput(
    date: String,
    onDateChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (b, c) = FocusRequester.createRefs()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        BasicTextField(
            modifier = Modifier
                .focusRequester(b)
                .focusProperties {
                    next = c
                },
            value = TextFieldValue(date, selection = TextRange(date.length)),
            onValueChange = {
                if (it.text.length == 3 && it.text[2] != '/') {
                    val newText = it.text.substring(0, 2) + '/' + it.text.substring(2)
                    onDateChange(newText)
                    return@BasicTextField
                }
                if (it.text.length > date.length) {
                    // If the user is typing at the third position, ensure it remains '/'
                    if (it.text.length >= 3 && it.text[2] != '/') {
                        val newText = it.text.substring(0, 2) + '/' + it.text.substring(3)
                        onDateChange(newText)
                        return@BasicTextField
                    }
                }
                onDateChange(it.text)
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    onDone()
                },
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            decorationBox = {
                Row(horizontalArrangement = Arrangement.Center) {
                    repeat(7) { index ->
                        FormattedDateView(
                            index = index,
                            text = date,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            },
        )
    }
}

@Composable
fun FormattedDateView(
    index: Int,
    text: String,
    modifier: Modifier = Modifier,
) {
    val isFocused = text.length == index

    val char = when {
        index == 2 -> "/"
        index == text.length -> "_"
        index > text.length -> "_"
        else -> text[index].toString()
    }
    Text(
        modifier = modifier
            .width(40.dp)
            .wrapContentHeight(align = Alignment.CenterVertically),
        text = char,
        style = MaterialTheme.typography.headlineSmall,
        color = if (isFocused) {
            Color.DarkGray
        } else {
            Color.LightGray
        },
        textAlign = TextAlign.Center,
    )
}

@DevicePreviews
@Composable
fun ExpiryDateInputPreview() {
    ExpiryDateInput(date = "", onDateChange = {}, onDone = {})
}
