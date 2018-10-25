package org.mifos.mobilewallet.core.data.fineract.entity.kyc;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ankur on 24/May/2018
 */

public class KYCLevel1Details implements Parcelable {

    public static final Creator<KYCLevel1Details> CREATOR =
            new Creator<KYCLevel1Details>() {
                @Override
                public KYCLevel1Details createFromParcel(Parcel source) {
                    return new KYCLevel1Details(source);
                }

                @Override
                public KYCLevel1Details[] newArray(int size) {
                    return new KYCLevel1Details[size];
                }
            };
    @SerializedName("firstName")
    String firstName;
    @SerializedName("lastName")
    String lastName;
    @SerializedName("addressLine1")
    String addressLine1;
    @SerializedName("addressLine2")
    String addressLine2;
    @SerializedName("mobileNo")
    String mobileNo;
    @SerializedName("dob")
    String dob;
    @SerializedName("currentLevel")
    String currentLevel;


    public KYCLevel1Details(String firstName, String lastName, String addressLine1,
            String addressLine2, String mobileNo, String dob, String currentLevel) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.mobileNo = mobileNo;
        this.dob = dob;
        this.currentLevel = currentLevel;
    }

    protected KYCLevel1Details(Parcel in) {
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.addressLine1 = in.readString();
        this.addressLine2 = in.readString();
        this.mobileNo = in.readString();
        this.dob = in.readString();
        this.currentLevel = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.addressLine1);
        dest.writeString(this.addressLine2);
        dest.writeString(this.mobileNo);
        dest.writeString(this.dob);
        dest.writeString(this.currentLevel);
    }

    @Override
    public String toString() {
        return "KYCLevel1Details{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", dob='" + dob + '\'' +
                ", currentLevel='" + currentLevel + '\'' +
                '}';
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(String currentLevel) {
        this.currentLevel = currentLevel;
    }
}
