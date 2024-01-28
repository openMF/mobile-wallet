package org.mifos.mobilewallet.core.domain.model.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by naman on 16/6/17.
 */

public class User implements Parcelable {
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    private long userId;
    private String username;
    private String authenticationKey;

    public User() {
    }

    protected User(Parcel in) {
        this.userId = in.readLong();
        this.username = in.readString();
        this.authenticationKey = in.readString();
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public void setAuthenticationKey(String authenticationKey) {
        this.authenticationKey = authenticationKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.userId);
        dest.writeString(this.username);
        dest.writeString(this.authenticationKey);
    }
}
