package org.mifospay.feature.settings.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.settings.SettingsViewModel

val SettingsModule = module{

    viewModel {
        SettingsViewModel(mUseCaseHandler = get(), mLocalRepository = get(),
            blockUnblockCommandUseCase = get())
    }

}