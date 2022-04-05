package org.mifos.mobilewallet.mifospay.bank.presenter;

import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.model.BankAccountDetails;
import org.mifos.mobilewallet.mifospay.bank.BankContract;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

/**
 * Created by ankur on 09/July/2018
 */

public class BankAccountsPresenter implements BankContract.BankAccountsPresenter {

    private static final Random mRandom = new Random();
    private final LocalRepository mLocalRepository;
    private final UseCaseHandler mUseCaseHandler;
    BankContract.BankAccountsView mBankAccountsView;

    @Inject
    public BankAccountsPresenter(LocalRepository localRepository, UseCaseHandler useCaseHandler) {
        mLocalRepository = localRepository;
        mUseCaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mBankAccountsView = (BankContract.BankAccountsView) baseView;
        mBankAccountsView.setPresenter(this);
    }

    @Override
    public void fetchLinkedBankAccounts() {
        // TODO:: fetch linked bank accounts

        List<BankAccountDetails> bankAccountDetailsList = new ArrayList<>();
        bankAccountDetailsList.add(new BankAccountDetails("SBI", "Ankur Sharma", "New Delhi",
                mRandom.nextInt() + " ", "Savings"));
        bankAccountDetailsList.add(new BankAccountDetails("HDFC", "Mandeep Singh ", "Uttar Pradesh",
                mRandom.nextInt() + " ", "Savings"));
        bankAccountDetailsList.add(new BankAccountDetails("ANDHRA", "Rakesh anna ", "Telegana",
                mRandom.nextInt() + " ", "Savings"));
        bankAccountDetailsList.add(new BankAccountDetails("PNB", "luv Pro ", "Gujrat",
                mRandom.nextInt() + " ", "Savings"));
        bankAccountDetailsList.add(new BankAccountDetails("HDF", "Harry potter ", "Hogwarts",
                mRandom.nextInt() + " ", "Savings"));
        bankAccountDetailsList.add(new BankAccountDetails("GCI", "JIGME ", "JAMMU",
                mRandom.nextInt() + " ", "Savings"));
        bankAccountDetailsList.add(new BankAccountDetails("FCI", "NISHU BOII ", "ASSAM",
                mRandom.nextInt() + " ", "Savings"));

        mBankAccountsView.showLinkedBankAccounts(bankAccountDetailsList);
    }
}

