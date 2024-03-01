package org.mifos.mobilewallet.core.domain.usecase.user


import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import com.mifos.mobilewallet.model.entity.Role
import com.mifos.mobilewallet.model.entity.UserWithRole
import org.mifos.mobilewallet.core.utils.Constants
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

import javax.inject.Inject

/**
 * Created by ankur on 11/June/2018
 */
class FetchUsers @Inject constructor(private val mFineractRepository: FineractRepository) :
    UseCase<FetchUsers.RequestValues, FetchUsers.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.users
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { userWithRoles ->
                val tbp = userWithRoles.filter { userWithRole ->
                    userWithRole.selectedRoles!!.any { role ->
                        role.name == Constants.MERCHANT
                    }
                }
                useCaseCallback.onSuccess(ResponseValue(tbp))
            }
    }

    class RequestValues : UseCase.RequestValues

    class ResponseValue(val userWithRoleList: List<UserWithRole>) :
        UseCase.ResponseValue
}
