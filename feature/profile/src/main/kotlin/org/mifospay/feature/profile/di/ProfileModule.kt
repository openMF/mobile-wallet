package org.mifospay.feature.profile.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.profile.ProfileViewModel
import org.mifospay.feature.profile.edit.EditProfileViewModel

val ProfileModule = module {

    viewModel {
        ProfileViewModel(mUseCaseHandler = get(), fetchClientImageUseCase = get(),
            localRepository = get(), mPreferencesHelper = get())
    }
    viewModel{
        EditProfileViewModel(mUseCaseHandler = get(), mPreferencesHelper = get(),
            updateUserUseCase = get(), updateClientUseCase = get())
    }
}