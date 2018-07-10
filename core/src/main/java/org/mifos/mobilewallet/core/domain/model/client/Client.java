package org.mifos.mobilewallet.core.domain.model.client;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by naman on 17/6/17.
 */

public class Client implements Parcelable {

    public static final Creator<Client> CREATOR = new Creator<Client>() {
        @Override
        public Client createFromParcel(Parcel source) {
            return new Client(source);
        }

        @Override
        public Client[] newArray(int size) {
            return new Client[size];
        }
    };
    private String name;
    private String image;
    private String externalId;
    private long clientId;
    private String displayName;
    private String mobileNo;

    protected Client(Parcel in) {
        this.name = in.readString();
        this.image = in.readString();
        this.externalId = in.readString();
        this.clientId = in.readLong();
        this.displayName = in.readString();
        this.mobileNo = in.readString();
    }

    public Client() {
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

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.image);
        dest.writeString(this.externalId);
        dest.writeLong(this.clientId);
        dest.writeString(this.displayName);
        dest.writeString(this.mobileNo);
    }

    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", externalId='" + externalId + '\'' +
                ", clientId=" + clientId +
                ", displayName='" + displayName + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                '}';
    }
}
