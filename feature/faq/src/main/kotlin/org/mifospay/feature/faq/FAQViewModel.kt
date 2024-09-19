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

import androidx.lifecycle.ViewModel

internal class FAQViewModel : ViewModel() {

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
            FAQ(R.string.feature_faq_question4, R.string.feature_faq_answer4),
        )
    }
}
