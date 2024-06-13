package org.mifospay.feature.faq

import androidx.lifecycle.ViewModel
import com.example.faq.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FAQViewModel @Inject constructor() : ViewModel() {

    /**
     * Retrieves a list of Frequently Asked Questions (FAQs).
     *
     * Currently, the FAQs are statically defined within this function. This is a temporary
     * implementation for demonstration or testing purposes. In the future, this method will
     * be updated to fetch the FAQ data from a backend service once the backend functionality
     * is implemented. This will allow for dynamic and up-to-date FAQ content.
     *
     * @return A list of [FAQ] objects containing the questions and answers.
     */
    fun getFAQ(): List<FAQ> {
        return listOf(
            FAQ(R.string.feature_faq_question1, R.string.feature_faq_answer1),
            FAQ(R.string.feature_faq_question2, R.string.feature_faq_answer2),
            FAQ(R.string.feature_faq_question3, R.string.feature_faq_answer3),
            FAQ(R.string.feature_faq_question4, R.string.feature_faq_answer4)
        )
    }
}