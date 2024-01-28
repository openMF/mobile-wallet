package org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.mifos.mobilewallet.core.domain.model.Account;
import org.mifos.mobilewallet.core.domain.model.client.Client;

/**
 * Created by ankur on 05/June/2018
 */

public class TransferDetail implements Parcelable {

    public static final Creator<TransferDetail> CREATOR = new Creator<TransferDetail>() {
        @Override
        public TransferDetail createFromParcel(Parcel in) {
            return new TransferDetail(in);
        }

        @Override
        public TransferDetail[] newArray(int size) {
            return new TransferDetail[size];
        }
    };
    @SerializedName("id")
    long id;
    @SerializedName("fromClient")
    Client fromClient;
    @SerializedName("fromAccount")
    SavingAccount fromAccount;
    @SerializedName("toClient")
    Client toClient;
    @SerializedName("toAccount")
    SavingAccount toAccount;

    protected TransferDetail(Parcel in) {
        this.id = in.readLong();
        this.fromClient = in.readParcelable(Client.class.getClassLoader());
        this.fromAccount = in.readParcelable(Account.class.getClassLoader());
        this.toClient = in.readParcelable(Client.class.getClassLoader());
        this.toAccount = in.readParcelable(Account.class.getClassLoader());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Client getFromClient() {
        return fromClient;
    }

    public void setFromClient(Client fromClient) {
        this.fromClient = fromClient;
    }

    public SavingAccount getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(
            SavingAccount fromAccount) {
        this.fromAccount = fromAccount;
    }

    public Client getToClient() {
        return toClient;
    }

    public void setToClient(Client toClient) {
        this.toClient = toClient;
    }

    public SavingAccount getToAccount() {
        return toAccount;
    }

    public void setToAccount(
            SavingAccount toAccount) {
        this.toAccount = toAccount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeParcelable(this.fromClient, flags);
        dest.writeParcelable(this.fromAccount, flags);
        dest.writeParcelable(this.toClient, flags);
        dest.writeParcelable(this.toAccount, flags);
    }

    @Override
    public String toString() {
        return "TransferDetail{" +
                "id='" + id + '\'' +
                ", fromClient=" + fromClient +
                ", fromAccount=" + fromAccount +
                ", toClient=" + toClient +
                ", toAccount=" + toAccount +
                '}';
    }
}
