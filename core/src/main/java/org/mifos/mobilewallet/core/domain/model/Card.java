package org.mifos.mobilewallet.core.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ankur on 21/May/2018
 */

public class Card implements Parcelable {

    @SerializedName("cardNumber")
    String cardNumber;

    @SerializedName("cvv")
    String cvv;

    @SerializedName("expiryDate")
    String expiryDate;

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel source) {
            return new Card(source);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    public Card() {
    }

    public Card(String cardNumber, String cvv, String expiryDate) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
    }

    @SerializedName("bankName")
    String bankName;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    protected Card(Parcel in) {
        this.cardNumber = in.readString();
        this.cvv = in.readString();
        this.expiryDate = in.readString();
        this.bankName = in.readString();
    }

    public String getBankName() {
        return bankName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cardNumber);
        dest.writeString(this.cvv);
        dest.writeString(this.expiryDate);
        dest.writeString(this.bankName);
    }

}
