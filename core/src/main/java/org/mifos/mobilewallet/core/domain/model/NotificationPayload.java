package org.mifos.mobilewallet.core.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ankur on 24/July/2018
 */

public class NotificationPayload implements Parcelable {

    public static final Creator<NotificationPayload> CREATOR = new Creator<NotificationPayload>() {
        @Override
        public NotificationPayload createFromParcel(Parcel in) {
            return new NotificationPayload(in);
        }

        @Override
        public NotificationPayload[] newArray(int size) {
            return new NotificationPayload[size];
        }
    };
    private String title;
    private String body;
    private String timestamp;

    protected NotificationPayload(Parcel in) {
        title = in.readString();
        body = in.readString();
        timestamp = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "NotificationPayload{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(body);
        dest.writeString(timestamp);
    }
}
