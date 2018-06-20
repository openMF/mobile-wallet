package org.mifos.mobilewallet.core.domain.model.twofactor;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ankur on 01/June/2018
 */

public class AccessToken implements Parcelable {

    public static final Creator<AccessToken> CREATOR = new Creator<AccessToken>() {
        @Override
        public AccessToken createFromParcel(Parcel source) {
            return new AccessToken(source);
        }

        @Override
        public AccessToken[] newArray(int size) {
            return new AccessToken[size];
        }
    };
    private String token;
    private Long validFrom;
    private Long validTo;

    public AccessToken() {
    }

    protected AccessToken(Parcel in) {
        this.token = in.readString();
        this.validFrom = in.readLong();
        this.validTo = in.readLong();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Long validFrom) {
        this.validFrom = validFrom;
    }

    public Long getValidTo() {
        return validTo;
    }

    public void setValidTo(Long validTo) {
        this.validTo = validTo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
        dest.writeLong(this.validFrom);
        dest.writeLong(this.validTo);
    }
}
