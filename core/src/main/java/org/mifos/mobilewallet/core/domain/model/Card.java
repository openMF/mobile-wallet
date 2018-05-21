package org.mifos.mobilewallet.core.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ankur on 21/May/2018
 */

public class Card implements Parcelable {

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
    @SerializedName("cardNumber")
    String cardNumber;
    @SerializedName("cvv")
    String cvv;
    @SerializedName("expiryDate")
    String expiryDate;

    public Card() {
    }

    public Card(String cardNumber, String cvv, String expiryDate) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
    }

    protected Card(Parcel in) {
        this.cardNumber = in.readString();
        this.cvv = in.readString();
        this.expiryDate = in.readString();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cardNumber);
        dest.writeString(this.cvv);
        dest.writeString(this.expiryDate);
    }

    //
//    private String cardNumber;
//    private String cvv;
//
//    public Card(String cardNumber, String cvv) {
//        this.cardNumber = cardNumber;
//        this.cvv = cvv;
//    }
//
//    public String getCardNumber() {
//        return cardNumber;
//    }
//
//    public String getCvv() {
//        return cvv;
//    }
}
