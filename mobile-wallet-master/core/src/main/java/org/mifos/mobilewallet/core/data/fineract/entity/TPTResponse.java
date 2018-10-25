package org.mifos.mobilewallet.core.data.fineract.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ankur on 18/June/2018
 */

public class TPTResponse implements Parcelable {

    public static final Creator<TPTResponse> CREATOR = new Creator<TPTResponse>() {
        @Override
        public TPTResponse createFromParcel(Parcel in) {
            return new TPTResponse(in);
        }

        @Override
        public TPTResponse[] newArray(int size) {
            return new TPTResponse[size];
        }
    };
    private String savingsId;
    private String resourceId;

    protected TPTResponse(Parcel in) {
        savingsId = in.readString();
        resourceId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(savingsId);
        dest.writeString(resourceId);
    }

    public String getSavingsId() {
        return savingsId;
    }

    public void setSavingsId(String savingsId) {
        this.savingsId = savingsId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String toString() {
        return "TPTResponse{" +
                "savingsId='" + savingsId + '\'' +
                ", resourceId='" + resourceId + '\'' +
                '}';
    }
}
