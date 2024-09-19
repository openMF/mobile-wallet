package org.mifospay.feature.standing.instruction.di


import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.standing.instruction.NewSIViewModel
import org.mifospay.feature.standing.instruction.StandingInstructionDetailsViewModel
import org.mifospay.feature.standing.instruction.StandingInstructionViewModel

val StandingInstructionModule = module{

    viewModel {
        NewSIViewModel(mUseCaseHandler = get(), preferencesHelper = get(), searchClient = get(),
            createStandingTransaction = get())
    }

    viewModel{
        StandingInstructionViewModel(mUseCaseHandler = get(), localRepository = get(),
            getAllStandingInstructions = get())
    }

    viewModel{
        StandingInstructionDetailsViewModel(mUseCaseHandler = get(), fetchStandingInstruction =
        get(), updateStandingInstruction = get(), deleteStandingInstruction = get(),
            savedStateHandle = get())
    }
}