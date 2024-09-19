package org.mifospay.feature.savedcards.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.savedcards.CardsScreenViewModel

val SavedCardsModule = module {

    viewModel {
        CardsScreenViewModel(mUseCaseHandler = get(), mLocalRepository = get(), addCardUseCase =
        get(), fetchSavedCardsUseCase = get(), editCardUseCase = get(), deleteCardUseCase = get())
    }

}