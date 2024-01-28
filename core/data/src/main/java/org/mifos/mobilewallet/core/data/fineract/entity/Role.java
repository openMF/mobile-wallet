package org.mifos.mobilewallet.core.data.fineract.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ankur on 11/June/2018
 */

public class Role implements Parcelable {

    public static final Creator<Role> CREATOR = new Creator<Role>() {
        @Override
        public Role createFromParcel(Parcel in) {
            return new Role(in);
        }

        @Override
        public Role[] newArray(int size) {
            return new Role[size];
        }
    };
    private String id;
    private String name;
    private String description;

    protected Role(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
    }
}
