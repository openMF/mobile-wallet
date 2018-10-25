package org.mifos.mobilewallet.core.data.fineract.entity.payload;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class DataTablePayload implements Parcelable {

    public static final Creator<DataTablePayload> CREATOR = new
            Creator<DataTablePayload>() {
                @Override
                public DataTablePayload createFromParcel(Parcel source) {
                    return new DataTablePayload(source);
                }

                @Override
                public DataTablePayload[] newArray(int size) {
                    return new DataTablePayload[size];
                }
            };
    transient Integer id;
    transient Long clientCreationTime;
    transient String dataTableString;
    String registeredTableName;
    String applicationTableName;
    HashMap<String, Object> data;

    public DataTablePayload() {
    }

    protected DataTablePayload(Parcel in) {
        this.registeredTableName = in.readString();
        this.data = (HashMap<String, Object>) in.readSerializable();
    }

    public Long getClientCreationTime() {
        return clientCreationTime;
    }

    public void setClientCreationTime(Long clientCreationTime) {
        this.clientCreationTime = clientCreationTime;
    }

    public String getDataTableString() {
        return dataTableString;
    }

    public void setDataTableString(String dataTableString) {
        this.dataTableString = dataTableString;
    }

    public String getRegisteredTableName() {
        return registeredTableName;
    }

    public void setRegisteredTableName(String registeredTableName) {
        this.registeredTableName = registeredTableName;
    }

    public void setApplicationTableName(String applicationTableName) {
        this.applicationTableName = applicationTableName;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.registeredTableName);
        dest.writeSerializable(this.data);
    }
}
