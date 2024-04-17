package org.mifospay.bank.presenter

import com.mifospay.core.model.domain.BankAccountDetails
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.bank.BankContract
import org.mifospay.bank.BankContract.BankAccountsView
import org.mifospay.base.BaseView
import org.mifospay.data.local.LocalRepository
import java.util.Random
import javax.inject.Inject

/**
 * Created by ankur on 09/July/2018
 */
class BankAccountsPresenter @Inject constructor(
    private val mLocalRepository: LocalRepository,
    private val mUseCaseHandler: UseCaseHandler
) : BankContract.BankAccountsPresenter {
    var mBankAccountsView: BankAccountsView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mBankAccountsView = baseView as BankAccountsView?
        mBankAccountsView!!.setPresenter(this)
    }

    override fun fetchLinkedBankAccounts() {
        // TODO:: fetch linked bank accounts
        val bankAccountDetailsList: MutableList<BankAccountDetails?> = ArrayList()
        bankAccountDetailsList.add(
            BankAccountDetails(
                "SBI", "Ankur Sharma", "New Delhi",
                mRandom.nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "HDFC", "Mandeep Singh ", "Uttar Pradesh",
                mRandom.nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "ANDHRA", "Rakesh anna ", "Telegana",
                mRandom.nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "PNB", "luv Pro ", "Gujrat",
                mRandom.nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "HDF", "Harry potter ", "Hogwarts",
                mRandom.nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "GCI", "JIGME ", "JAMMU",
                mRandom.nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "FCI", "NISHU BOII ", "ASSAM",
                mRandom.nextInt().toString() + " ", "Savings"
            )
        )
        mBankAccountsView!!.showLinkedBankAccounts(bankAccountDetailsList)
    }

    companion object {
        private val mRandom = Random()
    }
}