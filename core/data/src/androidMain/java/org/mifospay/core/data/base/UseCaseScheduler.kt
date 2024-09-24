/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.base

import org.mifospay.core.data.base.UseCase.UseCaseCallback

/**
 * Interface for schedulers, see [UseCaseThreadPoolScheduler].
 */
interface UseCaseScheduler {
    fun execute(runnable: Runnable?)
    fun <V : UseCase.ResponseValue?> notifyResponse(
        response: V,
        useCaseCallback: UseCaseCallback<V>?,
    )

    fun <V : UseCase.ResponseValue?> onError(
        message: String?,
        useCaseCallback: UseCaseCallback<V>?,
    )
}
