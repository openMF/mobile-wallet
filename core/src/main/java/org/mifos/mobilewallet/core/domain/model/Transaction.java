package org.mifos.mobilewallet.core.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.TransferDetail;

/**
 * Created by naman on 15/8/17.
 */

public class Transaction implements Parcelable {

    public static final Creator<Transaction> CREATOR = new
            Creator<Transaction>() {
                @Override
                public Transaction createFromParcel(Parcel source) {
                    return new Transaction(source);
                }

                @Override
                public Transaction[] newArray(int size) {
                    return new Transaction[size];
                }
            };
    String transactionId;
    long clientId;
    long accountId;
    double amount;
    String date;
    Currency currency;
    TransactionType transactionType;
    long transferId;
    TransferDetail transferDetail;
    String receiptId;

    protected Transaction(Parcel in) {
        this.transactionId = in.readString();
        this.clientId = in.readLong();
        this.accountId = in.readLong();
        this.amount = in.readDouble();
        this.date = in.readString();
        this.currency = in.readParcelable(Currency.class.getClassLoader());
        int tmpTransactionType = in.readInt();
        this.transactionType = tmpTransactionType == -1 ? null :
                TransactionType.values()[tmpTransactionType];
        this.transferId = in.readLong();
        this.transferDetail = in.readParcelable(TransferDetail.class.getClassLoader());
        this.receiptId = in.readString();
    }

    public Transaction() {
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public long getTransferId() {
        return transferId;
    }

    public void setTransferId(long transferId) {
        this.transferId = transferId;
    }

    public TransferDetail getTransferDetail() {
        return transferDetail;
    }

    public void setTransferDetail(
            TransferDetail transferDetail) {
        this.transferDetail = transferDetail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.transactionId);
        dest.writeLong(this.clientId);
        dest.writeLong(this.accountId);
        dest.writeDouble(this.amount);
        dest.writeString(this.date);
        dest.writeParcelable(this.currency, flags);
        dest.writeInt(this.transactionType == null ? -1 : this.transactionType.ordinal());
        dest.writeLong(this.transferId);
        dest.writeParcelable(this.transferDetail, flags);
        dest.writeString(this.receiptId);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", transferId=" + transferId +
                ", transferDetail=" + transferDetail +
                ", receiptId=" + receiptId +
                '}';
    }

    private static String input;
    private static String[] units=
            {"",
                    " One",
                    " Two",
                    " Three",
                    " Four",
                    " Five",
                    " Six",
                    " Seven",
                    " Eight",
                    " Nine"
            };
    private static String[] teen=
            {" Ten",
                    " Eleven",
                    " Twelve",
                    " Thirteen",
                    " Fourteen",
                    " Fifteen",
                    " Sixteen",
                    " Seventeen",
                    " Eighteen",
                    " Nineteen"
            };
    private static String[] tens=
            { " Twenty",
                    " Thirty",
                    " Forty",
                    " Fifty",
                    " Sixty",
                    " Seventy",
                    " Eighty",
                    " Ninety"
            };
    private static String[] maxs=
            {"",
                    "",
                    " Hundred",
                    " Thousand",
                    " Lakh",
                    " Crore"
            };


    public String convertNumberToWords(int n) {
        input=numToString(n);
        String converted="";
        int pos=1;
        boolean hun=false;
        while(input.length()> 0) {
            if(pos==1) {
                if(input.length()>= 2) {
                    String temp=input.substring(input.length()-2,input.length());
                    input=input.substring(0,input.length()-2);
                    converted+=digits(temp);
                } else if(input.length()==1) {
                    converted+=digits(input);
                    input="";
            }
                pos++;
            } else if(pos==2) {
                String temp=input.substring(input.length()-1,input.length());
                input=input.substring(0,input.length()-1);
                if(converted.length()> 0&&digits(temp)!="") {
                    converted=(digits(temp)+maxs[pos]+" and")+converted;
                    hun=true;
                } else {
                    if(digits(temp)=="") {
                    } else {
                        converted = (digits(temp) + maxs[pos]) + converted;
                        hun = true;
                    }
                }
                pos++;
            } else if(pos > 2) {
                if(input.length()>= 2) {
                    String temp=input.substring(input.length()-2,input.length());
                    input=input.substring(0,input.length()-2);
                    if(!hun&&converted.length()> 0) {
                        converted = digits(temp) + maxs[pos] + " and" + converted;
                    } else {
                        if(digits(temp)=="")  {
                        } else {
                            converted = digits(temp) + maxs[pos] + converted;
                        }
                    }
                } else if(input.length()==1) {
                    if(!hun&&converted.length()> 0) {
                        converted = digits(input) + maxs[pos] + " and" + converted;
                    } else {
                        if(digits(input)=="") {
                        } else {
                            converted = digits(input) + maxs[pos] + converted;
                        }
                        input="";
                    }
                }
                pos++;
            }
        }
        return converted+" Only";
    }
    // TO RETURN SELECTED NUMBERS IN WORDS
    private String digits(String temp) {
        String converted="";
        for(int i=temp.length()-1;i >= 0;i--) {
            int ch=temp.charAt(i)-48;
            if(i==0&&ch>1 && temp.length()> 1) {
                converted = tens[ch - 2] + converted;
            } else if(i==0&&ch==1&&temp.length()==2) {
                int sum=0;
                for(int j=0;j < 2;j++)
                    sum=(sum*10)+(temp.charAt(j)-48);
                return teen[sum-10];
            } else {
                if(ch > 0)
                    converted=units[ch]+converted;
            }
        }
        return converted;
    }
    // CONVERT THE NUMBER TO STRING
    private String numToString(int x) {
        String num = "";
        while (x != 0) {
            num = ((char) ((x % 10) + 48)) + num;
            x /= 10;
        }
        return num;
    }
    /**
     * Return amount into words
     */
    public String getAmountInWords(int amount) {
        return convertNumberToWords(amount);
    }
}
