package org.mifos.mobilewallet.core.domain.usecase.account;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.TPTResponse;
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingAccount;
import org.mifos.mobilewallet.core.data.fineract.entity.beneficary.Beneficiary;
import org.mifos.mobilewallet.core.data.fineract.entity.beneficary.BeneficiaryPayload;
import org.mifos.mobilewallet.core.data.fineract.entity.beneficary.BeneficiaryUpdatePayload;
import org.mifos.mobilewallet.core.data.fineract.entity.client.Client;
import org.mifos.mobilewallet.core.data.fineract.entity.client.ClientAccounts;
import org.mifos.mobilewallet.core.data.fineract.entity.payload.TransferPayload;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.utils.Constants;
import org.mifos.mobilewallet.core.utils.DateHelper;

import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 29/8/17.
 */

public class TransferFunds extends UseCase<TransferFunds.RequestValues,
        TransferFunds.ResponseValue> {

    private final FineractRepository apiRepository;

    private RequestValues requestValues;
    private Client fromClient, toClient;
    private SavingAccount fromAccount, toAccount;

    @Inject
    public TransferFunds(FineractRepository apiRepository) {
        this.apiRepository = apiRepository;
    }


    @Override
    protected void executeUseCase(final RequestValues requestValues) {

        /*
         * - get fromClient head office name
         * - get toClient head office name
         * - get wallet account of fromClient
         * - get wallet account of toClient
         * - check if toClient exists as beneficiary
         * - if not, add toClient as beneficiary with the transfer
         *   limit of amount to be transferred
         * - if beneficiary exists, check if transfer limit is greater
         *   than the amount to be transferred, if not update the transfer limit to the amount
         * - initiate third party account transfer
         */

        this.requestValues = requestValues;
        apiRepository.getSelfClientDetails(requestValues.fromClientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Client>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(Constants.ERROR_FETCHING_CLIENT_DATA);
                    }

                    @Override
                    public void onNext(Client client) {
                        fromClient = client;
                        fetchToClientDetails();
                    }
                });
    }

    private void fetchToClientDetails() {
        apiRepository.getClientDetails(requestValues.toClientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Client>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(Constants.ERROR_FETCHING_CLIENT_DATA);
                    }

                    @Override
                    public void onNext(Client client) {
                        toClient = client;
                        fetchFromAccountDetails();
                    }
                });
    }

    private void fetchFromAccountDetails() {
        apiRepository.getSelfAccounts(requestValues.fromClientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ClientAccounts>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(Constants.ERROR_FETCHING_FROM_ACCOUNT);
                    }

                    @Override
                    public void onNext(ClientAccounts clientAccounts) {
                        List<SavingAccount> accounts = clientAccounts.getSavingsAccounts();
                        if (accounts != null && accounts.size() != 0) {
                            SavingAccount walletAccount = null;
                            for (SavingAccount account : accounts) {
                                if (account.getProductId() ==
                                        Constants.WALLET_ACCOUNT_SAVINGS_PRODUCT_ID) {
                                    walletAccount = account;
                                    break;
                                }
                            }
                            if (walletAccount != null) {
                                fromAccount = walletAccount;
                                fetchToAccountDetails();
                            } else {
                                getUseCaseCallback().onError(Constants.NO_WALLET_FOUND);
                            }
                        } else {
                            getUseCaseCallback().onError(Constants.ERROR_FETCHING_FROM_ACCOUNT);
                        }
                    }
                });
    }

    private void fetchToAccountDetails() {
        apiRepository.getAccounts(requestValues.toClientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ClientAccounts>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(Constants.ERROR_FETCHING_TO_ACCOUNT);
                    }

                    @Override
                    public void onNext(ClientAccounts clientAccounts) {
                        List<SavingAccount> accounts = clientAccounts.getSavingsAccounts();
                        if (accounts != null && accounts.size() != 0) {
                            SavingAccount walletAccount = null;
                            for (SavingAccount account : accounts) {
                                if (account.getProductId() ==
                                        Constants.WALLET_ACCOUNT_SAVINGS_PRODUCT_ID) {
                                    walletAccount = account;
                                    break;
                                }
                            }
                            if (walletAccount != null) {
                                toAccount = walletAccount;
                                makeTransfer();
                            } else {
                                getUseCaseCallback().onError(Constants.NO_WALLET_FOUND);
                            }
                        } else {
                            getUseCaseCallback().onError(Constants.ERROR_FETCHING_TO_ACCOUNT);
                        }
                    }
                });
    }

    private void checkBeneficiary() {
        apiRepository.getBeneficiaryList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Beneficiary>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(Constants.ERROR_FETCHING_BENEFICIARIES);
                    }

                    @Override
                    public void onNext(List<Beneficiary> beneficiaries) {
                        if (beneficiaries != null) {
                            boolean exists = false;
                            for (Beneficiary beneficiary : beneficiaries) {
                                if (beneficiary.getAccountNumber()
                                        .equals(toAccount.getAccountNo())) {
                                    exists = true;
                                    if (beneficiary.getTransferLimit() >= requestValues.amount) {
                                        makeTransfer();
                                    } else {
                                        updateTransferLimit(beneficiary.getId());
                                    }
                                    break;
                                }
                            }
                            if (!exists) {
                                addBeneficiary();
                            }
                        }

                    }
                });
    }

    private void addBeneficiary() {
        BeneficiaryPayload payload = new BeneficiaryPayload();
        payload.setAccountNumber(toAccount.getAccountNo());
        payload.setName(toClient.getDisplayName());
        payload.setOfficeName(toClient.getOfficeName());
        payload.setTransferLimit((int) requestValues.amount);
        payload.setAccountType(2);

        apiRepository.createBeneficiary(payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(Constants.ERROR_ADDING_BENEFICIARY);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        makeTransfer();
                    }
                });
    }

    private void updateTransferLimit(long beneficiaryId) {
        BeneficiaryUpdatePayload updatePayload = new BeneficiaryUpdatePayload();
        updatePayload.setTransferLimit((int) requestValues.amount);
        apiRepository.updateBeneficiary(beneficiaryId, updatePayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(Constants.ERROR_ADDING_BENEFICIARY);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        makeTransfer();
                    }
                });
    }

    private void makeTransfer() {
        TransferPayload transferPayload = new TransferPayload();
        transferPayload.setFromAccountId((int) fromAccount.getId());
        transferPayload.setFromClientId((long) fromClient.getId());
        transferPayload.setFromAccountType(2);
        transferPayload.setFromOfficeId(fromClient.getOfficeId());
        transferPayload.setToOfficeId(toClient.getOfficeId());
        transferPayload.setToAccountId((int) toAccount.getId());
        transferPayload.setToClientId((long) toClient.getId());
        transferPayload.setToAccountType(2);
        transferPayload.setTransferDate(DateHelper
                .getDateAsStringFromLong(System.currentTimeMillis()));
        transferPayload.setTransferAmount(requestValues.amount);
        transferPayload.setTransferDescription(Constants.WALLET_TRANSFER);

        apiRepository.makeThirdPartyTransfer(transferPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<TPTResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(Constants.ERROR_MAKING_TRANSFER);
                    }

                    @Override
                    public void onNext(TPTResponse responseBody) {
                        getUseCaseCallback().onSuccess(new ResponseValue());
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final long fromClientId, toClientId;
        private final double amount;

        public RequestValues(long fromClientId, long toClientId, double amount) {
            this.fromClientId = fromClientId;
            this.toClientId = toClientId;
            this.amount = amount;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }
}

