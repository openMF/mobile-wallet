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
import androidx.hilt.navigation.compose.hiltViewModel
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.ui.FaqItemScreen

@Composable
fun FaqScreenRoute(
    navigateBack: () -> Unit,
    faqViewModel: FAQViewModel = hiltViewModel()
) {
    FaqScreen(navigateBack = { navigateBack.invoke() }, faqViewModel.getFAQ())
}

@Composable
fun FaqScreen(
    navigateBack: () -> Unit,
    faqList: List<FAQ>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MifosTopBar(
            topBarTitle = R.string.feature_faq_frequently_asked_questions,
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
    FaqScreen(
        {}, listOf(
            FAQ(R.string.feature_faq_question1, R.string.feature_faq_answer1),
            FAQ(R.string.feature_faq_question2, R.string.feature_faq_answer2),
            FAQ(R.string.feature_faq_question3, R.string.feature_faq_answer3),
            FAQ(R.string.feature_faq_question4, R.string.feature_faq_answer4)
        )
    )
}
