package mifos.org.mobilewallet.core.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by naman on 15/8/17.
 */

public class Transaction implements Parcelable {

    String transactionId;

    long clientId;

    long accountId;

    double amount;

    String date;

    Currency currency;

    TransactionType transactionType;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.transactionId);
        dest.writeLong(this.clientId);
        dest.writeLong(this.accountId);
        dest.writeDouble(this.amount);
        dest.writeString(this.date);
        dest.writeParcelable(this.currency, flags);
        dest.writeInt(this.transactionType == null ? -1 : this.transactionType.ordinal());
    }

    public Transaction() {
    }

    protected Transaction(Parcel in) {
        this.transactionId = in.readString();
        this.clientId = in.readLong();
        this.accountId = in.readLong();
        this.amount = in.readDouble();
        this.date = in.readString();
        this.currency = in.readParcelable(Currency.class.getClassLoader());
        int tmpTransactionType = in.readInt();
        this.transactionType = tmpTransactionType == -1 ? null : TransactionType.values()[tmpTransactionType];
    }

    public static final Parcelable.Creator<Transaction> CREATOR = new Parcelable.Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel source) {
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}
