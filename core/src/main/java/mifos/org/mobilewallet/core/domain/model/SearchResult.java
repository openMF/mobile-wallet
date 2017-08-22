package mifos.org.mobilewallet.core.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by naman on 19/8/17.
 */

public class SearchResult implements Parcelable {

    private int resultId;
    private String resultName;
    private String resultType;

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.resultId);
        dest.writeString(this.resultName);
        dest.writeString(this.resultType);
    }

    public SearchResult() {
    }

    protected SearchResult(Parcel in) {
        this.resultId = in.readInt();
        this.resultName = in.readString();
        this.resultType = in.readString();
    }

    public static final Parcelable.Creator<SearchResult> CREATOR = new Parcelable.Creator<SearchResult>() {
        @Override
        public SearchResult createFromParcel(Parcel source) {
            return new SearchResult(source);
        }

        @Override
        public SearchResult[] newArray(int size) {
            return new SearchResult[size];
        }
    };
}
