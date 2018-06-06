package org.mifos.mobilewallet.core.data.fineract.entity.client;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.mifos.mobilewallet.core.data.fineract.entity.Timeline;

import java.util.ArrayList;
import java.util.List;

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
    @SerializedName("id")
    private int id;
    @SerializedName("accountNo")
    private String accountNo;
    @SerializedName("status")
    private Status status;
    @SerializedName("active")
    private Boolean active;
    @SerializedName("activationDate")
    private List<Integer> activationDate = new ArrayList<>();
    @SerializedName("dobDate")
    private List<Integer> dobDate = new ArrayList<>();
    @SerializedName("firstname")
    private String firstname;
    @SerializedName("middlename")
    private String middlename;
    @SerializedName("lastname")
    private String lastname;
    @SerializedName("displayName")
    private String displayName;
    @SerializedName("fullname")
    private String fullname;
    @SerializedName("officeId")
    private Integer officeId;
    @SerializedName("officeName")
    private String officeName;
    @SerializedName("staffId")
    private Integer staffId;
    @SerializedName("staffName")
    private String staffName;
    @SerializedName("timeline")
    private Timeline timeline;
    @SerializedName("imageId")
    private int imageId;
    @SerializedName("imagePresent")
    private boolean imagePresent;
    @SerializedName("externalId")
    private String externalId;
    @SerializedName("mobileNo")
    private String mobileNo;

    public Client() {
    }

    protected Client(Parcel in) {
        this.id = in.readInt();
        this.accountNo = in.readString();
        this.status = in.readParcelable(Status.class.getClassLoader());
        this.active = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.activationDate = new ArrayList<Integer>();
        in.readList(this.activationDate, Integer.class.getClassLoader());
        this.dobDate = new ArrayList<Integer>();
        in.readList(this.dobDate, Integer.class.getClassLoader());
        this.firstname = in.readString();
        this.middlename = in.readString();
        this.lastname = in.readString();
        this.displayName = in.readString();
        this.fullname = in.readString();
        this.officeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.officeName = in.readString();
        this.staffId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.staffName = in.readString();
        this.timeline = in.readParcelable(Timeline.class.getClassLoader());
        this.fullname = in.readString();
        this.imageId = in.readInt();
        this.imagePresent = in.readByte() != 0;
        this.externalId = in.readString();
        this.mobileNo = in.readString();
    }

    public List<Integer> getDobDate() {
        return dobDate;
    }

    public void setDobDate(List<Integer> dobDate) {
        this.dobDate = dobDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public List<Integer> getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(List<Integer> activationDate) {
        this.activationDate = activationDate;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public boolean isImagePresent() {
        return imagePresent;
    }

    public void setImagePresent(boolean imagePresent) {
        this.imagePresent = imagePresent;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public Integer getOfficeId() {
        return officeId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", accountNo='" + accountNo + '\'' +
                ", activationDate=" + activationDate +
                ", firstname='" + firstname + '\'' +
                ", middlename='" + middlename + '\'' +
                ", lastname='" + lastname + '\'' +
                ", displayName='" + displayName + '\'' +
                ", fullname='" + fullname + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.accountNo);
        dest.writeParcelable(this.status, flags);
        dest.writeValue(this.active);
        dest.writeList(this.activationDate);
        dest.writeList(this.dobDate);
        dest.writeString(this.firstname);
        dest.writeString(this.middlename);
        dest.writeString(this.lastname);
        dest.writeString(this.displayName);
        dest.writeString(this.fullname);
        dest.writeValue(this.officeId);
        dest.writeString(this.officeName);
        dest.writeValue(this.staffId);
        dest.writeString(this.staffName);
        dest.writeString(this.externalId);
        dest.writeString(this.mobileNo);
    }
}
