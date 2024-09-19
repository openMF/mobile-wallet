package org.mifospay.feature.kyc.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.kyc.KYCDescriptionViewModel
import org.mifospay.feature.kyc.KYCLevel1ViewModel
import org.mifospay.feature.kyc.KYCLevel2ViewModel
import org.mifospay.feature.kyc.KYCLevel3ViewModel

val KYCModule = module{

    viewModel {
        KYCDescriptionViewModel(mUseCaseHandler = get(), mLocalRepository = get(),
            fetchKYCLevel1DetailsUseCase = get())
    }
    viewModel {
        KYCLevel1ViewModel(mUseCaseHandler = get(), mLocalRepository = get(),
            uploadKYCLevel1DetailsUseCase = get())
    }

    viewModel {
        KYCLevel2ViewModel(mUseCaseHandler = get(), preferencesHelper = get(),
            uploadKYCDocsUseCase = get())

    }

    viewModel{
        KYCLevel3ViewModel(mUseCaseHandler = get(), mLocalRepository = get())

    }

}