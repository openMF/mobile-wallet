/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package org.mifos.mobilewallet.core.data.fineract.entity.noncore;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ishankhanna on 02/07/14.
 */
public class Document implements Parcelable {

    public static final Creator<Document> CREATOR = new Creator<Document>() {
        @Override
        public Document createFromParcel(Parcel source) {
            return new Document(source);
        }

        @Override
        public Document[] newArray(int size) {
            return new Document[size];
        }
    };
    private int id;
    private String parentEntityType;
    private int parentEntityId;
    private String name;
    private String fileName;
    private long size;
    private String type;
    private String description;

    public Document() {
    }

    protected Document(Parcel in) {
        this.id = in.readInt();
        this.parentEntityType = in.readString();
        this.parentEntityId = in.readInt();
        this.name = in.readString();
        this.fileName = in.readString();
        this.size = in.readLong();
        this.type = in.readString();
        this.description = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParentEntityType() {
        return parentEntityType;
    }

    public void setParentEntityType(String parentEntityType) {
        this.parentEntityType = parentEntityType;
    }

    public int getParentEntityId() {
        return parentEntityId;
    }

    public void setParentEntityId(int parentEntityId) {
        this.parentEntityId = parentEntityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        dest.writeInt(this.id);
        dest.writeString(this.parentEntityType);
        dest.writeInt(this.parentEntityId);
        dest.writeString(this.name);
        dest.writeString(this.fileName);
        dest.writeLong(this.size);
        dest.writeString(this.type);
        dest.writeString(this.description);
    }
}
