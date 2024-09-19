package org.mifospay.feature.search.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.search.SearchViewModel

val SearchModule = module {

    viewModel {
        SearchViewModel(mUseCaseHandler = get(), searchClient = get(), savedStateHandle = get())
    }

}