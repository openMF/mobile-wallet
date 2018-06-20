package org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ankur on 05/June/2018
 */

public class Transfer implements Parcelable {

    public static final Creator<Transfer> CREATOR = new Creator<Transfer>() {
        @Override
        public Transfer createFromParcel(Parcel source) {
            return new Transfer(source);
        }

        @Override
        public Transfer[] newArray(int size) {
            return new Transfer[size];
        }
    };
    @SerializedName("id")
    long id;

    protected Transfer(Parcel in) {
        this.id = in.readLong();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id='" + id + '\'' +
                '}';
    }
}
