package org.mifos.mobilewallet.mifospay.utils

import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.faq.ui.FAQ

object FAQUtils {
    fun getFAQPreviewList(): List<FAQ> {
        return listOf(
            FAQ(R.string.question1, R.string.answer1),
            FAQ(R.string.question2, R.string.answer2),
            FAQ(R.string.question3, R.string.answer3),
            FAQ(R.string.question4, R.string.answer4)
        )
    }
}