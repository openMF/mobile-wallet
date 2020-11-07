package org.mifos.mobilewallet.mifospay.injection.component;


import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity;
import org.mifos.mobilewallet.mifospay.bank.fragment.DebitCardFragment;
import org.mifos.mobilewallet.mifospay.bank.fragment.OtpFragment;
import org.mifos.mobilewallet.mifospay.bank.fragment.SetupUpiPinDialog;
import org.mifos.mobilewallet.mifospay.bank.fragment.UpiPinFragment;
import org.mifos.mobilewallet.mifospay.bank.ui.BankAccountDetailActivity;
import org.mifos.mobilewallet.mifospay.bank.ui.LinkBankAccountActivity;
import org.mifos.mobilewallet.mifospay.bank.ui.SetupUpiPinActivity;
import org.mifos.mobilewallet.mifospay.common.ui.MakeTransferFragment;
import org.mifos.mobilewallet.mifospay.common.ui.SearchActivity;
import org.mifos.mobilewallet.mifospay.editprofile.ui.EditProfileActivity;
import org.mifos.mobilewallet.mifospay.bank.ui.AccountsFragment;
import org.mifos.mobilewallet.mifospay.history.ui.HistoryFragment;
import org.mifos.mobilewallet.mifospay.history.ui.SpecificTransactionsActivity;
import org.mifos.mobilewallet.mifospay.history.ui.TransactionDetailDialog;
import org.mifos.mobilewallet.mifospay.home.ui.HomeFragment;
import org.mifos.mobilewallet.mifospay.home.ui.MainActivity;
import org.mifos.mobilewallet.mifospay.home.ui.ProfileFragment;
import org.mifos.mobilewallet.mifospay.injection.PerActivity;
import org.mifos.mobilewallet.mifospay.injection.module.ActivityModule;
import org.mifos.mobilewallet.mifospay.invoice.ui.InvoiceActivity;
import org.mifos.mobilewallet.mifospay.invoice.ui.InvoicesFragment;
import org.mifos.mobilewallet.mifospay.kyc.ui.KYCDescriptionFragment;
import org.mifos.mobilewallet.mifospay.kyc.ui.KYCLevel1Fragment;
import org.mifos.mobilewallet.mifospay.kyc.ui.KYCLevel2Fragment;
import org.mifos.mobilewallet.mifospay.kyc.ui.KYCLevel3Fragment;
import org.mifos.mobilewallet.mifospay.merchants.ui.MerchantTransferActivity;
import org.mifos.mobilewallet.mifospay.merchants.ui.MerchantsFragment;
import org.mifos.mobilewallet.mifospay.notification.ui.NotificationActivity;
import org.mifos.mobilewallet.mifospay.passcode.ui.PassCodeActivity;
import org.mifos.mobilewallet.mifospay.password.ui.EditPasswordActivity;
import org.mifos.mobilewallet.mifospay.payments.ui.RequestFragment;
import org.mifos.mobilewallet.mifospay.payments.ui.SendFragment;
import org.mifos.mobilewallet.mifospay.qr.ui.ReadQrActivity;
import org.mifos.mobilewallet.mifospay.qr.ui.ShowQrActivity;
import org.mifos.mobilewallet.mifospay.receipt.ui.ReceiptActivity;
import org.mifos.mobilewallet.mifospay.registration.ui.MobileVerificationActivity;
import org.mifos.mobilewallet.mifospay.registration.ui.SignupActivity;
import org.mifos.mobilewallet.mifospay.savedcards.ui.CardsFragment;
import org.mifos.mobilewallet.mifospay.settings.ui.SettingsActivity;
import org.mifos.mobilewallet.mifospay.standinginstruction.ui.NewSIActivity;
import org.mifos.mobilewallet.mifospay.standinginstruction.ui.SIDetailsActivity;
import org.mifos.mobilewallet.mifospay.standinginstruction.ui.SIFragment;

import dagger.Component;

@PerActivity
@Component(dependencies = {ApplicationComponent.class},
        modules = {ActivityModule.class})

public interface ActivityComponent {

    void inject(LoginActivity loginActivity);

    void inject(MainActivity mainActivity);

    void inject(HomeFragment homeFragment);

    void inject(HistoryFragment HistoryFragment);

    void inject(SearchActivity searchActivity);

    void inject(ShowQrActivity showQrActivity);

    void inject(ReadQrActivity readQrActivity);

    void inject(RequestFragment requestFragment);

    void inject(ProfileFragment profileFragment);

    void inject(MakeTransferFragment transferFragment);

    void inject(AccountsFragment accountsFragment);

    void inject(CardsFragment cardsFragment);

    void inject(SendFragment sendFragment);

    void inject(PassCodeActivity passCodeActivity);

    void inject(KYCDescriptionFragment kycDescriptionFragment);

    void inject(KYCLevel1Fragment kycLevel1Fragment);

    void inject(KYCLevel2Fragment kycLevel2Fragment);

    void inject(KYCLevel3Fragment kycLevel3Fragment);

    void inject(ReceiptActivity receiptActivity);

    void inject(TransactionDetailDialog transactionDetailDialog);

    void inject(InvoiceActivity invoiceActivity);

    void inject(SpecificTransactionsActivity specificTransactionsActivity);

    void inject(InvoicesFragment invoicesFragment);

    void inject(SignupActivity signupActivity);

    void inject(MobileVerificationActivity mobileVerificationActivity);

    void inject(EditProfileActivity editProfileActivity);

    void inject(EditPasswordActivity editPasswordActivity);

    void inject(SettingsActivity settingsActivity);

    void inject(LinkBankAccountActivity linkBankAccountActivity);

    void inject(BankAccountDetailActivity bankAccountDetailActivity);

    void inject(DebitCardFragment debitCardFragment);

    void inject(OtpFragment otpFragment);

    void inject(MerchantsFragment merchantsFragment);

    void inject(UpiPinFragment upiPinFragment);

    void inject(SetupUpiPinDialog setupUpiPinDialog);

    void inject(SetupUpiPinActivity setupUpiPinActivity);

    void inject(NotificationActivity notificationActivity);

    void inject(MerchantTransferActivity merchantTransferActivity);

    void inject (NewSIActivity newSIActivity);

    void inject (SIFragment siFragment);

    void inject (SIDetailsActivity siDetailsActivity);

}
