package org.mifos.mobilewallet.core.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ankur on 09/July/2018
 */

public class BankAccountDetails implements Parcelable {

    public static final Creator<BankAccountDetails> CREATOR = new Creator<BankAccountDetails>() {
        @Override
        public BankAccountDetails createFromParcel(Parcel in) {
            return new BankAccountDetails(in);
        }

        @Override
        public BankAccountDetails[] newArray(int size) {
            return new BankAccountDetails[size];
        }
    };
    private String bankName;
    private String accountholderName;
    private String branch;
    private String ifsc;
    private String type;
    private boolean upiEnabled;
    private String upiPin;

    public BankAccountDetails() {
    }


    public BankAccountDetails(String bankName, String accountholderName, String branch,
            String ifsc, String type) {
        this.bankName = bankName;
        this.accountholderName = accountholderName;
        this.branch = branch;
        this.ifsc = ifsc;
        this.type = type;
    }

    protected BankAccountDetails(Parcel in) {
        bankName = in.readString();
        accountholderName = in.readString();
        branch = in.readString();
        ifsc = in.readString();
        type = in.readString();
        upiEnabled = in.readByte() != 0;
        upiPin = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bankName);
        dest.writeString(accountholderName);
        dest.writeString(branch);
        dest.writeString(ifsc);
        dest.writeString(type);
        dest.writeByte((byte) (upiEnabled ? 1 : 0));
        dest.writeString(upiPin);
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountholderName() {
        return accountholderName;
    }

    public void setAccountholderName(String accountholderName) {
        this.accountholderName = accountholderName;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isUpiEnabled() {
        return upiEnabled;
    }

    public void setUpiEnabled(boolean upiEnabled) {
        this.upiEnabled = upiEnabled;
    }

    public String getUpiPin() {
        return upiPin;
    }

    public void setUpiPin(String upiPin) {
        this.upiPin = upiPin;
    }

    @Override
    public String toString() {
        return "BankAccountDetails{" +
                "bankName='" + bankName + '\'' +
                ", accountholderName='" + accountholderName + '\'' +
                ", branch='" + branch + '\'' +
                ", ifsc='" + ifsc + '\'' +
                ", type='" + type + '\'' +
                ", upiEnabled=" + upiEnabled +
                ", upiPin='" + upiPin + '\'' +
                '}';
    }
}
