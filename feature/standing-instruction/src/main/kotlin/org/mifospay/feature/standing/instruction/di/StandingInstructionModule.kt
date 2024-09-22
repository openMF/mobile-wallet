/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.standing.instruction.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.standing.instruction.NewSIViewModel
import org.mifospay.feature.standing.instruction.StandingInstructionDetailsViewModel
import org.mifospay.feature.standing.instruction.StandingInstructionViewModel

val StandingInstructionModule = module {

    viewModel {
        NewSIViewModel(
            mUseCaseHandler = get(),
            preferencesHelper = get(),
            searchClient = get(),
            createStandingTransaction = get(),
        )
    }

    viewModel {
        StandingInstructionViewModel(
            mUseCaseHandler = get(),
            localRepository = get(),
            getAllStandingInstructions = get(),
        )
    }

    viewModel {
        StandingInstructionDetailsViewModel(
            mUseCaseHandler = get(),
            fetchStandingInstruction =
            get(),
            updateStandingInstruction = get(),
            deleteStandingInstruction = get(),
            savedStateHandle = get(),
        )
    }
}
