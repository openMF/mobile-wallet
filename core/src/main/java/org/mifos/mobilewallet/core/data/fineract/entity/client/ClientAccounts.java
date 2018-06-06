package org.mifos.mobilewallet.core.data.fineract.entity.client;

import android.os.Parcel;
import android.os.Parcelable;

import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingAccount;

import java.util.ArrayList;
import java.util.List;

public class ClientAccounts implements Parcelable {

    public static final Creator<ClientAccounts> CREATOR = new Creator<ClientAccounts>() {

        @Override
        public ClientAccounts createFromParcel(Parcel source) {
            return new ClientAccounts(source);
        }

        @Override
        public ClientAccounts[] newArray(int size) {
            return new ClientAccounts[size];
        }
    };
    private List<SavingAccount> savingsAccounts = new ArrayList<>();

    public ClientAccounts() {
    }


    protected ClientAccounts(Parcel in) {
        this.savingsAccounts = new ArrayList<SavingAccount>();
    }

    public List<SavingAccount> getSavingsAccounts() {
        return savingsAccounts;
    }

    public void setSavingsAccounts(List<SavingAccount> savingsAccounts) {
        this.savingsAccounts = savingsAccounts;
    }

    public ClientAccounts withSavingsAccounts(List<SavingAccount> savingsAccounts) {
        this.savingsAccounts = savingsAccounts;
        return this;
    }

    public List<SavingAccount> getRecurringSavingsAccounts() {
        return getSavingsAccounts(true);
    }

    public List<SavingAccount> getNonRecurringSavingsAccounts() {
        return getSavingsAccounts(false);
    }

    private List<SavingAccount> getSavingsAccounts(boolean wantRecurring) {
        List<SavingAccount> result = new ArrayList<SavingAccount>();
        if (this.savingsAccounts != null) {
            for (SavingAccount account : savingsAccounts) {
                if (account.isRecurring() == wantRecurring) {
                    result.add(account);
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "ClientAccounts{" +
                ", savingsAccounts=" + savingsAccounts +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.savingsAccounts);
    }
}
