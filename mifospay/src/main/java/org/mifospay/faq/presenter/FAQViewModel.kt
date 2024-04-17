package org.mifospay.faq.presenter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.mifospay.R
import org.mifospay.faq.ui.FAQ
import javax.inject.Inject

@HiltViewModel
class FAQViewModel @Inject constructor() : ViewModel() {

    fun getFAQ(): List<FAQ> {
        return listOf(
            FAQ(R.string.question1, R.string.answer1),
            FAQ(R.string.question2, R.string.answer2),
            FAQ(R.string.question3, R.string.answer3),
            FAQ(R.string.question4, R.string.answer4)
        )
    }
}