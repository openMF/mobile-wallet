package org.mifospay.feature.invoices.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.invoices.InvoiceDetailViewModel
import org.mifospay.feature.invoices.InvoicesViewModel

val InvoicesModule = module{

    viewModel {
        InvoiceDetailViewModel(mUseCaseHandler = get(), mPreferencesHelper = get(),
            fetchInvoiceUseCase = get(), savedStateHandle = get())
    }

    viewModel{
        InvoicesViewModel(mUseCaseHandler = get(), mPreferencesHelper = get(),
            fetchInvoicesUseCase = get())
    }
}