/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.domain.usecase.user

import com.mifospay.core.model.entity.UserWithRole
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class FetchUsers (
    private val mFineractRepository: FineractRepository,
) : UseCase<FetchUsers.RequestValues, FetchUsers.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.users
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<UserWithRole>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(e.toString())
                }

                override fun onNext(userWithRoles: List<UserWithRole>) {
                    val tbp: MutableList<UserWithRole> = ArrayList()
                    for (userWithRole in userWithRoles) {
                        for ((_, name) in userWithRole.selectedRoles!!) {
                            if (name == Constants.MERCHANT) {
                                tbp.add(userWithRole)
                                break
                            }
                        }
                    }
                    useCaseCallback.onSuccess(ResponseValue(tbp))
                }
            })
    }

    class RequestValues : UseCase.RequestValues
    data class ResponseValue(val userWithRoleList: List<UserWithRole>) : UseCase.ResponseValue
}
