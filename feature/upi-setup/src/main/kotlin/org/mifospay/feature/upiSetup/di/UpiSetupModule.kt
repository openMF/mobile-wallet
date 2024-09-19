package org.mifospay.feature.upiSetup.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.upiSetup.viewmodel.DebitCardViewModel
import org.mifospay.feature.upiSetup.viewmodel.SetUpUpiViewModal

val UpiSetupModule = module{

    viewModel {
        DebitCardViewModel()
    }

    viewModel {
        SetUpUpiViewModal()
    }

}