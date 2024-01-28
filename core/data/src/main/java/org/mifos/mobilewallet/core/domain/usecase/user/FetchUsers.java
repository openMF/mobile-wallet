package org.mifos.mobilewallet.core.domain.usecase.user;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.Role;
import org.mifos.mobilewallet.core.data.fineract.entity.UserWithRole;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 11/June/2018
 */

public class FetchUsers extends
        UseCase<FetchUsers.RequestValues, FetchUsers.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public FetchUsers(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mFineractRepository.getUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<UserWithRole>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(e.toString());
                    }

                    @Override
                    public void onNext(List<UserWithRole> userWithRoles) {
                        List<UserWithRole> tbp = new ArrayList<>();
                        for (UserWithRole userWithRole : userWithRoles) {
                            for (Role role : userWithRole.getSelectedRoles()) {
                                if (role.getName().equals(Constants.MERCHANT)) {
                                    tbp.add(userWithRole);
                                    break;
                                }
                            }
                        }
                        getUseCaseCallback().onSuccess(new ResponseValue(tbp));
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<UserWithRole> mUserWithRoleList;

        public ResponseValue(
                List<UserWithRole> userWithRoleList) {
            mUserWithRoleList = userWithRoleList;
        }

        public List<UserWithRole> getUserWithRoleList() {
            return mUserWithRoleList;
        }
    }
}
