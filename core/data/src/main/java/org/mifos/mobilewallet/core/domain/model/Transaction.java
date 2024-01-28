package org.mifos.mobilewallet.core.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.TransferDetail;

/**
 * Created by naman on 15/8/17.
 */

public class Transaction implements Parcelable {

    public static final Creator<Transaction> CREATOR = new
            Creator<Transaction>() {
                @Override
                public Transaction createFromParcel(Parcel source) {
                    return new Transaction(source);
                }

                @Override
                public Transaction[] newArray(int size) {
                    return new Transaction[size];
                }
            };
    String transactionId;
    long clientId;
    long accountId;
    double amount;
    String date;
    Currency currency;
    TransactionType transactionType;
    long transferId;
    TransferDetail transferDetail;
    String receiptId;

    protected Transaction(Parcel in) {
        this.transactionId = in.readString();
        this.clientId = in.readLong();
        this.accountId = in.readLong();
        this.amount = in.readDouble();
        this.date = in.readString();
        this.currency = in.readParcelable(Currency.class.getClassLoader());
        int tmpTransactionType = in.readInt();
        this.transactionType = tmpTransactionType == -1 ? null :
                TransactionType.values()[tmpTransactionType];
        this.transferId = in.readLong();
        this.transferDetail = in.readParcelable(TransferDetail.class.getClassLoader());
        this.receiptId = in.readString();
    }

    public Transaction() {
    }

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

    public long getTransferId() {
        return transferId;
    }

    public void setTransferId(long transferId) {
        this.transferId = transferId;
    }

    public TransferDetail getTransferDetail() {
        return transferDetail;
    }

    public void setTransferDetail(
            TransferDetail transferDetail) {
        this.transferDetail = transferDetail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
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
        dest.writeLong(this.transferId);
        dest.writeParcelable(this.transferDetail, flags);
        dest.writeString(this.receiptId);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", transferId=" + transferId +
                ", transferDetail=" + transferDetail +
                ", receiptId=" + receiptId +
                '}';
    }
}
