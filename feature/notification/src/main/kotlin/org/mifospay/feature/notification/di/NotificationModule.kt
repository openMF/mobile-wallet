package org.mifospay.feature.notification.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mifospay.feature.notification.NotificationViewModel

val NotificationModule = module{

    viewModel {
        NotificationViewModel(mUseCaseHandler = get(), mLocalRepository = get(), fetchNotificationsUseCase = get())
    }

}