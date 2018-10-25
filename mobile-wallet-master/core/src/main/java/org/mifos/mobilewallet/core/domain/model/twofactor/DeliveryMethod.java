package org.mifos.mobilewallet.core.domain.model.twofactor;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ankur on 01/June/2018
 */

public class DeliveryMethod implements Parcelable {

    public static final Creator<DeliveryMethod> CREATOR = new Creator<DeliveryMethod>() {
        @Override
        public DeliveryMethod createFromParcel(Parcel source) {
            return new DeliveryMethod(source);
        }

        @Override
        public DeliveryMethod[] newArray(int size) {
            return new DeliveryMethod[size];
        }
    };
    private String name;
    private String target;

    public DeliveryMethod() {
    }

    protected DeliveryMethod(Parcel in) {
        this.name = in.readString();
        this.target = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.target);
    }

    @Override
    public String toString() {
        return "DeliveryMethod{" +
                "name='" + name + '\'' +
                ", target='" + target + '\'' +
                '}';
    }
}