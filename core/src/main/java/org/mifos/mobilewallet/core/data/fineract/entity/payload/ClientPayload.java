package org.mifos.mobilewallet.core.data.fineract.entity.payload;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ClientPayload implements Parcelable {

    public static final Creator<ClientPayload> CREATOR =
            new Creator<ClientPayload>() {
                @Override
                public ClientPayload createFromParcel(Parcel source) {
                    return new ClientPayload(source);
                }

                @Override
                public ClientPayload[] newArray(int size) {
                    return new ClientPayload[size];
                }
            };
    public static final String DD_MMMM_YYYY = "dd MMMM YYYY";
    @SerializedName("firstname")
    String firstname;
    @SerializedName("lastname")
    String lastname;
    @SerializedName("middlename")
    String middlename;
    @SerializedName("officeId")
    Integer officeId;
    @SerializedName("staffId")
    Integer staffId;
    @SerializedName("genderId")
    Integer genderId;
    @SerializedName("active")
    Boolean active;
    @SerializedName("activationDate")
    String activationDate;
    @SerializedName("submittedOnDate")
    String submittedOnDate;
    @SerializedName("dateOfBirth")
    String dateOfBirth;
    @SerializedName("mobileNo")
    String mobileNo;
    @SerializedName("externalId")
    String externalId;
    @SerializedName("clientTypeId")
    Integer clientTypeId;
    @SerializedName("clientClassificationId")
    Integer clientClassificationId;
    @SerializedName("dateFormat")
    String dateFormat = DD_MMMM_YYYY;
    @SerializedName("locale")
    String locale = "en";
    @SerializedName("datatables")
    List<DataTablePayload> datatables = new ArrayList<>();

    public ClientPayload() {
    }

    protected ClientPayload(Parcel in) {
        this.firstname = in.readString();
        this.lastname = in.readString();
        this.middlename = in.readString();
        this.officeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.staffId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.genderId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.active = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.activationDate = in.readString();
        this.submittedOnDate = in.readString();
        this.dateOfBirth = in.readString();
        this.mobileNo = in.readString();
        this.externalId = in.readString();
        this.clientTypeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.clientClassificationId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.dateFormat = in.readString();
        this.locale = in.readString();
    }

    public List<DataTablePayload> getDatatables() {
        return datatables;
    }

    public void setDatatables(List<DataTablePayload> datatables) {
        this.datatables = datatables;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public Integer getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Integer officeId) {
        this.officeId = officeId;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public Integer getGenderId() {
        return genderId;
    }

    public void setGenderId(Integer genderId) {
        this.genderId = genderId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
    }

    public String getSubmittedOnDate() {
        return submittedOnDate;
    }

    public void setSubmittedOnDate(String submittedOnDate) {
        this.submittedOnDate = submittedOnDate;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Integer getClientTypeId() {
        return clientTypeId;
    }

    public void setClientTypeId(Integer clientTypeId) {
        this.clientTypeId = clientTypeId;
    }

    public Integer getClientClassificationId() {
        return clientClassificationId;
    }

    public void setClientClassificationId(Integer clientClassificationId) {
        this.clientClassificationId = clientClassificationId;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public String toString() {
        return "ClientPayload{" +
                " firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", middlename='" + middlename + '\'' +
                ", officeId=" + officeId +
                ", staffId=" + staffId +
                ", genderId=" + genderId +
                ", active=" + active +
                ", activationDate='" + activationDate + '\'' +
                ", submittedOnDate='" + submittedOnDate + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", externalId='" + externalId + '\'' +
                ", clientTypeId=" + clientTypeId +
                ", clientClassificationId=" + clientClassificationId +
                ", dateFormat='" + dateFormat + '\'' +
                ", locale='" + locale + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstname);
        dest.writeString(this.lastname);
        dest.writeString(this.middlename);
        dest.writeValue(this.officeId);
        dest.writeValue(this.staffId);
        dest.writeValue(this.genderId);
        dest.writeValue(this.active);
        dest.writeString(this.activationDate);
        dest.writeString(this.submittedOnDate);
        dest.writeString(this.dateOfBirth);
        dest.writeString(this.mobileNo);
        dest.writeString(this.externalId);
        dest.writeValue(this.clientTypeId);
        dest.writeValue(this.clientClassificationId);
        dest.writeString(this.dateFormat);
        dest.writeString(this.locale);
    }
}

