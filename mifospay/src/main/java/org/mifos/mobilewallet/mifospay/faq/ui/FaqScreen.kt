package org.mifos.mobilewallet.mifospay.faq.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.FaqItemScreen
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosTopBar
import org.mifos.mobilewallet.mifospay.faq.presenter.FAQViewModel
import org.mifos.mobilewallet.mifospay.utils.FAQUtils.getFAQPreviewList

@Composable
fun FaqScreen(
    navigateBack: () -> Unit,
    faqViewModel: FAQViewModel = hiltViewModel()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MifosTopBar(
            topBarTitle = R.string.frequently_asked_questions,
            backPress = { navigateBack.invoke() })
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            itemsIndexed(items = faqViewModel.getFAQ()) { _, faqItem ->
                FaqItemScreen(
                    question = stringResource(id = faqItem.question),
                    answer = faqItem.answer?.let { stringResource(id = it) }
                )
            }
        }
    }
}

@Composable
fun FaqScreen(
    navigateBack: () -> Unit,
    faqList: List<FAQ>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MifosTopBar(
            topBarTitle = R.string.frequently_asked_questions,
            backPress = { navigateBack.invoke() })
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            itemsIndexed(items = faqList) { _, faqItem ->
                FaqItemScreen(
                    question = stringResource(id = faqItem.question),
                    answer = faqItem.answer?.let { stringResource(id = it) }
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun FaqScreenPreview() {
    FaqScreen({}, getFAQPreviewList())
}
