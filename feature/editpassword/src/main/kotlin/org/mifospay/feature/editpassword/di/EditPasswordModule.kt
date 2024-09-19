package org.mifospay.feature.editpassword.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.editpassword.EditPasswordViewModel

val EditPasswordModule = module {
    viewModel {
        EditPasswordViewModel(
            mUseCaseHandler = get(), mPreferencesHelper = get(), authenticateUserUseCase = get(),
            updateUserUseCase = get(),
        )
    }
}