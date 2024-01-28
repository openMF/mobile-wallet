package org.mifos.mobilewallet.core.data.fineract.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by ankur on 11/June/2018
 */

public class UserWithRole implements Parcelable {

    public static final Creator<UserWithRole> CREATOR = new Creator<UserWithRole>() {
        @Override
        public UserWithRole createFromParcel(Parcel in) {
            return new UserWithRole(in);
        }

        @Override
        public UserWithRole[] newArray(int size) {
            return new UserWithRole[size];
        }
    };
    private String id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private List<Role> selectedRoles;

    protected UserWithRole(Parcel in) {
        id = in.readString();
        username = in.readString();
        firstname = in.readString();
        lastname = in.readString();
        email = in.readString();
        selectedRoles = in.createTypedArrayList(Role.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getSelectedRoles() {
        return selectedRoles;
    }

    public void setSelectedRoles(
            List<Role> selectedRoles) {
        this.selectedRoles = selectedRoles;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(firstname);
        dest.writeString(lastname);
        dest.writeString(email);
        dest.writeTypedList(selectedRoles);
    }
}
