package org.mifos.mobilewallet.core.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by naman on 11/7/17.
 */

public class Account implements Parcelable {

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
    private String image;
    private String name;
    private String number;
    private double balance;
    private long id;
    private long productId;
    private Currency currency;

    public Account() {
    }

    protected Account(Parcel in) {
        this.image = in.readString();
        this.name = in.readString();
        this.number = in.readString();
        this.balance = in.readDouble();
        this.id = in.readLong();
        this.productId = in.readLong();
        this.currency = in.readParcelable(Currency.class.getClassLoader());
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.image);
        dest.writeString(this.name);
        dest.writeString(this.number);
        dest.writeDouble(this.balance);
        dest.writeLong(this.id);
        dest.writeLong(this.productId);
        dest.writeParcelable(this.currency, flags);
    }
}
