package org.mifos.mobilewallet.core.domain.usecase.user

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.entity.UserWithRole
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.utils.Constants
import rx.Subscriber
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
            .subscribe(object : Subscriber<List<UserWithRole>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(e.toString())
                }

                override fun onNext(userWithRoles: List<UserWithRole>) {
                    val tbp: MutableList<UserWithRole> = ArrayList()
                    for (userWithRole in userWithRoles) {
                        for (role in userWithRole.selectedRoles) {
                            if (role.name == Constants.MERCHANT) {
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
    class ResponseValue(
        val userWithRoleList: List<UserWithRole>
    ) : UseCase.ResponseValue
}
