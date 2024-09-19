package org.mifospay.feature.faq.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.faq.FAQViewModel

val FaqModule = module{

    viewModel {
        FAQViewModel()
    }

}