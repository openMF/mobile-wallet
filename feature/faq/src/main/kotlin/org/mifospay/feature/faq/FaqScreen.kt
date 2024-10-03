/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.faq

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.ui.FaqItemScreen

@Composable
internal fun FaqScreenRoute(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    faqViewModel: FAQViewModel = koinViewModel(),
) {
    FaqScreen(
        modifier = modifier,
        navigateBack = navigateBack,
        faqList = faqViewModel.getFAQ(),
    )
}

@Composable
private fun FaqScreen(
    navigateBack: () -> Unit,
    faqList: List<FAQ>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        MifosTopBar(
            topBarTitle = R.string.feature_faq,
            backPress = { navigateBack.invoke() },
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            itemsIndexed(items = faqList) { _, faqItem ->
                FaqItemScreen(
                    question = stringResource(id = faqItem.question),
                    answer = faqItem.answer?.let { stringResource(id = it) },
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun FaqScreenPreview() {
    FaqScreen(
        {},
        listOf(
            FAQ(R.string.feature_faq_question1, R.string.feature_faq_answer1),
            FAQ(R.string.feature_faq_question2, R.string.feature_faq_answer2),
            FAQ(R.string.feature_faq_question3, R.string.feature_faq_answer3),
            FAQ(R.string.feature_faq_question4, R.string.feature_faq_answer4),
        ),
    )
}
