package org.mifos.mobilewallet.core.data.fineract.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchedEntity implements Parcelable {

    public static final Creator<SearchedEntity> CREATOR = new Creator<SearchedEntity>() {
        @Override
        public SearchedEntity createFromParcel(Parcel source) {
            return new SearchedEntity(source);
        }

        @Override
        public SearchedEntity[] newArray(int size) {
            return new SearchedEntity[size];
        }
    };
    private int entityId;
    private String entityAccountNo;
    private String entityName;
    private String entityType;
    private int parentId;
    private String parentName;

    public SearchedEntity() {
    }

    protected SearchedEntity(Parcel in) {
        this.entityId = in.readInt();
        this.entityAccountNo = in.readString();
        this.entityName = in.readString();
        this.entityType = in.readString();
        this.parentId = in.readInt();
        this.parentName = in.readString();
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public SearchedEntity withEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public String getEntityAccountNo() {
        return entityAccountNo;
    }

    public void setEntityAccountNo(String entityAccountNo) {
        this.entityAccountNo = entityAccountNo;
    }

    public SearchedEntity withEntityAccountNo(String entityAccountNo) {
        this.entityAccountNo = entityAccountNo;
        return this;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public SearchedEntity withEntityName(String entityName) {
        this.entityName = entityName;
        return this;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public SearchedEntity withEntityType(String entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public SearchedEntity withParentId(int parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public SearchedEntity withParentName(String parentName) {
        this.parentName = parentName;
        return this;
    }

    public String getDescription() {
        return "#" + getEntityId() + " - " + getEntityName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.entityId);
        dest.writeString(this.entityAccountNo);
        dest.writeString(this.entityName);
        dest.writeString(this.entityType);
        dest.writeInt(this.parentId);
        dest.writeString(this.parentName);
    }
}