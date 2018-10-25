package org.mifos.mobilewallet.core.data.fineract.entity.templates.beneficiary;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dilpreet on 14/6/17.
 */

public class BeneficiaryTemplate implements Parcelable {

    public static final Creator<BeneficiaryTemplate> CREATOR =
            new Creator<BeneficiaryTemplate>() {
                @Override
                public BeneficiaryTemplate createFromParcel(Parcel source) {
                    return new BeneficiaryTemplate(source);
                }

                @Override
                public BeneficiaryTemplate[] newArray(int size) {
                    return new BeneficiaryTemplate[size];
                }
            };
    @SerializedName("accountTypeOptions")
    private List<AccountTypeOption> accountTypeOptions = null;

    public BeneficiaryTemplate() {
    }

    protected BeneficiaryTemplate(Parcel in) {
        this.accountTypeOptions = new ArrayList<AccountTypeOption>();
        in.readList(this.accountTypeOptions, AccountTypeOption.class.getClassLoader());
    }

    public List<AccountTypeOption> getAccountTypeOptions() {
        return accountTypeOptions;
    }

    public void setAccountTypeOptions(List<AccountTypeOption> accountTypeOptions) {
        this.accountTypeOptions = accountTypeOptions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.accountTypeOptions);
    }
}
