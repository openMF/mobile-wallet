package org.mifos.mobilewallet.core.data.fineract.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankur on 07/June/2018
 */

public class Invoice implements Parcelable {

    public static final Creator<Invoice> CREATOR = new Creator<Invoice>() {
        @Override
        public Invoice createFromParcel(Parcel in) {
            return new Invoice(in);
        }

        @Override
        public Invoice[] newArray(int size) {
            return new Invoice[size];
        }
    };
    @SerializedName("consumerId")
    String consumerId;
    @SerializedName("consumerName")
    String consumerName;
    @SerializedName("amount")
    double amount;
    @SerializedName("itemsBought")
    String itemsBought;
    @SerializedName("status")
    long status;
    @SerializedName("transactionId")
    String transactionId;
    @SerializedName("id")
    long id;
    @SerializedName("title")
    String title;
    @SerializedName("date")
    List<Integer> date = new ArrayList<>();

    public Invoice(String consumerId, String consumerName, double amount, String itemsBought,
            long status, String transactionId, long id, String title, List<Integer> date) {
        this.consumerId = consumerId;
        this.consumerName = consumerName;
        this.amount = amount;
        this.itemsBought = itemsBought;
        this.status = status;
        this.transactionId = transactionId;
        this.id = id;
        this.title = title;
        this.date = date;
    }

    protected Invoice(Parcel in) {
        this.consumerId = in.readString();
        this.consumerName = in.readString();
        this.amount = in.readLong();
        this.itemsBought = in.readString();
        this.status = in.readInt();
        this.transactionId = in.readString();
        this.id = in.readInt();
        this.title = in.readString();
        this.date = new ArrayList<Integer>();
        in.readList(this.date, Integer.class.getClassLoader());
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getItemsBought() {
        return itemsBought;
    }

    public void setItemsBought(String itemsBought) {
        this.itemsBought = itemsBought;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.consumerId);
        dest.writeString(this.consumerName);
        dest.writeDouble(this.amount);
        dest.writeString(this.itemsBought);
        dest.writeLong(this.status);
        dest.writeString(this.transactionId);
        dest.writeList(this.date);
        dest.writeString(this.title);
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "consumerId='" + consumerId + '\'' +
                ", consumerName='" + consumerName + '\'' +
                ", amount=" + amount +
                ", itemsBought='" + itemsBought + '\'' +
                ", status=" + status +
                ", transactionId='" + transactionId + '\'' +
                ", title=" + title +
                ", id=" + id +
                ", date=" + date +
                '}';
    }

    public List<Integer> getDate() {
        return date;
    }

    public void setDate(List<Integer> date) {
        this.date = date;
    }
}
